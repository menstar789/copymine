package mekanism.common.content.transporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.util.StackUtils;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.content.transporter.TransporterStack.Path;
import mekanism.common.tile.TileEntityBin;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

public class TransporterManager
{
	public static Map<Coord4D, Set<TransporterStack>> flowingStacks = new HashMap<Coord4D, Set<TransporterStack>>();
	
	public static void reset()
	{
		flowingStacks.clear();
	}

	public static void add(TransporterStack stack)
	{
		Set<TransporterStack> set = new HashSet<TransporterStack>();
		set.add(stack);
		
		if(flowingStacks.get(stack.getDest()) == null)
		{
			flowingStacks.put(stack.getDest(), set);
		}
		else {
			flowingStacks.get(stack.getDest()).addAll(set);
		}
	}

	public static void remove(TransporterStack stack)
	{
		if(stack.hasPath() && stack.pathType != Path.NONE)
		{
			flowingStacks.get(stack.getDest()).remove(stack);
		}
	}

	public static List<TransporterStack> getStacksToDest(Coord4D dest)
	{
		List<TransporterStack> ret = new ArrayList<TransporterStack>();

		if(flowingStacks.containsKey(dest))
		{
			for(TransporterStack stack : flowingStacks.get(dest))
			{
				if(stack != null && stack.pathType != Path.NONE && stack.hasPath())
				{
					if(stack.getDest().equals(dest))
					{
						ret.add(stack);
					}
				}
			}
		}

		return ret;
	}

	public static InventoryCopy copyInvFromSide(IInventory inv, EnumFacing side)
	{
		inv = InventoryUtils.checkChestInv(inv);

		ItemStack[] ret = new ItemStack[inv.getSizeInventory()];

		if(!(inv instanceof ISidedInventory))
		{
			for(int i = 0; i <= inv.getSizeInventory() - 1; i++)
			{
				ret[i] = inv.getStackInSlot(i) != null ? inv.getStackInSlot(i).copy() : null;
			}
		}
		else {
			ISidedInventory sidedInventory = (ISidedInventory)inv;
			int[] slots = sidedInventory.getSlotsForFace(side.getOpposite());

			if(slots == null || slots.length == 0)
			{
				return null;
			}

			for(int get = 0; get <= slots.length - 1; get++)
			{
				int slotID = slots[get];

				ret[slotID] = sidedInventory.getStackInSlot(slotID) != null ? sidedInventory.getStackInSlot(slotID).copy() : null;
			}
			
			if(inv instanceof TileEntityBin)
			{
				return new InventoryCopy(ret, ((TileEntityBin)inv).getItemCount());
			}
			else {
				return new InventoryCopy(ret);
			}
		}

		return new InventoryCopy(ret);
	}

	public static void testInsert(IInventory inv, InventoryCopy copy, EnumFacing side, TransporterStack stack)
	{
		ItemStack toInsert = stack.itemStack.copy();

		if(stack.pathType != Path.HOME && inv instanceof ISideConfiguration)
		{
			ISideConfiguration config = (ISideConfiguration)inv;
			EnumFacing tileSide = config.getOrientation();
			EnumColor configColor = config.getEjector().getInputColor(MekanismUtils.getBaseOrientation(side, tileSide).getOpposite());

			if(config.getEjector().hasStrictInput() && configColor != null && configColor != stack.color)
			{
				return;
			}
		}
		
		if(Loader.isModLoaded("MinefactoryReloaded") && inv instanceof IDeepStorageUnit && !(inv instanceof TileEntityBin))
		{
			return;
		}

		if(!(inv instanceof ISidedInventory))
		{
			for(int i = 0; i <= inv.getSizeInventory() - 1; i++)
			{
				if(stack.pathType != Path.HOME)
				{
					if(!inv.isItemValidForSlot(i, toInsert))
					{
						continue;
					}
				}

				ItemStack inSlot = copy.inventory[i];

				if(inSlot == null)
				{
					if(toInsert.stackSize <= inv.getInventoryStackLimit())
					{
						copy.inventory[i] = toInsert;
						return;
					}
					else {
						int rejects = toInsert.stackSize - inv.getInventoryStackLimit();
						
						ItemStack toSet = toInsert.copy();
						toSet.stackSize = inv.getInventoryStackLimit();

						ItemStack remains = toInsert.copy();
						remains.stackSize = rejects;

						copy.inventory[i] = toSet;

						toInsert = remains;
					}
				}
				else if(InventoryUtils.areItemsStackable(toInsert, inSlot) && inSlot.stackSize < Math.min(inSlot.getMaxStackSize(), inv.getInventoryStackLimit()))
				{
					int max = Math.min(inSlot.getMaxStackSize(), inv.getInventoryStackLimit());
					
					if(inSlot.stackSize + toInsert.stackSize <= max)
					{
						ItemStack toSet = toInsert.copy();
						toSet.stackSize += inSlot.stackSize;

						copy.inventory[i] = toSet;
						return;
					}
					else {
						int rejects = (inSlot.stackSize + toInsert.stackSize) - max;

						ItemStack toSet = toInsert.copy();
						toSet.stackSize = max;

						ItemStack remains = toInsert.copy();
						remains.stackSize = rejects;

						copy.inventory[i] = toSet;

						toInsert = remains;
					}
				}
			}
		}
		else {
			ISidedInventory sidedInventory = (ISidedInventory)inv;
			int[] slots = sidedInventory.getSlotsForFace(side.getOpposite());

			if(slots != null && slots.length != 0)
			{
				if(stack.pathType != Path.HOME && sidedInventory instanceof TileEntityBin && side.getOpposite() == EnumFacing.DOWN)
				{
					slots = sidedInventory.getSlotsForFace(EnumFacing.UP);
				}

				if(inv instanceof TileEntityBin)
				{
					int slot = slots[0];
					
					if(!sidedInventory.isItemValidForSlot(slot, toInsert) || !sidedInventory.canInsertItem(slot, toInsert, side.getOpposite()))
					{
						return;
					}
					
					int amountRemaining = ((TileEntityBin)inv).getMaxStoredCount()-copy.binAmount;
					copy.binAmount += Math.min(amountRemaining, toInsert.stackSize);
					
					return;
				}
				else {
					for(int get = 0; get <= slots.length - 1; get++)
					{
						int slotID = slots[get];
	
						if(stack.pathType != Path.HOME)
						{
							if(!sidedInventory.isItemValidForSlot(slotID, toInsert) || !sidedInventory.canInsertItem(slotID, toInsert, side.getOpposite()))
							{
								continue;
							}
						}
	
						ItemStack inSlot = copy.inventory[slotID];
	
						if(inSlot == null)
						{
							if(toInsert.stackSize <= inv.getInventoryStackLimit())
							{
								copy.inventory[slotID] = toInsert;
								return;
							}
							else {
								int rejects = toInsert.stackSize - inv.getInventoryStackLimit();
								
								ItemStack toSet = toInsert.copy();
								toSet.stackSize = inv.getInventoryStackLimit();

								ItemStack remains = toInsert.copy();
								remains.stackSize = rejects;

								copy.inventory[slotID] = toSet;

								toInsert = remains;
							}
						}
						else if(InventoryUtils.areItemsStackable(toInsert, inSlot) && inSlot.stackSize < Math.min(inSlot.getMaxStackSize(), inv.getInventoryStackLimit()))
						{
							int max = Math.min(inSlot.getMaxStackSize(), inv.getInventoryStackLimit());
							
							if(inSlot.stackSize + toInsert.stackSize <= max)
							{
								ItemStack toSet = toInsert.copy();
								toSet.stackSize += inSlot.stackSize;
	
								copy.inventory[slotID] = toSet;
								return;
							}
							else {
								int rejects = (inSlot.stackSize + toInsert.stackSize) - max;
	
								ItemStack toSet = toInsert.copy();
								toSet.stackSize = max;
	
								ItemStack remains = toInsert.copy();
								remains.stackSize = rejects;
	
								copy.inventory[slotID] = toSet;
	
								toInsert = remains;
							}
						}
					}
				}
			}
		}
	}

	public static boolean didEmit(ItemStack stack, ItemStack returned)
	{
		return returned == null || returned.stackSize < stack.stackSize;
	}

	public static ItemStack getToUse(ItemStack stack, ItemStack returned)
	{
		if(returned == null || returned.stackSize == 0)
		{
			return stack;
		}

		return MekanismUtils.size(stack, stack.stackSize-returned.stackSize);
	}

	/**
	 * @return rejects
	 */
	public static ItemStack getPredictedInsert(TileEntity tileEntity, EnumColor color, ItemStack itemStack, EnumFacing side)
	{
		if(!(tileEntity instanceof IInventory))
		{
			return itemStack;
		}

		if(tileEntity instanceof ISideConfiguration)
		{
			ISideConfiguration config = (ISideConfiguration)tileEntity;
			EnumFacing tileSide = config.getOrientation();
			EnumColor configColor = config.getEjector().getInputColor(MekanismUtils.getBaseOrientation(side, tileSide).getOpposite());

			if(config.getEjector().hasStrictInput() && configColor != null && configColor != color)
			{
				return itemStack;
			}
		}

		IInventory inventory = (IInventory)tileEntity;
		InventoryCopy copy = copyInvFromSide(inventory, side);

		if(copy == null)
		{
			return itemStack;
		}

		List<TransporterStack> insertQueue = getStacksToDest(Coord4D.get(tileEntity));

		for(TransporterStack tStack : insertQueue)
		{
			testInsert(inventory, copy, side, tStack);
		}

		ItemStack toInsert = itemStack.copy();

		if(!(inventory instanceof ISidedInventory))
		{
			inventory = InventoryUtils.checkChestInv(inventory);

			for(int i = 0; i <= inventory.getSizeInventory() - 1; i++)
			{
				if(!inventory.isItemValidForSlot(i, toInsert))
				{
					continue;
				}

				ItemStack inSlot = copy.inventory[i];

				if(toInsert == null)
				{
					return null;
				}
				else if(inSlot == null)
				{
					if(toInsert.stackSize <= inventory.getInventoryStackLimit())
					{
						return null;
					}
					else {
						int rejects = toInsert.stackSize - inventory.getInventoryStackLimit();
						
						if(rejects < toInsert.stackSize)
						{
							toInsert = StackUtils.size(toInsert, rejects);
						}
					}
				}
				else if(InventoryUtils.areItemsStackable(toInsert, inSlot) && inSlot.stackSize < Math.min(inSlot.getMaxStackSize(), inventory.getInventoryStackLimit()))
				{
					int max = Math.min(inSlot.getMaxStackSize(), inventory.getInventoryStackLimit());
					
					if(inSlot.stackSize + toInsert.stackSize <= max)
					{
						return null;
					}
					else {
						int rejects = (inSlot.stackSize + toInsert.stackSize) - max;

						if(rejects < toInsert.stackSize)
						{
							toInsert = StackUtils.size(toInsert, rejects);
						}
					}
				}
			}
		}
		else {
			ISidedInventory sidedInventory = (ISidedInventory)inventory;
			int[] slots = sidedInventory.getSlotsForFace(side.getOpposite());

			if(slots != null && slots.length != 0)
			{
				if(inventory instanceof TileEntityBin)
				{
					int slot = slots[0];
					
					if(!sidedInventory.isItemValidForSlot(slot, toInsert) || !sidedInventory.canInsertItem(slot, toInsert, side.getOpposite()))
					{
						return toInsert;
					}
					
					int amountRemaining = ((TileEntityBin)inventory).getMaxStoredCount()-copy.binAmount;
					
					if(toInsert.stackSize <= amountRemaining)
					{
						return null;
					}
					else {
						return StackUtils.size(toInsert, toInsert.stackSize-amountRemaining);
					}
				}
				else {
					for(int get = 0; get <= slots.length - 1; get++)
					{
						int slotID = slots[get];
	
						if(!sidedInventory.isItemValidForSlot(slotID, toInsert) || !sidedInventory.canInsertItem(slotID, toInsert, side.getOpposite()))
						{
							continue;
						}
	
						ItemStack inSlot = copy.inventory[slotID];
						
						if(toInsert == null)
						{
							return null;
						}
						else if(inSlot == null)
						{
							if(toInsert.stackSize <= inventory.getInventoryStackLimit())
							{
								return null;
							}
							else {
								int rejects = toInsert.stackSize - inventory.getInventoryStackLimit();
								
								if(rejects < toInsert.stackSize)
								{
									toInsert = StackUtils.size(toInsert, rejects);
								}
							}
						}
						else if(InventoryUtils.areItemsStackable(toInsert, inSlot) && inSlot.stackSize < Math.min(inSlot.getMaxStackSize(), inventory.getInventoryStackLimit()))
						{
							int max = Math.min(inSlot.getMaxStackSize(), inventory.getInventoryStackLimit());
							
							if(inSlot.stackSize + toInsert.stackSize <= max)
							{
								return null;
							}
							else {
								int rejects = (inSlot.stackSize + toInsert.stackSize) - max;
	
								if(rejects < toInsert.stackSize)
								{
									toInsert = StackUtils.size(toInsert, rejects);
								}
							}
						}
					}
				}
			}
		}

		return toInsert;
	}
	
	public static class InventoryCopy
	{
		public ItemStack[] inventory;
		
		public int binAmount;
		
		public InventoryCopy(ItemStack[] inv)
		{
			inventory = inv;
		}
		
		public InventoryCopy(ItemStack[] inv, int amount)
		{
			this(inv);
			binAmount = amount;
		}
	}
}
