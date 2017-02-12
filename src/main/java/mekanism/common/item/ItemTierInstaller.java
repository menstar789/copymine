package mekanism.common.item;

import java.util.List;

import mekanism.common.Tier.BaseTier;
import mekanism.common.base.IMetaItem;
import mekanism.common.base.ITierUpgradeable;
import mekanism.common.tile.TileEntityBasicBlock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTierInstaller extends ItemMekanism implements IMetaItem
{
	public ItemTierInstaller()
	{
		super();
		setMaxStackSize(1);
		setHasSubtypes(true);
	}
	
	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		if(world.isRemote) 
		{
			return EnumActionResult.PASS;
		}
		
		TileEntity tile = world.getTileEntity(pos);
		BaseTier tier = BaseTier.values()[stack.getItemDamage()];
		
		if(tile instanceof ITierUpgradeable)
		{
			if(tile instanceof TileEntityBasicBlock && ((TileEntityBasicBlock)tile).playersUsing.size() > 0)
			{
				return EnumActionResult.FAIL;
			}
			
			if(((ITierUpgradeable)tile).upgrade(tier))
			{
				if(!player.capabilities.isCreativeMode)
				{
					stack.stackSize--;
				}
				
				return EnumActionResult.SUCCESS;
			}
			
			return EnumActionResult.PASS;
		}
		
		return EnumActionResult.PASS;
	}
	
	@Override
	public String getTexture(int meta)
	{
		return BaseTier.values()[meta].getSimpleName() + "TierInstaller";
	}
	
	@Override
	public int getVariants()
	{
		return BaseTier.values().length-1;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> itemList)
	{
		for(BaseTier tier : BaseTier.values())
		{
			if(tier.isObtainable())
			{
				itemList.add(new ItemStack(item, 1, tier.ordinal()));
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack item)
	{
		return "item." + BaseTier.values()[item.getItemDamage()].getSimpleName().toLowerCase() + "TierInstaller";
	}
}
