package mekanism.client.render.transmitter;

import mekanism.api.MekanismConfig.client;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.ColourRGBA;
import mekanism.common.multipart.PartPressurizedTube;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.EnumFacing;

import org.lwjgl.opengl.GL11;

public class RenderPressurizedTube extends RenderTransmitterBase<PartPressurizedTube>
{
	public RenderPressurizedTube()
	{
		super();
	}
	
	@Override
	public void renderMultipartAt(PartPressurizedTube tube, double x, double y, double z, float partialTick, int destroyStage)
	{
		if(client.opaqueTransmitters || !tube.getTransmitter().hasTransmitterNetwork() || tube.getTransmitter().getTransmitterNetwork().refGas == null || tube.getTransmitter().getTransmitterNetwork().gasScale == 0)
		{
			return;
		}

		push();
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer worldRenderer = tessellator.getBuffer();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

		for(EnumFacing side : EnumFacing.VALUES)
		{
			renderGasSide(worldRenderer, side, tube);
		}

		MekanismRenderer.glowOn(0);

		tessellator.draw();

		MekanismRenderer.glowOff();
		
		pop();
	}
	
	public void renderGasSide(VertexBuffer renderer, EnumFacing side, PartPressurizedTube tube)
	{
		bindTexture(MekanismRenderer.getBlocksTexture());
		renderTransparency(renderer, tube.getTransmitter().getTransmitterNetwork().refGas.getSprite(), getModelForSide(tube, side), new ColourRGBA(1.0, 1.0, 1.0, tube.currentScale));
	}
}
