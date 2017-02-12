package mekanism.common.block;

import java.util.Random;

import mekanism.common.block.states.BlockStateBounding;
import mekanism.common.tile.TileEntityAdvancedBoundingBlock;
import mekanism.common.tile.TileEntityBoundingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockBounding extends Block
{
	public BlockBounding()
	{
		super(Material.IRON);
		setHardness(3.5F);
		setResistance(8F);
	}

	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateBounding(this);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(BlockStateBounding.advancedProperty, meta > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		boolean isAdvanced = state.getValue(BlockStateBounding.advancedProperty);
		return isAdvanced ? 1 : 0;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		try {
			TileEntityBoundingBlock tileEntity = (TileEntityBoundingBlock)world.getTileEntity(pos);
			IBlockState state1 = world.getBlockState(tileEntity.mainPos);
			return state1.getBlock().onBlockActivated(world, tileEntity.mainPos, state1, player, hand, stack, side, hitX, hitY, hitZ);
		} catch(Exception e) {
			return false;
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		super.breakBlock(world, pos, state);
		
		world.removeTileEntity(pos);
	}

	@Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		try {
			TileEntityBoundingBlock tileEntity = (TileEntityBoundingBlock)world.getTileEntity(pos);
			IBlockState state1 = world.getBlockState(tileEntity.mainPos);
			return state1.getBlock().getPickBlock(state1, target, world, tileEntity.mainPos, player);
		} catch(Exception e) {
			return null;
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		try {
			TileEntityBoundingBlock tileEntity = (TileEntityBoundingBlock)world.getTileEntity(pos);
			IBlockState state1 = world.getBlockState(tileEntity.mainPos);
			return state1.getBlock().removedByPlayer(state1, world, tileEntity.mainPos, player, willHarvest);
		} catch(Exception e) {
			return false;
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock)
	{
		try {
			TileEntityBoundingBlock tileEntity = (TileEntityBoundingBlock)world.getTileEntity(pos);
			tileEntity.onNeighborChange(state.getBlock());
			IBlockState state1 = world.getBlockState(tileEntity.mainPos);
			state1.getBlock().neighborChanged(state1, world, tileEntity.mainPos, this);
		} catch(Exception e) {}
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos)
	{
		try {
			TileEntityBoundingBlock tileEntity = (TileEntityBoundingBlock)world.getTileEntity(pos);
			IBlockState state1 = world.getBlockState(tileEntity.mainPos);
			return state1.getBlock().getPlayerRelativeBlockHardness(state1, player, world, tileEntity.mainPos);
		} catch(Exception e) {
			return super.getPlayerRelativeBlockHardness(state, player, world, pos);
		}
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
	{
		return null;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		if(state.getValue(BlockStateBounding.advancedProperty))
		{
			return new TileEntityAdvancedBoundingBlock();
		}
		else {
			return new TileEntityBoundingBlock();
		}
	}
}
