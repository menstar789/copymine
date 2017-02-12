package mekanism.client.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityJetpackSmokeFX extends ParticleSmokeNormal
{
	private static Minecraft mc = FMLClientHandler.instance().getClient();

	public EntityJetpackSmokeFX(World world, double posX, double posY, double posZ, double velX, double velY, double velZ) 
	{
		super(world, posX, posY, posZ, velX, velY, velZ, 1.0F);
	}

	@Override
	public int getBrightnessForRender(float p_70013_1_) 
	{
		return 190 + (int)(20F * (1.0F - mc.gameSettings.gammaSetting));
	}

	@Override
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_)
	{
		if(particleAge > 0)
		{
			super.renderParticle(worldRendererIn, entityIn, partialTicks, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
		}
	}
}
