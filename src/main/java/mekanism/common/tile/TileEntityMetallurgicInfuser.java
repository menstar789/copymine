package mekanism.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.IConfigCardAccess;
import mekanism.api.MekanismConfig.general;
import mekanism.api.MekanismConfig.usage;
import mekanism.api.Range4D;
import mekanism.api.infuse.InfuseObject;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.InfuseStorage;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.PacketHandler;
import mekanism.common.SideData;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Upgrade;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ITierUpgradeable;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.block.states.BlockStateMachine;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.integration.IComputerIntegration;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.InfusionInput;
import mekanism.common.recipe.machines.MetallurgicInfuserRecipe;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityMetallurgicInfuser extends TileEntityNoisyElectricBlock implements IComputerIntegration, ISideConfiguration, IUpgradeTile, IRedstoneControl, IConfigCardAccess, ISecurityTile, ITierUpgradeable
{
	/** The maxiumum amount of infuse this machine can store. */
	public int MAX_INFUSE = 1000;

	/** How much energy this machine consumes per-tick. */
	public double BASE_ENERGY_PER_TICK = usage.metallurgicInfuserUsage;

	public double energyPerTick = BASE_ENERGY_PER_TICK;

	/** How many ticks it takes to run an operation. */
	public int BASE_TICKS_REQUIRED = 200;

	public int ticksRequired = BASE_TICKS_REQUIRED;

	/** The amount of infuse this machine has stored. */
	public InfuseStorage infuseStored = new InfuseStorage();

	/** How many ticks this machine has been operating for. */
	public int operatingTicks;

	/** Whether or not this machine is in it's active state. */
	public boolean isActive;

	/** The client's current active state. */
	public boolean clientActive;

	/** How many ticks must pass until this block's active state can sync with the client. */
	public int updateDelay;

	/** This machine's previous amount of energy. */
	public double prevEnergy;

	/** This machine's current RedstoneControl type. */
	public RedstoneControl controlType = RedstoneControl.DISABLED;

	public TileComponentUpgrade upgradeComponent;
	public TileComponentEjector ejectorComponent;
	public TileComponentConfig configComponent;
	public TileComponentSecurity securityComponent;

	public TileEntityMetallurgicInfuser()
	{
		super("machine.metalinfuser", "MetallurgicInfuser", BlockStateMachine.MachineType.METALLURGIC_INFUSER.baseEnergy);

		configComponent = new TileComponentConfig(this, TransmissionType.ITEM);
		
		configComponent.addOutput(TransmissionType.ITEM, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Input", EnumColor.DARK_RED, new int[] {2}));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Output", EnumColor.DARK_BLUE, new int[] {3}));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Energy", EnumColor.DARK_GREEN, new int[] {4}));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Infuse", EnumColor.PURPLE, new int[] {1}));
		
		configComponent.setConfig(TransmissionType.ITEM, new byte[] {4, 0, 0, 3, 1, 2});

		inventory = new ItemStack[5];
		
		upgradeComponent = new TileComponentUpgrade(this, 0);
		upgradeComponent.setSupported(Upgrade.MUFFLING);
		
		ejectorComponent = new TileComponentEjector(this);
		ejectorComponent.setOutputData(TransmissionType.ITEM, configComponent.getOutputs(TransmissionType.ITEM).get(2));
		
		securityComponent = new TileComponentSecurity(this);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(worldObj.isRemote && updateDelay > 0)
		{
			updateDelay--;

			if(updateDelay == 0 && clientActive != isActive)
			{
				isActive = clientActive;
				MekanismUtils.updateBlock(worldObj, getPos());
			}
		}

		if(!worldObj.isRemote)
		{
			if(updateDelay > 0)
			{
				updateDelay--;

				if(updateDelay == 0 && clientActive != isActive)
				{
					Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList<Object>())), new Range4D(Coord4D.get(this)));
				}
			}

			ChargeUtils.discharge(4, this);

			if(inventory[1] != null)
			{
				if(InfuseRegistry.getObject(inventory[1]) != null)
				{
					InfuseObject infuse = InfuseRegistry.getObject(inventory[1]);

					if(infuseStored.type == null || infuseStored.type == infuse.type)
					{
						if(infuseStored.amount + infuse.stored <= MAX_INFUSE)
						{
							infuseStored.amount += infuse.stored;
							infuseStored.type = infuse.type;
							inventory[1].stackSize--;

							if(inventory[1].stackSize <= 0)
							{
								inventory[1] = null;
							}
						}
					}
				}
			}

			MetallurgicInfuserRecipe recipe = RecipeHandler.getMetallurgicInfuserRecipe(getInput());

			if(canOperate(recipe) && MekanismUtils.canFunction(this) && getEnergy() >= energyPerTick)
			{
				setActive(true);
				setEnergy(getEnergy() - energyPerTick);

				if((operatingTicks + 1) < ticksRequired)
				{
					operatingTicks++;
				} 
				else {
					operate(recipe);
					operatingTicks = 0;
				}
			}
			else {
				if(prevEnergy >= getEnergy())
				{
					setActive(false);
				}
			}

			if(!canOperate(recipe))
			{
				operatingTicks = 0;
			}

			if(infuseStored.amount <= 0)
			{
				infuseStored.amount = 0;
				infuseStored.type = null;
			}

			prevEnergy = getEnergy();
		}
	}
	
	@Override
	public boolean upgrade(BaseTier upgradeTier)
	{
		if(upgradeTier != BaseTier.BASIC)
		{
			return false;
		}
		
		worldObj.setBlockToAir(getPos());
		worldObj.setBlockState(getPos(), MekanismBlocks.MachineBlock.getStateFromMeta(5), 3);
		
		TileEntityFactory factory = (TileEntityFactory)worldObj.getTileEntity(getPos());
		RecipeType type = RecipeType.INFUSING;
		
		//Basic
		factory.facing = facing;
		factory.clientFacing = clientFacing;
		factory.ticker = ticker;
		factory.redstone = redstone;
		factory.redstoneLastTick = redstoneLastTick;
		factory.doAutoSync = doAutoSync;
		
		//Electric
		factory.electricityStored = electricityStored;
		
		//Noisy
		factory.soundURL = soundURL;
		
		//Machine
		factory.progress[0] = operatingTicks;
		factory.clientActive = clientActive;
		factory.isActive = isActive;
		factory.updateDelay = updateDelay;
		factory.controlType = controlType;
		factory.prevEnergy = prevEnergy;
		factory.upgradeComponent.readFrom(upgradeComponent);
		factory.upgradeComponent.setUpgradeSlot(0);
		factory.ejectorComponent.readFrom(ejectorComponent);
		factory.ejectorComponent.setOutputData(TransmissionType.ITEM, factory.configComponent.getOutputs(TransmissionType.ITEM).get(2));
		factory.recipeType = type;
		factory.upgradeComponent.setSupported(Upgrade.GAS, type.fuelEnergyUpgrades());
		factory.securityComponent.readFrom(securityComponent);
		
		for(TransmissionType transmission : configComponent.transmissions)
		{
			factory.configComponent.setConfig(transmission, configComponent.getConfig(transmission));
			factory.configComponent.setEjecting(transmission, configComponent.isEjecting(transmission));
		}
		
		//Infuser
		factory.infuseStored = infuseStored;

		factory.inventory[5] = inventory[2];
		factory.inventory[1] = inventory[4];
		factory.inventory[5+3] = inventory[3];
		factory.inventory[0] = inventory[0];
		factory.inventory[4] = inventory[1];
		
		for(Upgrade upgrade : factory.upgradeComponent.getSupportedTypes())
		{
			factory.recalculateUpgradables(upgrade);
		}
		
		factory.upgraded = true;
		factory.markDirty();
		
		return true;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, EnumFacing side)
	{
		if(slotID == 4)
		{
			return ChargeUtils.canBeOutputted(itemstack, false);
		}
		else if(slotID == 3)
		{
			return true;
		}

		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
	{
		if(slotID == 3)
		{
			return false;
		}
		else if(slotID == 1)
		{
			return InfuseRegistry.getObject(itemstack) != null && (infuseStored.type == null || infuseStored.type == InfuseRegistry.getObject(itemstack).type);
		}
		else if(slotID == 0)
		{
			return itemstack.getItem() == MekanismItems.SpeedUpgrade || itemstack.getItem() == MekanismItems.EnergyUpgrade;
		}
		else if(slotID == 2)
		{
			if(infuseStored.type != null)
			{
				if(RecipeHandler.getMetallurgicInfuserRecipe(new InfusionInput(infuseStored, itemstack)) != null)
				{
					return true;
				}
			}
			else {
				for(Object obj : Recipe.METALLURGIC_INFUSER.get().keySet())
				{
					InfusionInput input = (InfusionInput)obj;
					
					if(input.inputStack.isItemEqual(itemstack))
					{
						return true;
					}
				}
			}
		}
		else if(slotID == 4)
		{
			return ChargeUtils.canBeDischarged(itemstack);
		}

		return false;
	}

	public InfusionInput getInput()
	{
		return new InfusionInput(infuseStored, inventory[2]);
	}

	public void operate(MetallurgicInfuserRecipe recipe)
	{
		recipe.output(inventory, 2, 3, infuseStored);

		markDirty();
		ejectorComponent.outputItems();
	}

	public boolean canOperate(MetallurgicInfuserRecipe recipe)
	{
		return recipe != null && recipe.canOperate(inventory, 2, 3, infuseStored);
	}

	public int getScaledInfuseLevel(int i)
	{
		return infuseStored.amount * i / MAX_INFUSE;
	}

	public double getScaledProgress()
	{
		return ((double)operatingTicks) / ((double)ticksRequired);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTags)
	{
		super.readFromNBT(nbtTags);

		clientActive = isActive = nbtTags.getBoolean("isActive");
		operatingTicks = nbtTags.getInteger("operatingTicks");
		infuseStored.amount = nbtTags.getInteger("infuseStored");
		controlType = RedstoneControl.values()[nbtTags.getInteger("controlType")];
		infuseStored.type = InfuseRegistry.get(nbtTags.getString("type"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTags)
	{
		super.writeToNBT(nbtTags);

		nbtTags.setBoolean("isActive", isActive);
		nbtTags.setInteger("operatingTicks", operatingTicks);
		nbtTags.setInteger("infuseStored", infuseStored.amount);
		nbtTags.setInteger("controlType", controlType.ordinal());

		if(infuseStored.type != null)
		{
			nbtTags.setString("type", infuseStored.type.name);
		}
		else {
			nbtTags.setString("type", "null");
		}

		nbtTags.setBoolean("sideDataStored", true);
		
		return nbtTags;
	}

	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
 		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			infuseStored.amount = dataStream.readInt();
			return;
		}

		super.handlePacketData(dataStream);

		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			clientActive = dataStream.readBoolean();
			operatingTicks = dataStream.readInt();
			infuseStored.amount = dataStream.readInt();
			controlType = RedstoneControl.values()[dataStream.readInt()];
			infuseStored.type = InfuseRegistry.get(PacketHandler.readString(dataStream));
	
			if(updateDelay == 0 && clientActive != isActive)
			{
				updateDelay = general.UPDATE_DELAY;
				isActive = clientActive;
				MekanismUtils.updateBlock(worldObj, getPos());
			}
		}
	}

	@Override
	public ArrayList<Object> getNetworkedData(ArrayList<Object> data)
	{
		super.getNetworkedData(data);

		data.add(isActive);
		data.add(operatingTicks);
		data.add(infuseStored.amount);
		data.add(controlType.ordinal());

		if(infuseStored.type != null)
		{
			data.add(infuseStored.type.name);
		}
		else {
			data.add("null");
		}

		return data;
	}

    private static final String[] methods = new String[] {"getEnergy", "getProgress", "facing", "canOperate", "getMaxEnergy", "getEnergyNeeded", "getInfuse", "getInfuseNeeded"};

	@Override
	public String[] getMethods()
	{
		return methods;
	}

	@Override
	public Object[] invoke(int method, Object[] arguments) throws Exception
	{
		switch(method)
		{
			case 0:
				return new Object[] {getEnergy()};
			case 1:
				return new Object[] {operatingTicks};
			case 2:
				return new Object[] {facing};
			case 3:
				return new Object[] {canOperate(RecipeHandler.getMetallurgicInfuserRecipe(getInput()))};
			case 4:
				return new Object[] {getMaxEnergy()};
			case 5:
				return new Object[] {getMaxEnergy()-getEnergy()};
			case 6:
				return new Object[] {infuseStored};
			case 7:
				return new Object[] {MAX_INFUSE-infuseStored.amount};
			default:
				throw new NoSuchMethodException();
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return configComponent.getOutput(TransmissionType.ITEM, side, facing).availableSlots;
	}

	@Override
	public boolean canSetFacing(int side)
	{
		return side != 0 && side != 1;
	}

	@Override
	public void setActive(boolean active)
	{
		isActive = active;

		if(clientActive != active && updateDelay == 0)
		{
			Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList<Object>())), new Range4D(Coord4D.get(this)));

			updateDelay = 10;
			clientActive = active;
		}
	}

	@Override
	public boolean getActive()
	{
		return isActive;
	}

	@Override
	public TileComponentConfig getConfig()
	{
		return configComponent;
	}

	@Override
	public EnumFacing getOrientation()
	{
		return facing;
	}

	@Override
	public boolean renderUpdate()
	{
		return false;
	}

	@Override
	public boolean lightUpdate()
	{
		return true;
	}

	@Override
	public RedstoneControl getControlType()
	{
		return controlType;
	}

	@Override
	public void setControlType(RedstoneControl type)
	{
		controlType = type;
		MekanismUtils.saveChunk(this);
	}

	@Override
	public boolean canPulse()
	{
		return false;
	}

	@Override
	public TileComponentUpgrade getComponent()
	{
		return upgradeComponent;
	}

	@Override
	public TileComponentEjector getEjector()
	{
		return ejectorComponent;
	}
	
	@Override
	public TileComponentSecurity getSecurity()
	{
		return securityComponent;
	}

	@Override
	public void recalculateUpgradables(Upgrade upgrade)
	{
		super.recalculateUpgradables(upgrade);

		switch(upgrade)
		{
			case SPEED:
				ticksRequired = MekanismUtils.getTicks(this, BASE_TICKS_REQUIRED);
			case ENERGY:
				energyPerTick = MekanismUtils.getEnergyPerTick(this, BASE_ENERGY_PER_TICK);
				maxEnergy = MekanismUtils.getMaxEnergy(this, BASE_MAX_ENERGY);
			default:
				break;
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing side)
	{
		return capability == Capabilities.CONFIG_CARD_CAPABILITY || super.hasCapability(capability, side);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing side)
	{
		if(capability == Capabilities.CONFIG_CARD_CAPABILITY)
		{
			return (T)this;
		}
		
		return super.getCapability(capability, side);
	}
}
