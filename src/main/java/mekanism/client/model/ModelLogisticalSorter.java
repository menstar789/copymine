package mekanism.client.model;

import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLogisticalSorter extends ModelBase
{
	ModelRenderer portBack;
	ModelRenderer portBackLarge;
	ModelRenderer connectorBack;
	ModelRenderer portFront;
	ModelRenderer ring1;
	ModelRenderer ring2;
	ModelRenderer ring3;
	ModelRenderer ring4;
	ModelRenderer ring5;
	ModelRenderer ring6;
	ModelRenderer ring7;
	ModelRenderer pistonBar1;
	ModelRenderer pipe;
	ModelRenderer pistonBase1;
	ModelRenderer pistonBrace1;
	ModelRenderer pistonConnector1;
	ModelRenderer pistonBrace2;
	ModelRenderer pistonConnector2;
	ModelRenderer pistonBar2;
	ModelRenderer pistonBase2;
	ModelRenderer panel2;
	ModelRenderer led4;
	ModelRenderer led3;
	ModelRenderer led2;
	ModelRenderer led1;
	ModelRenderer panel1;

	public ModelLogisticalSorter() 
	{
		textureWidth = 128;
		textureHeight = 64;

		portBack = new ModelRenderer(this, 26, 11);
		portBack.addBox(0F, 0F, 0F, 8, 8, 1);
		portBack.setRotationPoint(-4F, 12F, 8F);
		portBack.setTextureSize(128, 64);
		portBack.mirror = true;
		setRotation(portBack, 0F, 0F, 0F);
		portBackLarge = new ModelRenderer(this, 0, 0);
		portBackLarge.addBox(0F, 0F, 0F, 12, 12, 1);
		portBackLarge.setRotationPoint(-6F, 10F, 7F);
		portBackLarge.setTextureSize(128, 64);
		portBackLarge.mirror = true;
		setRotation(portBackLarge, 0F, 0F, 0F);
		connectorBack = new ModelRenderer(this, 26, 0);
		connectorBack.addBox(0F, 0F, 0F, 10, 10, 1);
		connectorBack.setRotationPoint(-5F, 11F, 6F);
		connectorBack.setTextureSize(128, 64);
		connectorBack.mirror = true;
		setRotation(connectorBack, 0F, 0F, 0F);
		portFront = new ModelRenderer(this, 48, 0);
		portFront.addBox(0F, 0F, 0F, 10, 10, 1);
		portFront.setRotationPoint(-5F, 11F, -8F);
		portFront.setTextureSize(128, 64);
		portFront.mirror = true;
		setRotation(portFront, 0F, 0F, 0F);
		ring1 = new ModelRenderer(this, 44, 11);
		ring1.addBox(0F, 0F, 0F, 7, 7, 1);
		ring1.setRotationPoint(-3.5F, 12.5F, -7F);
		ring1.setTextureSize(128, 64);
		ring1.mirror = true;
		setRotation(ring1, 0F, 0F, 0F);
		ring2 = new ModelRenderer(this, 44, 11);
		ring2.addBox(0F, 0F, 0F, 7, 7, 1);
		ring2.setRotationPoint(-3.5F, 12.5F, -5F);
		ring2.setTextureSize(128, 64);
		ring2.mirror = true;
		setRotation(ring2, 0F, 0F, 0F);
		ring3 = new ModelRenderer(this, 44, 11);
		ring3.addBox(0F, 0F, 0F, 7, 7, 1);
		ring3.setRotationPoint(-3.5F, 12.5F, -3F);
		ring3.setTextureSize(128, 64);
		ring3.mirror = true;
		setRotation(ring3, 0F, 0F, 0F);
		ring4 = new ModelRenderer(this, 44, 11);
		ring4.addBox(0F, 0F, 0F, 7, 7, 1);
		ring4.setRotationPoint(-3.5F, 12.5F, -1F);
		ring4.setTextureSize(128, 64);
		ring4.mirror = true;
		setRotation(ring4, 0F, 0F, 0F);
		ring5 = new ModelRenderer(this, 44, 11);
		ring5.addBox(0F, 0F, 0F, 7, 7, 1);
		ring5.setRotationPoint(-3.5F, 12.5F, 1F);
		ring5.setTextureSize(128, 64);
		ring5.mirror = true;
		setRotation(ring5, 0F, 0F, 0F);
		ring6 = new ModelRenderer(this, 44, 11);
		ring6.addBox(0F, 0F, 0F, 7, 7, 1);
		ring6.setRotationPoint(-3.5F, 12.5F, 3F);
		ring6.setTextureSize(128, 64);
		ring6.mirror = true;
		setRotation(ring6, 0F, 0F, 0F);
		ring7 = new ModelRenderer(this, 44, 11);
		ring7.addBox(0F, 0F, 0F, 7, 7, 1);
		ring7.setRotationPoint(-3.5F, 12.5F, 5F);
		ring7.setTextureSize(128, 64);
		ring7.mirror = true;
		setRotation(ring7, 0F, 0F, 0F);
		pistonBar1 = new ModelRenderer(this, 0, 20);
		pistonBar1.addBox(0F, 0F, 0F, 1, 1, 5);
		pistonBar1.setRotationPoint(-0.5F, 19.5F, -2.99F);
		pistonBar1.setTextureSize(128, 64);
		pistonBar1.mirror = true;
		setRotation(pistonBar1, 0F, 0F, 0F);
		pipe = new ModelRenderer(this, 0, 13);
		pipe.addBox(0F, 0F, 0F, 6, 6, 14);
		pipe.setRotationPoint(-3F, 13F, -7F);
		pipe.setTextureSize(128, 64);
		pipe.mirror = true;
		setRotation(pipe, 0F, 0F, 0F);
		pistonBase1 = new ModelRenderer(this, 0, 13);
		pistonBase1.addBox(0F, 0F, 0F, 2, 2, 5);
		pistonBase1.setRotationPoint(-1F, 19F, 1.01F);
		pistonBase1.setTextureSize(128, 64);
		pistonBase1.mirror = true;
		setRotation(pistonBase1, 0F, 0F, 0F);
		pistonBrace1 = new ModelRenderer(this, 0, 33);
		pistonBrace1.addBox(0F, 0F, 0F, 2, 2, 3);
		pistonBrace1.setRotationPoint(-1F, 18.5F, -7F);
		pistonBrace1.setTextureSize(128, 64);
		pistonBrace1.mirror = true;
		setRotation(pistonBrace1, 0F, 0F, 0F);
		pistonConnector1 = new ModelRenderer(this, 10, 33);
		pistonConnector1.addBox(0F, 0F, 0F, 2, 2, 1);
		pistonConnector1.setRotationPoint(-1F, 19F, -4F);
		pistonConnector1.setTextureSize(128, 64);
		pistonConnector1.mirror = true;
		setRotation(pistonConnector1, 0F, 0F, 0F);
		pistonBrace2 = new ModelRenderer(this, 0, 33);
		pistonBrace2.addBox(0F, 0F, 0F, 2, 2, 3);
		pistonBrace2.setRotationPoint(-1F, 11.5F, -7F);
		pistonBrace2.setTextureSize(128, 64);
		pistonBrace2.mirror = true;
		setRotation(pistonBrace2, 0F, 0F, 0F);
		pistonConnector2 = new ModelRenderer(this, 10, 33);
		pistonConnector2.addBox(0F, 0F, 0F, 2, 2, 1);
		pistonConnector2.setRotationPoint(-1F, 11F, -4F);
		pistonConnector2.setTextureSize(128, 64);
		pistonConnector2.mirror = true;
		setRotation(pistonConnector2, 0F, 0F, 0F);
		pistonBar2 = new ModelRenderer(this, 0, 20);
		pistonBar2.addBox(0F, 0F, 0F, 1, 1, 5);
		pistonBar2.setRotationPoint(-0.5F, 11.5F, -2.99F);
		pistonBar2.setTextureSize(128, 64);
		pistonBar2.mirror = true;
		setRotation(pistonBar2, 0F, 0F, 0F);
		pistonBase2 = new ModelRenderer(this, 0, 13);
		pistonBase2.addBox(0F, 0F, 0F, 2, 2, 5);
		pistonBase2.setRotationPoint(-1F, 11F, 1.01F);
		pistonBase2.setTextureSize(128, 64);
		pistonBase2.mirror = true;
		setRotation(pistonBase2, 0F, 0F, 0F);
		panel2 = new ModelRenderer(this, 40, 22);
		panel2.addBox(0F, 0F, 0F, 1, 3, 8);
		panel2.setRotationPoint(3F, 14.5F, -4.5F);
		panel2.setTextureSize(128, 64);
		panel2.mirror = true;
		setRotation(panel2, 0F, 0F, 0F);
		led4 = new ModelRenderer(this, 40, 22);
		led4.addBox(0F, 0F, 0F, 1, 1, 1);
		led4.setRotationPoint(3.5F, 15.5F, -1.5F);
		led4.setTextureSize(128, 64);
		led4.mirror = true;
		setRotation(led4, 0F, 0F, 0F);
		led3 = new ModelRenderer(this, 40, 22);
		led3.addBox(0F, 0F, 0F, 1, 1, 1);
		led3.setRotationPoint(3.5F, 15.5F, -3.5F);
		led3.setTextureSize(128, 64);
		led3.mirror = true;
		setRotation(led3, 0F, 0F, 0F);
		led2 = new ModelRenderer(this, 40, 22);
		led2.addBox(0F, 0F, 0F, 1, 1, 1);
		led2.setRotationPoint(-4.5F, 15.5F, -3.5F);
		led2.setTextureSize(128, 64);
		led2.mirror = true;
		setRotation(led2, 0F, 0F, 0F);
		led1 = new ModelRenderer(this, 40, 22);
		led1.addBox(0F, 0F, 0F, 1, 1, 1);
		led1.setRotationPoint(-4.5F, 15.5F, -1.5F);
		led1.setTextureSize(128, 64);
		led1.mirror = true;
		setRotation(led1, 0F, 0F, 0F);
		panel1 = new ModelRenderer(this, 40, 22);
		panel1.addBox(0F, 0F, 0F, 1, 3, 8);
		panel1.setRotationPoint(-4F, 14.5F, -4.5F);
		panel1.setTextureSize(128, 64);
		panel1.mirror = true;
		setRotation(panel1, 0F, 0F, 0F);
	}

	public void render(float size, boolean active)
	{
		MekanismRenderer.glowOn();
		MekanismRenderer.blendOn();
		
		GlStateManager.scale(1.001F, 1.001F, 1.001F);
		GlStateManager.translate(0, -0.0011F, 0);
		
		led4.render(size);
		led3.render(size);
		led2.render(size);
		led1.render(size);
		
		MekanismRenderer.blendOff();
		MekanismRenderer.glowOff();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
