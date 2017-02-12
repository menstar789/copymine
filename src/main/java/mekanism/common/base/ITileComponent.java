package mekanism.common.base;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;

public interface ITileComponent
{
	public void tick();

	public void read(NBTTagCompound nbtTags);

	public void read(ByteBuf dataStream);

	public void write(NBTTagCompound nbtTags);

	public void write(ArrayList<Object> data);
	
	public void invalidate();
}
