package mekanism.common.inventory.container;

import mekanism.common.entity.EntityRobit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.math.BlockPos;

public class ContainerRobitCrafting extends ContainerWorkbench
{
	public EntityRobit robit;
	
	public ContainerRobitCrafting(InventoryPlayer inventory, EntityRobit entity)
	{
		super(inventory, entity.worldObj, BlockPos.ORIGIN);
		robit = entity;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return !robit.isDead;
	}
}
