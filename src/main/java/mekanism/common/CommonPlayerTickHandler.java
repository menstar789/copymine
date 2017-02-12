package mekanism.common;

import mekanism.api.ObfuscatedNames;
import mekanism.api.gas.GasStack;
import mekanism.api.util.ReflectionUtils;
import mekanism.common.entity.EntityFlame;
import mekanism.common.item.ItemFlamethrower;
import mekanism.common.item.ItemFreeRunners;
import mekanism.common.item.ItemGasMask;
import mekanism.common.item.ItemJetpack;
import mekanism.common.item.ItemJetpack.JetpackMode;
import mekanism.common.item.ItemScubaTank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CommonPlayerTickHandler
{
	@SubscribeEvent
	public void onTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.END && event.side == Side.SERVER)
		{
			tickEnd(event.player);
		}
	}

	public void tickEnd(EntityPlayer player)
	{
		ItemStack feetStack = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		
		if(feetStack != null && feetStack.getItem() instanceof ItemFreeRunners)
		{
			player.stepHeight = 1.002F;
		}
		else {
			if(player.stepHeight == 1.002F)
			{
				player.stepHeight = 0.5F;
			}
		}
		
		if(isFlamethrowerOn(player))
		{
			player.worldObj.spawnEntityInWorld(new EntityFlame(player));
			
			if(!player.capabilities.isCreativeMode)
			{
				((ItemFlamethrower)player.inventory.getCurrentItem().getItem()).useGas(player.inventory.getCurrentItem());
			}
		}

		if(isJetpackOn(player))
		{
			ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			ItemJetpack jetpack = (ItemJetpack)stack.getItem();

			if(jetpack.getMode(stack) == JetpackMode.NORMAL)
			{
				player.motionY = Math.min(player.motionY + 0.15D, 0.5D);
			}
			else if(jetpack.getMode(stack) == JetpackMode.HOVER)
			{
				if((!Mekanism.keyMap.has(player, KeySync.ASCEND) && !Mekanism.keyMap.has(player, KeySync.DESCEND)) || (Mekanism.keyMap.has(player, KeySync.ASCEND) && Mekanism.keyMap.has(player, KeySync.DESCEND)))
				{
					if(player.motionY > 0)
					{
						player.motionY = Math.max(player.motionY - 0.15D, 0);
					}
					else if(player.motionY < 0)
					{
						if(!isOnGround(player))
						{
							player.motionY = Math.min(player.motionY + 0.15D, 0);
						}
					}
				}
				else {
					if(Mekanism.keyMap.has(player, KeySync.ASCEND))
					{
						player.motionY = Math.min(player.motionY + 0.15D, 0.2D);
					}
					else if(Mekanism.keyMap.has(player, KeySync.DESCEND))
					{
						if(!isOnGround(player))
						{
							player.motionY = Math.max(player.motionY - 0.15D, -0.2D);
						}
					}
				}
			}

			player.fallDistance = 0.0F;

			if(player instanceof EntityPlayerMP)
			{
				ReflectionUtils.setPrivateValue(((EntityPlayerMP)player).connection, 0, NetHandlerPlayServer.class, ObfuscatedNames.NetHandlerPlayServer_floatingTickCount);
			}

			jetpack.useGas(stack);
		}

		if(isGasMaskOn(player))
		{
			ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			ItemScubaTank tank = (ItemScubaTank)stack.getItem();

			final int max = 300;
			
			tank.useGas(stack);
			GasStack received = tank.useGas(stack, max-player.getAir());
			
			if(received != null)
			{
				player.setAir(player.getAir()+received.amount);
			}
			
			if(player.getAir() == max)
			{
				for(Object obj : player.getActivePotionEffects())
				{
					if(obj instanceof PotionEffect)
					{
						for(int i = 0; i < 9; i++)
						{
							((PotionEffect)obj).onUpdate(player);
						}
					}
				}
			}
		}
	}
	
	public static boolean isOnGround(EntityPlayer player)
	{
		int x = MathHelper.floor_double(player.posX);
		int y = (int)Math.round(player.posY - 1);
		int z = MathHelper.floor_double(player.posZ);

		BlockPos pos = new BlockPos(x, y, z);
		IBlockState s = player.worldObj.getBlockState(pos);
		AxisAlignedBB box = s.getCollisionBoundingBox(player.worldObj, pos);
		AxisAlignedBB playerBox = player.getCollisionBoundingBox();
		
		return box != null && playerBox != null && playerBox.offset(0, -0.01, 0).intersectsWith(box);
	}

	public boolean isJetpackOn(EntityPlayer player)
	{
		ItemStack stack = player.inventory.armorInventory[2];

		if(stack != null && !player.capabilities.isCreativeMode)
		{
			if(stack.getItem() instanceof ItemJetpack)
			{
				ItemJetpack jetpack = (ItemJetpack)stack.getItem();

				if(jetpack.getGas(stack) != null)
				{
					if((Mekanism.keyMap.has(player, KeySync.ASCEND) && jetpack.getMode(stack) == JetpackMode.NORMAL))
					{
						return true;
					}
					else if(jetpack.getMode(stack) == JetpackMode.HOVER)
					{
						if((!Mekanism.keyMap.has(player, KeySync.ASCEND) && !Mekanism.keyMap.has(player, KeySync.DESCEND)) || (Mekanism.keyMap.has(player, KeySync.ASCEND) && Mekanism.keyMap.has(player, KeySync.DESCEND)))
						{
							return !player.onGround;
						}
						else if(Mekanism.keyMap.has(player, KeySync.DESCEND))
						{
							return !player.onGround;
						}
						
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean isGasMaskOn(EntityPlayer player)
	{
		ItemStack tank = player.inventory.armorInventory[2];
		ItemStack mask = player.inventory.armorInventory[3];

		if(tank != null && mask != null)
		{
			if(tank.getItem() instanceof ItemScubaTank && mask.getItem() instanceof ItemGasMask)
			{
				ItemScubaTank scubaTank = (ItemScubaTank)tank.getItem();

				if(scubaTank.getGas(tank) != null)
				{
					if(scubaTank.getFlowing(tank))
					{
						return true;
					}
				}
			}
		}

		return false;
	}
	
	public static boolean isFlamethrowerOn(EntityPlayer player)
	{
		if(Mekanism.flamethrowerActive.contains(player.getName()))
		{
			if(player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFlamethrower)
			{
				return true;
			}
		}
		
		return false;
	}
}
