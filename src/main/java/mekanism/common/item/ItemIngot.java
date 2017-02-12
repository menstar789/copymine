package mekanism.common.item;

import java.util.List;

import mekanism.common.base.IMetaItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemIngot extends ItemMekanism implements IMetaItem
{
	public static String[] en_USNames = {"Obsidian", "Osmium", "Bronze",
										"Glowstone", "Steel", "Copper", 
										"Tin"};

	public ItemIngot()
	{
		super();
		setHasSubtypes(true);
	}
	
	@Override
	public String getTexture(int meta)
	{
		return en_USNames[meta] + "Ingot";
	}
	
	@Override
	public int getVariants()
	{
		return en_USNames.length;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> itemList)
	{
		for(int counter = 0; counter <= 6; counter++)
		{
			itemList.add(new ItemStack(item, 1, counter));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack item)
	{
		return "item." + en_USNames[item.getItemDamage()].toLowerCase() + "Ingot";
	}
}
