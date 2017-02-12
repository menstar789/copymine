package mekanism.common.tile;

import mekanism.api.Coord4D;
import mekanism.api.util.CapabilityUtils;
import mekanism.common.base.FluidHandlerWrapper;
import mekanism.common.base.IFluidHandlerWrapper;
import mekanism.common.content.boiler.BoilerSteamTank;
import mekanism.common.content.boiler.BoilerTank;
import mekanism.common.content.boiler.BoilerWaterTank;
import mekanism.common.util.PipeUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityBoilerValve extends TileEntityBoilerCasing implements IFluidHandlerWrapper
{
	public BoilerTank waterTank;
	public BoilerTank steamTank;

	public TileEntityBoilerValve()
	{
		super("BoilerValve");
		
		waterTank = new BoilerWaterTank(this);
		steamTank = new BoilerSteamTank(this);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if(!worldObj.isRemote)
		{
			if(structure != null && structure.upperRenderLocation != null && getPos().getY() >= structure.upperRenderLocation.yCoord-1)
			{
				if(structure.steamStored != null && structure.steamStored.amount > 0)
				{
					for(EnumFacing side : EnumFacing.values())
					{
						TileEntity tile = Coord4D.get(this).offset(side).getTileEntity(worldObj);
						
						if(tile != null && !(tile instanceof TileEntityBoilerValve) && CapabilityUtils.hasCapability(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()))
						{
							IFluidHandler handler = CapabilityUtils.getCapability(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
							
							if(PipeUtils.canFill(handler, structure.steamStored))
							{
								structure.steamStored.amount -= handler.fill(structure.steamStored, true);
								
								if(structure.steamStored.amount <= 0)
								{
									structure.steamStored = null;
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from)
	{
		if((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure))
		{
			if(structure.upperRenderLocation != null && getPos().getY() >= structure.upperRenderLocation.yCoord-1)
			{
				return new FluidTankInfo[] {steamTank.getInfo()};
			}
			else {
				return new FluidTankInfo[] {waterTank.getInfo()};
			}
		}
		
		return PipeUtils.EMPTY;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill)
	{
		if(structure != null && structure.upperRenderLocation != null && getPos().getY() < structure.upperRenderLocation.yCoord-1)
		{
			return waterTank.fill(resource, doFill);
		}
		
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain)
	{
		if(structure != null && structure.upperRenderLocation != null && getPos().getY() >= structure.upperRenderLocation.yCoord-1)
		{
			if(structure.steamStored != null)
			{
				if(resource.getFluid() == structure.steamStored.getFluid())
				{
					return steamTank.drain(resource.amount, doDrain);
				}
			}
		}

		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain)
	{
		if(structure != null && structure.upperRenderLocation != null && getPos().getY() >= structure.upperRenderLocation.yCoord-1)
		{
			return steamTank.drain(maxDrain, doDrain);
		}

		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid)
	{
		if((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure))
		{
			return structure.upperRenderLocation != null && getPos().getY() < structure.upperRenderLocation.yCoord-1;
		}
		
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid)
	{
		if((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure))
		{
			return structure.upperRenderLocation != null && getPos().getY() >= structure.upperRenderLocation.yCoord-1;
		}
		
		return false;
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
