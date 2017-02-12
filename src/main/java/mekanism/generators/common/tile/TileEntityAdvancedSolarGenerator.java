package mekanism.generators.common.tile;

import java.util.EnumSet;

import mekanism.api.Coord4D;
import mekanism.api.IEvaporationSolar;
import mekanism.api.MekanismConfig.generators;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.util.MekanismUtils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityAdvancedSolarGenerator extends TileEntitySolarGenerator implements IBoundingBlock, IEvaporationSolar
{
	public TileEntityAdvancedSolarGenerator()
	{
		super("AdvancedSolarGenerator", 200000, generators.advancedSolarGeneration*2);
		GENERATION_RATE = generators.advancedSolarGeneration;
	}

	@Override
	public EnumSet<EnumFacing> getOutputtingSides()
	{
		return EnumSet.of(facing);
	}

	@Override
	public void onPlace()
	{
		Coord4D current = Coord4D.get(this);
		MekanismUtils.makeBoundingBlock(worldObj, getPos().add(0, 1, 0), current);

		for(int x = -1; x <= 1; x++)
		{
			for(int z = -1; z <= 1; z++)
			{
				MekanismUtils.makeBoundingBlock(worldObj, getPos().add(x, 2, z), current);
			}
		}
	}

	@Override
	public void onBreak()
	{
		worldObj.setBlockToAir(getPos().add(0, 1, 0));

		for(int x = -1; x <= 1; x++)
		{
			for(int z = -1; z <= 1; z++)
			{
				worldObj.setBlockToAir(getPos().add(x, 2, z));
			}
		}

		invalidate();
		worldObj.setBlockToAir(getPos());
	}

	@Override
	public boolean seesSun()
	{
		return seesSun;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing side)
	{
		return capability == Capabilities.EVAPORATION_SOLAR_CAPABILITY || super.hasCapability(capability, side);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing side)
	{
		if(capability == Capabilities.EVAPORATION_SOLAR_CAPABILITY)
		{
			return (T)this;
		}
		
		return super.getCapability(capability, side);
	}
}
