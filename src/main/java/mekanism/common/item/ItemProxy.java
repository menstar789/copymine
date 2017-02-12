package mekanism.common.item;

import mekanism.common.util.ItemDataUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemProxy extends Item
{
	public ItemProxy()
	{
		super();
		setMaxDamage(1);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack)
	{
		return getSavedItem(stack);
	}

	@Override
	public boolean hasContainerItem(ItemStack itemStack)
	{
		return getSavedItem(itemStack) != null;
	}

	public void setSavedItem(ItemStack stack, ItemStack save)
	{
		if(save == null)
		{
			ItemDataUtils.setBoolean(stack, "hasStack", false);
			ItemDataUtils.removeData(stack, "savedItem");
		}
		else {
			ItemDataUtils.setBoolean(stack, "hasStack", true);
			ItemDataUtils.setCompound(stack, "savedItem", save.writeToNBT(new NBTTagCompound()));
		}
	}

	public ItemStack getSavedItem(ItemStack stack)
	{
		if(ItemDataUtils.getBoolean(stack, "hasStack"))
		{
			return ItemStack.loadItemStackFromNBT(ItemDataUtils.getCompound(stack, "savedItem"));
		}

		return null;
	}

	@Override
	public void onUpdate(ItemStack stacks, World world, Entity entity, int j, boolean flag)
	{
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			
			for(int i = 0; i < player.inventory.mainInventory.length; i++)
			{
				if(player.inventory.mainInventory[i] != null && player.inventory.mainInventory[i].getItem() == this)
				{
					player.inventory.mainInventory[i] = null;
				}
			}
		}
	}
}
