package mekanism.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import mekanism.common.Tier.InductionProviderTier;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityInductionProvider extends TileEntityBasicBlock
{
	public InductionProviderTier tier = InductionProviderTier.BASIC;
	
	@Override
	public void onUpdate() {}
	
	public String getName()
	{
		return LangUtils.localize(getBlockType().getUnlocalizedName() + ".InductionProvider" + tier.getBaseTier().getSimpleName() + ".name");
	}
	
	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);
		
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			InductionProviderTier prevTier = tier;
			tier = InductionProviderTier.values()[dataStream.readInt()];
	
			if(prevTier != tier)
			{
				MekanismUtils.updateBlock(worldObj, getPos());
			}
		}
	}

	@Override
	public ArrayList<Object> getNetworkedData(ArrayList<Object> data)
	{
		super.getNetworkedData(data);
		
		data.add(tier.ordinal());

		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTags)
	{
		super.readFromNBT(nbtTags);

		tier = InductionProviderTier.values()[nbtTags.getInteger("tier")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTags)
	{
		super.writeToNBT(nbtTags);

		nbtTags.setInteger("tier", tier.ordinal());
		
		return nbtTags;
	}
}
