package mekanism.common.base;

import mekanism.common.capabilities.ItemCapabilityWrapper.ItemCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidItemWrapper extends ItemCapability implements IFluidHandler
{
	@Override
    public FluidTankProperties[] getTankProperties()
    {
        return new FluidTankProperties[] { new FluidTankProperties(getItem().getFluid(getStack()), getItem().getCapacity(getStack())) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if(getStack().stackSize != 1)
        {
            return 0;
        }
        
        return getItem().fill(getStack(), resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if(getStack().stackSize != 1 || resource == null)
        {
            return null;
        }

        FluidStack canDrain = drain(resource.amount, false);
        
        if(canDrain != null)
        {
            if(canDrain.isFluidEqual(resource))
            {
                return drain(resource.amount, doDrain);
            }
        }
        
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if(getStack().stackSize != 1)
        {
            return null;
        }
        
        return getItem().drain(getStack(), maxDrain, doDrain);
    }
    
    public IFluidContainerItem getItem()
    {
    	return (IFluidContainerItem)getStack().getItem();
    }

	@Override
	public boolean canProcess(Capability cap)
	{
		return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}
}
