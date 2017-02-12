package mekanism.generators.common.tile.turbine;

import mekanism.api.Coord4D;
import mekanism.api.util.CapabilityUtils;
import mekanism.common.base.FluidHandlerWrapper;
import mekanism.common.base.IFluidHandlerWrapper;
import mekanism.common.util.PipeUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityTurbineVent extends TileEntityTurbineCasing implements IFluidHandlerWrapper
{
	public FluidTankInfo fakeInfo = new FluidTankInfo(null, 1000);
	
	public TileEntityTurbineVent()
	{
		super("TurbineVent");
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if(structure != null && structure.flowRemaining > 0)
		{
			FluidStack fluidStack = new FluidStack(FluidRegistry.WATER, structure.flowRemaining);
			
			for(EnumFacing side : EnumFacing.VALUES)
			{
				TileEntity tile = Coord4D.get(this).offset(side).getTileEntity(worldObj);
				
				if(tile != null && CapabilityUtils.hasCapability(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()))
				{
					IFluidHandler handler = CapabilityUtils.getCapability(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
					
					if(PipeUtils.canFill(handler, fluidStack))
					{
						structure.flowRemaining -= handler.fill(fluidStack, true);
					}
				}
			}
		}
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from)
	{
		return ((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure)) ? new FluidTankInfo[] {fakeInfo} : PipeUtils.EMPTY;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill)
	{
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain)
	{
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid)
	{
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid)
	{
		return fluid == FluidRegistry.WATER;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing side)
	{
		if((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure))
		{
			if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			{
				return true;
			}
		}
		
		return super.hasCapability(capability, side);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing side)
	{
		if((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure))
		{
			if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			{
				return (T)new FluidHandlerWrapper(this, side);
			}
		}
		
		return super.getCapability(capability, side);
	}
}
