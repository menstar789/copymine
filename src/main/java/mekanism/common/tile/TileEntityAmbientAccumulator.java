package mekanism.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Random;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.ITubeConnection;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.IntegerInput;
import mekanism.common.recipe.machines.AmbientGasRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityAmbientAccumulator extends TileEntityContainerBlock implements IGasHandler, ITubeConnection
{
	public GasTank collectedGas = new GasTank(1000);

	public int cachedDimensionId = 0;
	public AmbientGasRecipe cachedRecipe;

	public static Random gasRand = new Random();

	public TileEntityAmbientAccumulator()
	{
		super("AmbientAccumulator");
		inventory = new ItemStack[0];
	}

	@Override
	public void onUpdate()
	{
		if(!worldObj.isRemote)
		{
			if(cachedRecipe == null || worldObj.provider.getDimension() != cachedDimensionId)
			{
				cachedDimensionId = worldObj.provider.getDimension();
				cachedRecipe = RecipeHandler.getDimensionGas(new IntegerInput(cachedDimensionId));
			}

			if(cachedRecipe != null && gasRand.nextDouble() < 0.05 && cachedRecipe.getOutput().applyOutputs(collectedGas, false, 1))
			{
				cachedRecipe.getOutput().applyOutputs(collectedGas, true, 1);
			}
		}
	}


	@Override
	public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer)
	{
		return 0;
	}

	@Override
	public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer)
	{
		return collectedGas.draw(amount, doTransfer);
	}

	@Override
	public boolean canReceiveGas(EnumFacing side, Gas type)
	{
		return false;
	}

	@Override
	public boolean canDrawGas(EnumFacing side, Gas type)
	{
		return type == collectedGas.getGasType();
	}

	@Override
	public boolean canTubeConnect(EnumFacing side)
	{
		return true;
	}

	@Override
	public ArrayList<Object> getNetworkedData(ArrayList<Object> data)
	{
		if(collectedGas.getGasType() != null)
		{
			data.add(collectedGas.getGasType().getID());
			data.add(collectedGas.getStored());
		} 
		else {
			data.add(-1);
			data.add(0);
		}
		
		return data;
	}

	@Override
	public void handlePacketData(ByteBuf data)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			int gasID = data.readInt();
			
			if(gasID < 0)
			{
				collectedGas.setGas(null);
			} 
			else {
				collectedGas.setGas(new GasStack(gasID, data.readInt()));
			}
		}
	}
}
