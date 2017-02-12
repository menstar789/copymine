package mekanism.common.item;

import mekanism.api.IAlloyInteraction;
import mekanism.api.MekanismConfig.general;
import mekanism.api.util.CapabilityUtils;
import mekanism.common.MekanismItems;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAlloy extends ItemMekanism
{
	public ItemAlloy()
	{
		super();
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = world.getTileEntity(pos);
		
		if(general.allowTransmitterAlloyUpgrade && CapabilityUtils.hasCapability(tile, Capabilities.ALLOY_INTERACTION_CAPABILITY, side))
		{
			if(!world.isRemote)
			{
				IAlloyInteraction interaction = CapabilityUtils.getCapability(tile, Capabilities.ALLOY_INTERACTION_CAPABILITY, side);
				int ordinal = stack.getItem() == MekanismItems.EnrichedAlloy? 1 : (stack.getItem() == MekanismItems.ReinforcedAlloy ? 2 : 3);
				interaction.onAlloyInteraction(player, hand, stack, ordinal);
			}
			
			return EnumActionResult.SUCCESS;
		}
		
        return EnumActionResult.PASS;
    }
}
