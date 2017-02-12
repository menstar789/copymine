package mekanism.generators.common.block;

import java.util.List;

import mekanism.client.render.ctm.CTMBlockRenderContext;
import mekanism.client.render.ctm.CTMData;
import mekanism.client.render.ctm.ICTMBlock;
import mekanism.common.Mekanism;
import mekanism.common.base.IActiveState;
import mekanism.common.block.states.BlockStateBasic;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.tile.TileEntityElectricBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.GeneratorsBlocks;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.block.states.BlockStateReactor;
import mekanism.generators.common.block.states.BlockStateReactor.ReactorBlock;
import mekanism.generators.common.block.states.BlockStateReactor.ReactorBlockType;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import mekanism.generators.common.tile.reactor.TileEntityReactorLogicAdapter;
import mekanism.generators.common.tile.reactor.TileEntityReactorPort;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import buildcraft.api.tools.IToolWrench;

public abstract class BlockReactor extends BlockContainer implements ICTMBlock
{
	public CTMData[] ctmData = new CTMData[16];

	public BlockReactor()
	{
		super(Material.IRON);
		setHardness(3.5F);
		setResistance(8F);
		setCreativeTab(Mekanism.tabMekanism);
		
		initCTMs();
	}
	
	public static BlockReactor getReactorBlock(ReactorBlock block)
	{
		return new BlockReactor()
		{
			@Override
			public ReactorBlock getReactorBlock()
			{
				return block;
			}
		};
	}

	public abstract ReactorBlock getReactorBlock();
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		
		if(tile instanceof TileEntityReactorController)
		{
			state = state.withProperty(BlockStateReactor.activeProperty, ((IActiveState)tile).getActive());
		}
		
		if(tile instanceof TileEntityReactorPort)
		{
			state = state.withProperty(BlockStateReactor.activeProperty, ((TileEntityReactorPort)tile).fluidEject);
		}
		
		return state;
	}
	
	public void initCTMs()
	{
		switch(getReactorBlock())
		{
			case REACTOR_BLOCK:
				ctmData[0] = new CTMData(ReactorBlockType.REACTOR_CONTROLLER, ReactorBlockType.REACTOR_FRAME, ReactorBlockType.NEUTRON_CAPTURE, ReactorBlockType.REACTOR_PORT, ReactorBlockType.REACTOR_LOGIC_ADAPTER);
				ctmData[1] = new CTMData(ReactorBlockType.REACTOR_CONTROLLER, ReactorBlockType.REACTOR_FRAME, ReactorBlockType.NEUTRON_CAPTURE, ReactorBlockType.REACTOR_PORT, ReactorBlockType.REACTOR_LOGIC_ADAPTER);
				ctmData[2] = new CTMData(ReactorBlockType.REACTOR_CONTROLLER, ReactorBlockType.REACTOR_FRAME, ReactorBlockType.NEUTRON_CAPTURE, ReactorBlockType.REACTOR_PORT, ReactorBlockType.REACTOR_LOGIC_ADAPTER);
				ctmData[3] = new CTMData(ReactorBlockType.REACTOR_CONTROLLER, ReactorBlockType.REACTOR_FRAME, ReactorBlockType.NEUTRON_CAPTURE, ReactorBlockType.REACTOR_PORT, ReactorBlockType.REACTOR_LOGIC_ADAPTER);
				ctmData[4] = new CTMData(ReactorBlockType.REACTOR_CONTROLLER, ReactorBlockType.REACTOR_FRAME, ReactorBlockType.NEUTRON_CAPTURE, ReactorBlockType.REACTOR_PORT, ReactorBlockType.REACTOR_LOGIC_ADAPTER);
				
				break;
			case REACTOR_GLASS:
				ctmData[0] = new CTMData(ReactorBlockType.REACTOR_GLASS, ReactorBlockType.LASER_FOCUS_MATRIX);
				ctmData[1] = new CTMData(ReactorBlockType.REACTOR_GLASS, ReactorBlockType.LASER_FOCUS_MATRIX);
				
				break;
		}
	}
	
	@SideOnly(Side.CLIENT)
    @Override
    public IBlockState getExtendedState(IBlockState stateIn, IBlockAccess w, BlockPos pos) 
	{
        if(stateIn.getBlock() == null || stateIn.getMaterial() == Material.AIR) 
        {
            return stateIn;
        }
        
        IExtendedBlockState state = (IExtendedBlockState)stateIn;
        CTMBlockRenderContext ctx = new CTMBlockRenderContext(w, pos);

        return state.withProperty(BlockStateBasic.ctmProperty, ctx);
    }
	
	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateReactor(this, getTypeProperty());
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		ReactorBlockType type = ReactorBlockType.get(getReactorBlock(), meta & 0xF);

		return getDefaultState().withProperty(getTypeProperty(), type);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		ReactorBlockType type = state.getValue(getTypeProperty());
		return type.meta;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return state.getBlock().getMetaFromState(state);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock)
	{
		if(!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if(tileEntity instanceof TileEntityBasicBlock)
			{
				((TileEntityBasicBlock)tileEntity).onNeighborChange(neighborBlock);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityplayer, EnumHand hand, ItemStack stack, EnumFacing facing, float playerX, float playerY, float playerZ)
	{
		if(world.isRemote)
		{
			return true;
		}

		TileEntityElectricBlock tileEntity = (TileEntityElectricBlock)world.getTileEntity(pos);
		int metadata = state.getBlock().getMetaFromState(state);

		if(stack != null)
		{
			if(MekanismUtils.isBCWrench(stack.getItem()) && !stack.getUnlocalizedName().contains("omniwrench"))
			{
				if(entityplayer.isSneaking())
				{
					dismantleBlock(world, pos, false);
					return true;
				}

				((IToolWrench)stack.getItem()).wrenchUsed(entityplayer, pos);

				return true;
			}
		}

		if(tileEntity instanceof TileEntityReactorController)
		{
			if(!entityplayer.isSneaking())
			{
				entityplayer.openGui(MekanismGenerators.instance, ReactorBlockType.get(this, metadata).guiId, world, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}
		
		if(tileEntity instanceof TileEntityReactorLogicAdapter)
		{
			if(!entityplayer.isSneaking())
			{
				entityplayer.openGui(MekanismGenerators.instance, BlockStateReactor.ReactorBlockType.get(this, metadata).guiId, world, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativetabs, List list)
	{
		for(BlockStateReactor.ReactorBlockType type : BlockStateReactor.ReactorBlockType.values())
		{
			if(type.blockType == getReactorBlock() && type.isValidReactorBlock())
			{
				list.add(new ItemStack(item, 1, type.meta));
			}
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		int metadata = state.getBlock().getMetaFromState(state);
		
		if(ReactorBlockType.get(getReactorBlock(), metadata) == null)
		{
			return null;
		}

		return ReactorBlockType.get(getReactorBlock(), metadata).create();
	}
	
	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return this == GeneratorsBlocks.Reactor ? BlockRenderLayer.CUTOUT : BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
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

	/*This method is not used, metadata manipulation is required to create a Tile Entity.*/
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		int meta = state.getBlock().getMetaFromState(state);
		ReactorBlockType type = ReactorBlockType.get(getReactorBlock(), meta);
		
		if(type == ReactorBlockType.REACTOR_GLASS || type == ReactorBlockType.LASER_FOCUS_MATRIX)
		{
			if(!ctmData[meta].shouldRenderSide(world, pos.offset(side), side))
			{
				return false;
			}
		}
			
		return super.shouldSideBeRendered(state, world, pos, side);
	}
	
	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
		TileEntity tile = world.getTileEntity(pos);
		
		if(tile instanceof TileEntityReactorLogicAdapter)
		{
			return ((TileEntityReactorLogicAdapter)tile).checkMode() ? 15 : 0;
		}
		
        return 0;
    }
	
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		ReactorBlockType type = ReactorBlockType.get(getReactorBlock(), state.getBlock().getMetaFromState(state));

		switch(type)
		{
			case REACTOR_FRAME:
			case REACTOR_PORT:
			case REACTOR_LOGIC_ADAPTER:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		ReactorBlockType type = BlockStateReactor.ReactorBlockType.get(this, state.getBlock().getMetaFromState(state));

		switch(type)
		{
			case REACTOR_LOGIC_ADAPTER:
				return true;
			default:
				return false;
		}
	}

	public ItemStack dismantleBlock(World world, BlockPos pos, boolean returnBlock)
	{
		IBlockState state = world.getBlockState(pos);
		ItemStack itemStack = new ItemStack(this, 1, state.getBlock().getMetaFromState(state));

		world.setBlockToAir(pos);

		if(!returnBlock)
		{
			float motion = 0.7F;
			double motionX = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
			double motionY = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
			double motionZ = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;

			EntityItem entityItem = new EntityItem(world, pos.getX() + motionX, pos.getY() + motionY, pos.getZ() + motionZ, itemStack);

			world.spawnEntityInWorld(entityItem);
		}

		return itemStack;
	}
	
	@Override
	public CTMData getCTMData(IBlockState state)
	{
		return ctmData[state.getBlock().getMetaFromState(state)];
	}
	
	@Override
	public String getOverrideTexture(IBlockState state, EnumFacing side)
	{
		ReactorBlockType type = state.getValue(getTypeProperty());
		
		if(type == ReactorBlockType.REACTOR_CONTROLLER)
		{
			if(side == EnumFacing.UP)
			{
				return type.getName() + (state.getValue(BlockStateReactor.activeProperty) ? "_on" : "");
			}
			else {
				return "reactor_frame";
			}
		}
		else if(type == ReactorBlockType.REACTOR_PORT)
		{
			return type.getName() + (state.getValue(BlockStateReactor.activeProperty) ? "_output" : "");
		}
		
		return null;
	}
	
	@Override
	public PropertyEnum<ReactorBlockType> getTypeProperty()
	{
		return getReactorBlock().getProperty();
	}
}
