package dev.xkmc.l2itemselector.overlay;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class WheelOverlay implements LayeredDraw.Layer {

	@Override
	public void render(GuiGraphics g, DeltaTracker pt) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		if (WheelHandler.wheel == null) return;
		WheelHandler.wheel.render(g, player);
	}


	public static void fillFan(GuiGraphics g, float x0, float y0, float ai, float da, float r0, float r1, float dr, int pZ, int c0, int c1) {
		Matrix4f mat = g.pose().last().pose();
		VertexConsumer vc = g.bufferSource().getBuffer(Shard.GUI_FAN);
		float x1 = x0 + Mth.cos(ai) * dr;
		float y1 = y0 + Mth.sin(ai) * dr;
		int n = (int) Math.max(3, da / (Math.PI / 24));
		for (int i = 0; i <= n; i++) {
			float a = ai + da / 2 - da / n * i;
			float x2 = x1 + Mth.cos(a) * r1;
			float y2 = y1 + Mth.sin(a) * r1;
			vc.addVertex(mat, x2, y2, pZ).setColor(c1);
			float x3 = x1 + Mth.cos(a) * r0;
			float y3 = y1 + Mth.sin(a) * r0;
			vc.addVertex(mat, x3, y3, pZ).setColor(c0);
		}
	}



	public static void drawSideGradient(GuiGraphics g, float x0, float y0, boolean left, float width, int outerColor, int innerColor) {
		Matrix4f mat = g.pose().last().pose();
		VertexConsumer vc = g.bufferSource().getBuffer(Shard.GUI_FAN);
		float xOuter = left ? -x0 : x0;
		float xInner = left ? -x0 + width : x0 - width;
		vc.addVertex(mat, xOuter, -y0, 0).setColor(outerColor);
		vc.addVertex(mat, xOuter, y0, 0).setColor(outerColor);
		vc.addVertex(mat, xInner, -y0, 0).setColor(innerColor);
		vc.addVertex(mat, xInner, y0, 0).setColor(innerColor);
	}

	public static void drawSeparator(GuiGraphics g, float x0, float y0, float a, float rInner, float rOuter, float innerHalfW, float outerHalfW, int innerColor, int outerColor) {
		Matrix4f mat = g.pose().last().pose();
		VertexConsumer vc = g.bufferSource().getBuffer(Shard.GUI_FAN);
		int seg = 8;
		for (int i = 0; i <= seg; i++) {
			float t = (float) i / seg;
			float et = (float) Math.sqrt(t);
			float r = rInner + (rOuter - rInner) * t;
			float halfW = innerHalfW + (outerHalfW - innerHalfW) * t;

			int aCol = (int) (((innerColor >> 24) & 0xFF) * (1 - et) + ((outerColor >> 24) & 0xFF) * et);
			int rCol = (int) (((innerColor >> 16) & 0xFF) * (1 - et) + ((outerColor >> 16) & 0xFF) * et);
			int gCol = (int) (((innerColor >> 8) & 0xFF) * (1 - et) + ((outerColor >> 8) & 0xFF) * et);
			int bCol = (int) ((innerColor & 0xFF) * (1 - et) + (outerColor & 0xFF) * et);
			int color = (aCol << 24) | (rCol << 16) | (gCol << 8) | bCol;

			float lx = x0 + Mth.cos(a - halfW) * r;
			float ly = y0 + Mth.sin(a - halfW) * r;
			float rx = x0 + Mth.cos(a + halfW) * r;
			float ry = y0 + Mth.sin(a + halfW) * r;

			vc.addVertex(mat, lx, ly, 0).setColor(color);
			vc.addVertex(mat, rx, ry, 0).setColor(color);
		}
	}


	private static class Shard extends RenderType {

		public Shard(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
			super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
		}

		public static final RenderType GUI_FAN = create("gui_fan",
				DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP, 786432,
				RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_GUI_SHADER)
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setDepthTestState(LEQUAL_DEPTH_TEST)
						.createCompositeState(false));

	}

}
