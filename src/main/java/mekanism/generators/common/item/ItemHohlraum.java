package mekanism.generators.common.item;

import java.util.List;

import mekanism.api.EnumColor;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import mekanism.common.item.ItemMekanism;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemHohlraum extends ItemMekanism implements IGasItem
{
	public static final int MAX_GAS = 10;
	public static final int TRANSFER_RATE = 1;
	
	public ItemHohlraum()
	{
		super();
		setMaxStackSize(1);
	}
	
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag)
	{
		GasStack gasStack = getGas(itemstack);

		if(gasStack == null)
		{
			list.add(LangUtils.localize("tooltip.noGas") + ".");
			list.add(EnumColor.DARK_RED + LangUtils.localize("tooltip.insufficientFuel"));
		}
		else {
			list.add(LangUtils.localize("tooltip.stored") + " " + gasStack.getGas().getLocalizedName() + ": " + gasStack.amount);
			
			if(gasStack.amount == getMaxGas(itemstack))
			{
				list.add(EnumColor.DARK_GREEN + LangUtils.localize("tooltip.readyForReaction") + "!");
			}
			else {
				list.add(EnumColor.DARK_RED + LangUtils.localize("tooltip.insufficientFuel"));
			}
		}
	}
	
	@Override
	public int getMaxGas(ItemStack itemstack)
	{
		return MAX_GAS;
	}

	@Override
	public int getRate(ItemStack itemstack)
	{
		return TRANSFER_RATE;
	}

	@Override
	public int addGas(ItemStack itemstack, GasStack stack)
	{
		if(getGas(itemstack) != null && getGas(itemstack).getGas() != stack.getGas())
		{
			return 0;
		}

		if(stack.getGas() != GasRegistry.getGas("fusionFuelDT"))
		{
			return 0;
		}

		int toUse = Math.min(getMaxGas(itemstack)-getStored(itemstack), Math.min(getRate(itemstack), stack.amount));
		setGas(itemstack, new GasStack(stack.getGas(), getStored(itemstack)+toUse));

		return toUse;
	}

	@Override
	public GasStack removeGas(ItemStack itemstack, int amount)
	{
		return null;
	}

	public int getStored(ItemStack itemstack)
	{
		return getGas(itemstack) != null ? getGas(itemstack).amount : 0;
	}

	@Override
	public boolean canReceiveGas(ItemStack itemstack, Gas type)
	{
		return type == GasRegistry.getGas("fusionFuelDT");
	}

	@Override
	public boolean canProvideGas(ItemStack itemstack, Gas type)
	{
		return false;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1D-((getGas(stack) != null ? (double)getGas(stack).amount : 0D)/(double)getMaxGas(stack));
	}

	@Override
	public GasStack getGas(ItemStack itemstack)
	{
		return GasStack.readFromNBT(ItemDataUtils.getCompound(itemstack, "stored"));
	}

	@Override
	public void setGas(ItemStack itemstack, GasStack stack)
	{
		if(stack == null || stack.amount == 0)
		{
			ItemDataUtils.removeData(itemstack, "stored");
		}
		else {
			int amount = Math.max(0, Math.min(stack.amount, getMaxGas(itemstack)));
			GasStack gasStack = new GasStack(stack.getGas(), amount);

			ItemDataUtils.setCompound(itemstack, "stored", gasStack.write(new NBTTagCompound()));
		}
	}

	public ItemStack getEmptyItem()
	{
		ItemStack stack = new ItemStack(this);
		setGas(stack, null);
		
		return stack;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List list)
	{
		ItemStack empty = new ItemStack(this);
		setGas(empty, null);
		list.add(empty);

		ItemStack filled = new ItemStack(this);
		setGas(filled, new GasStack(GasRegistry.getGas("fusionFuelDT"), ((IGasItem)filled.getItem()).getMaxGas(filled)));
		list.add(filled);
	}
}
