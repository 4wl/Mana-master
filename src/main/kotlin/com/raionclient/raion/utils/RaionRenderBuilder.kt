package com.raionclient.raion.utils

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11

/**
 * @author cookiedragon234 26/Jun/2020
 */
open class RaionRenderBuilder {
	var translation: Vec3d = Vec3d.ZERO
	
	inline fun begin(glMode: Int, action: RaionRenderBuilder.() -> Unit) {
		try {
			GL11.glBegin(glMode)
			action()
		} finally {
			GL11.glEnd()
		}
	}
}

fun <T: RaionRenderBuilder> T.vertex(x: Float, y: Float, z: Float): T = this.apply {
	GL11.glVertex3d(x + translation.x, y + translation.y, z + translation.z)
}

fun <T: RaionRenderBuilder> T.vertex(x: Double, y: Double, z: Double): T = this.apply {
	GL11.glVertex3d(x + translation.x, y + translation.y, z + translation.z)
}

fun <T: RaionRenderBuilder> T.color(r: Float, g: Float, b: Float, a: Float): T = this.apply {
	GlStateManager.color4f(r, g, b, a)
}

fun <T: RaionRenderBuilder> T.color(r: Int, g: Int, b: Int, a: Int): T = this.apply {
	GlStateManager.color4f(r / 255f, g / 255f, b / 255f, a / 255f)
}

fun <T: RaionRenderBuilder> T.box(bb: Box): T = this.apply {
	vertex(bb.minX, bb.minY, bb.minZ)
	vertex(bb.maxX, bb.minY, bb.minZ)
	vertex(bb.maxX, bb.minY, bb.maxZ)
	vertex(bb.minX, bb.minY, bb.maxZ)
	vertex(bb.minX, bb.maxY, bb.minZ)
	vertex(bb.minX, bb.maxY, bb.maxZ)
	vertex(bb.maxX, bb.maxY, bb.maxZ)
	vertex(bb.maxX, bb.maxY, bb.minZ)
	vertex(bb.minX, bb.minY, bb.minZ)
	vertex(bb.minX, bb.maxY, bb.minZ)
	vertex(bb.maxX, bb.maxY, bb.minZ)
	vertex(bb.maxX, bb.minY, bb.minZ)
	vertex(bb.maxX, bb.minY, bb.minZ)
	vertex(bb.maxX, bb.maxY, bb.minZ)
	vertex(bb.maxX, bb.maxY, bb.maxZ)
	vertex(bb.maxX, bb.minY, bb.maxZ)
	vertex(bb.minX, bb.minY, bb.maxZ)
	vertex(bb.maxX, bb.minY, bb.maxZ)
	vertex(bb.maxX, bb.maxY, bb.maxZ)
	vertex(bb.minX, bb.maxY, bb.maxZ)
	vertex(bb.minX, bb.minY, bb.minZ)
	vertex(bb.minX, bb.minY, bb.maxZ)
	vertex(bb.minX, bb.maxY, bb.maxZ)
	vertex(bb.minX, bb.maxY, bb.minZ)
}

inline fun draw3d(translate: Boolean = false, action: RaionRenderBuilder.() -> Unit) {
	val builder = RaionRenderBuilder()
	
	try {
		GlStateManager.pushMatrix()
		GlStateManager.enableBlend()
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		GL11.glEnable(GL11.GL_LINE_SMOOTH)
		GlStateManager.lineWidth(2f)
		GlStateManager.disableTexture()
		GlStateManager.enableCull()
		GlStateManager.disableDepthTest()
		GlStateManager.disableLighting()
		
		if (translate) {
			val camera = mc.gameRenderer.camera
			GlStateManager.rotatef(MathHelper.wrapDegrees(camera.pitch), 1f, 0f, 0f)
			GlStateManager.rotatef(MathHelper.wrapDegrees(camera.yaw + 180f), 0f, 1f, 0f)
			builder.translation = builder.translation.subtract(camera.pos)
		}
		
		builder.action()
	} finally {
		GlStateManager.color4f(1f, 1f, 1f, 1f)
		GlStateManager.enableDepthTest()
		GlStateManager.enableTexture()
		GlStateManager.enableBlend()
		GL11.glDisable(GL11.GL_LINE_SMOOTH)
		GlStateManager.popMatrix()
	}
}

private fun RaionRenderBuilder.translateVec(camera: Camera) {

}
