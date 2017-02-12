package mekanism.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.EnumSet;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.Range4D;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.SideData;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Tier.EnergyCubeTier;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ITierUpgradeable;
import mekanism.common.integration.IComputerIntegration;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.util.CableUtils;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityEnergyCube extends TileEntityElectricBlock implements IComputerIntegration, IRedstoneControl, ISideConfiguration, ISecurityTile, ITierUpgradeable
{
	/** This Energy Cube's tier. */
	public EnergyCubeTier tier = EnergyCubeTier.BASIC;

	/** The redstone level this Energy Cube is outputting at. */
	public int currentRedstoneLevel;

	/** This machine's current RedstoneControl type. */
	public RedstoneControl controlType;

	public int prevScale;
	
	public TileComponentEjector ejectorComponent;
	public TileComponentConfig configComponent;
	public TileComponentSecurity securityComponent;

	/**
	 * A block used to store and transfer electricity.
	 */
	public TileEntityEnergyCube()
	{
		super("EnergyCube", 0);
		
		configComponent = new TileComponentConfig(this, TransmissionType.ENERGY, TransmissionType.ITEM);
		
		configComponent.addOutput(TransmissionType.ITEM, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Charge", EnumColor.DARK_BLUE, new int[] {0}));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Discharge", EnumColor.DARK_RED, new int[] {1}));
		
		configComponent.setConfig(TransmissionType.ITEM, new byte[] {0, 0, 0, 0, 2, 1});
		configComponent.setCanEject(TransmissionType.ITEM, false);
		configComponent.setIOConfig(TransmissionType.ENERGY);
		configComponent.setEjecting(TransmissionType.ENERGY, true);

		inventory = new ItemStack[2];
		controlType = RedstoneControl.DISABLED;
		
		ejectorComponent = new TileComponentEjector(this);
		
		securityComponent = new TileComponentSecurity(this);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(!worldObj.isRemote)
		{
			ChargeUtils.charge(0, this);
			ChargeUtils.discharge(1, this);
	
			if(MekanismUtils.canFunction(this) && configComponent.isEjecting(TransmissionType.ENERGY))
			{
				CableUtils.emit(this);
			}
			
			int newScale = getScaledEnergyLevel(20);
	
			if(newScale != prevScale)
			{
				Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
			}
	
			prevScale = newScale;
		}
	}
	
	@Override
	public boolean upgrade(BaseTier upgradeTier)
	{
		if(upgradeTier.ordinal() != tier.ordinal()+1)
		{
			return false;
		}
		
		tier = EnergyCubeTier.values()[upgradeTier.ordinal()];
		
		Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
		markDirty();
		
		return true;
	}

	@Override
	public String getName()
	{
		return LangUtils.localize("tile.EnergyCube" + tier.getBaseTier().getSimpleName() + ".name");
	}

	@Override
	public double getMaxOutput()
	{
		if(tier == EnergyCubeTier.CREATIVE)
		{
			return Integer.MAX_VALUE;
		}
		
		return tier.output;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
	{
		if(slotID == 0)
		{
			return ChargeUtils.canBeCharged(itemstack);
		}
		else if(slotID == 1)
		{
			return ChargeUtils.canBeDischarged(itemstack);
		}

		return true;
	}

	@Override
	public EnumSet<EnumFacing> getConsumingSides()
	{
		return configComponent.getSidesForData(TransmissionType.ENERGY, facing, 1);
	}

	@Override
	public EnumSet<EnumFacing> getOutputtingSides()
	{
		return configComponent.getSidesForData(TransmissionType.ENERGY, facing, 2);
	}

	@Override
	public boolean canSetFacing(int side)
	{
		return true;
	}

	@Override
	public double getMaxEnergy()
	{
		return tier.maxEnergy;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return configComponent.getOutput(TransmissionType.ITEM, side, facing).availableSlots;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, EnumFacing side)
	{
		if(slotID == 1)
		{
			return ChargeUtils.canBeOutputted(itemstack, false);
		}
		else if(slotID == 0)
		{
			return ChargeUtils.canBeOutputted(itemstack, true);
		}

		return false;
	}

    private static final String[] methods = new String[] {"getEnergy", "getOutput", "getMaxEnergy", "getEnergyNeeded"};

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
				return new Object[] {tier.output};
			case 2:
				return new Object[] {getMaxEnergy()};
			case 3:
				return new Object[] {(getMaxEnergy()-getEnergy())};
			default:
				throw new NoSuchMethodException();
		}
	}

	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);
		
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{	
			EnergyCubeTier prevTier = tier;
			
			tier = EnergyCubeTier.values()[dataStream.readInt()];
			controlType = RedstoneControl.values()[dataStream.readInt()];
	
			if(prevTier != tier)
			{
				MekanismUtils.updateBlock(worldObj, getPos());
			}
		}
	}

	@Override
	public ArrayList<Object> getNetworkedData(ArrayList<Object> data)
	{
		super.getNetworkedData(data);

		data.add(tier.ordinal());
		data.add(controlType.ordinal());

		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTags)
	{
		super.readFromNBT(nbtTags);

		tier = EnergyCubeTier.values()[nbtTags.getInteger("tier")];
		controlType = RedstoneControl.values()[nbtTags.getInteger("controlType")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTags)
	{
		super.writeToNBT(nbtTags);

		nbtTags.setInteger("tier", tier.ordinal());
		nbtTags.setInteger("controlType", controlType.ordinal());
		
		return nbtTags;
	}

	@Override
	public void setEnergy(double energy)
	{
		if(tier == EnergyCubeTier.CREATIVE && energy != Double.MAX_VALUE)
		{
			return;
		}
		
		super.setEnergy(energy);

		int newRedstoneLevel = getRedstoneLevel();

		if(newRedstoneLevel != currentRedstoneLevel)
		{
			markDirty();
			currentRedstoneLevel = newRedstoneLevel;
		}
	}

	public int getRedstoneLevel()
	{
		double fractionFull = getEnergy()/getMaxEnergy();
		return MathHelper.floor_float((float)(fractionFull * 14.0F)) + (fractionFull > 0 ? 1 : 0);
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
	}

	@Override
	public boolean canPulse()
	{
		return false;
	}

	@Override
	public TileComponentEjector getEjector()
	{
		return ejectorComponent;
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
	public TileComponentSecurity getSecurity()
	{
		return securityComponent;
	}
}
