package mekanism.generators.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.generators;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityWindGenerator extends TileEntityGenerator implements IBoundingBlock
{
	public static final float SPEED = 32F;
	public static final float SPEED_SCALED = 256F/SPEED;
	
	/** The angle the blades of this Wind Turbine are currently at. */
	public double angle;
	
	public float currentMultiplier;

	public TileEntityWindGenerator()
	{
		super("wind", "WindGenerator", 200000, (generators.windGenerationMax)*2);
		inventory = new ItemStack[1];
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(!worldObj.isRemote)
		{
			ChargeUtils.charge(0, this);
			
			if(ticker % 20 == 0)
			{
				setActive((currentMultiplier = getMultiplier()) > 0);
			}
			
			if(getActive())
			{
				setEnergy(electricityStored + (generators.windGenerationMin*currentMultiplier));
			}
		}
		else {
			if(getActive())
			{
				angle = (angle+(getPos().getY()+4F)/SPEED_SCALED) % 360;
			}
		}
	}
	
	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);

		if(worldObj.isRemote)
		{
			currentMultiplier = dataStream.readFloat();
		}
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);

		data.add(currentMultiplier);

		return data;
	}

	/** Determines the current output multiplier, taking sky visibility and height into account. **/
	public float getMultiplier()
	{
		if(worldObj.canSeeSky(getPos().add(0, 4, 0))) 
		{
			final float minY = (float)generators.windGenerationMinY;
			final float maxY = (float)generators.windGenerationMaxY;
			final float minG = (float)generators.windGenerationMin;
			final float maxG = (float)generators.windGenerationMax;

			final float slope = (maxG - minG) / (maxY - minY);
			final float intercept = minG - slope * minY;

			final float clampedY = Math.min(maxY, Math.max(minY, (float)(getPos().getY()+4)));
			final float toGen = slope * clampedY + intercept;

			return toGen / minG;
		} 
		else {
			return 0;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getVolume()
	{
		return 1.5F*super.getVolume();
	}

    private static final String[] methods = new String[] {"getEnergy", "getOutput", "getMaxEnergy", "getEnergyNeeded", "getMultiplier"};

	@Override
	public String[] getMethods()
	{
		return methods;
	}

	@Override
	public Object[] invoke(int method, Object[] arguments) throws Exception
	{
		switch(method)
		{
			case 0:
				return new Object[] {electricityStored};
			case 1:
				return new Object[] {output};
			case 2:
				return new Object[] {BASE_MAX_ENERGY};
			case 3:
				return new Object[] {(BASE_MAX_ENERGY -electricityStored)};
			case 4:
				return new Object[] {getMultiplier()};
			default:
				throw new NoSuchMethodException();
		}
	}

	@Override
	public boolean canOperate()
	{
		return electricityStored < BASE_MAX_ENERGY && getMultiplier() > 0 && MekanismUtils.canFunction(this);
	}

	@Override
	public void onPlace()
	{
		Coord4D current = Coord4D.get(this);
		MekanismUtils.makeBoundingBlock(worldObj, getPos().offset(EnumFacing.UP, 1), current);
		MekanismUtils.makeBoundingBlock(worldObj, getPos().offset(EnumFacing.UP, 2), current);
		MekanismUtils.makeBoundingBlock(worldObj, getPos().offset(EnumFacing.UP, 3), current);
		MekanismUtils.makeBoundingBlock(worldObj, getPos().offset(EnumFacing.UP, 4), current);
	}

	@Override
	public void onBreak()
	{
		worldObj.setBlockToAir(getPos().add(0, 1, 0));
		worldObj.setBlockToAir(getPos().add(0, 2, 0));
		worldObj.setBlockToAir(getPos().add(0, 3, 0));
		worldObj.setBlockToAir(getPos().add(0, 4, 0));

		worldObj.setBlockToAir(getPos());
	}

	@Override
	public boolean renderUpdate()
	{
		return false;
	}

	@Override
	public boolean lightUpdate()
	{
		return false;
	}
}
