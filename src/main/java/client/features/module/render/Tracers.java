package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventRenderGUI;
import client.event.listeners.EventRenderWorld;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.ui.theme.ThemeManager;
import client.utils.TimerUtils;
import client.utils.WVec3;
import client.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Tracers extends Module {

	public Tracers() {
		super("Tracers", Keyboard.KEY_F4, Category.RENDER);
	}

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventRenderWorld) {
			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();
			GlStateManager.disableTexture2D();
			GlStateManager.disableBlend();


			WVec3 eyeVector = (new WVec3(0.0D, 0.0D, 1.0D))
					.rotatePitch((float)-Math.toRadians(mc.thePlayer.rotationPitch))
					.rotateYaw((float)-Math.toRadians(mc.thePlayer.rotationYaw));
			for (Entity e1 : mc.theWorld.loadedEntityList) {
				if (e1 == mc.thePlayer) continue;
				if (!(e1 instanceof EntityPlayer)) continue;
				RenderUtils.glColor(ThemeManager.getTheme().light(0));
				RenderUtils.drawLine(new Vec3(eyeVector.getXCoord(), mc.thePlayer.getEyeHeight() + eyeVector.getYCoord(), eyeVector.getZCoord()), RenderUtils.renderEntityPos(e1), 1f);
			}

			GlStateManager.disableBlend();
			GlStateManager.enableTexture2D();
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
		}
		super.onEvent(e);
	}

	private final void drawTraces(Entity entity, Color color) {
		EntityPlayerSP thePlayer = mc.thePlayer;
		double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * TimerUtils.getTimer().renderPartialTicks -
				mc.getRenderManager().viewerPosX;
		double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * TimerUtils.getTimer().renderPartialTicks -
				mc.getRenderManager().viewerPosY;
		double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * TimerUtils.getTimer().renderPartialTicks -
				mc.getRenderManager().viewerPosZ;
		WVec3 eyeVector = (new WVec3(0.0D, 0.0D, 1.0D))
				.rotatePitch((float)-Math.toRadians(thePlayer.rotationPitch))
				.rotateYaw((float)-Math.toRadians(thePlayer.rotationYaw));

		RenderUtils.glColor(color);
		GlStateManager.disableTexture2D();
		GL11.glVertex3d(eyeVector.getXCoord(), thePlayer.getEyeHeight() + eyeVector.getYCoord(), eyeVector.getZCoord());
		GL11.glVertex3d(x, y, z);
		GL11.glVertex3d(x, y, z);
		GL11.glVertex3d(x, y + entity.getEyeHeight(), z);
		return;
	}

}
