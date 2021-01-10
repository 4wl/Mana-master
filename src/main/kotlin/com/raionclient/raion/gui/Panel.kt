package com.raionclient.raion.gui

import com.mojang.blaze3d.systems.RenderSystem
import com.raionclient.raion.gui.font.RaionFontRenderer
import com.raionclient.raion.utils.Mouse
import com.raionclient.raion.utils.Vec2f
import com.raionclient.raion.utils.box
import com.raionclient.raion.utils.draw
import net.minecraft.client.render.VertexFormats.POSITION_COLOR
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL11.GL_LINE_LOOP
import org.lwjgl.opengl.GL11.GL_QUADS
import java.awt.Color

/**
 * @author cookiedragon234 24/Jun/2020
 */
class Panel(
	val title: String,
	val children: Collection<DrawableBox>
): DrawableBox() {
	var open = true
	var titleHeight = 0f
	var dragOffset: Vec2f? = null // If this is not null the panel is being dragged
	
	override fun render(matrices: MatrixStack, mousePos: Vec2f) {
		matrices.draw (glMode = GL_QUADS, format = POSITION_COLOR) {
			box(this@Panel, Color(0, 0, 0, 70))
		}
		matrices.draw (glMode = GL_LINE_LOOP, format = POSITION_COLOR) {
			box(this@Panel, Color.BLACK)
		}
		val strHeight = RaionFontRenderer.getStringHeight(title)
		titleHeight = strHeight + 4f
		RaionFontRenderer.drawCentered(matrices, title, posX + (sizeX / 2), posY + 2f, Color.WHITE.rgb)
		
		var currentY = posY + titleHeight
		if (open) {
			for (child in children) {
				child.posX = this.posX
				child.posY = currentY
				
				child.render(matrices, mousePos)
				
				currentY += child.sizeY
			}
		}
		bottomRight.x = topLeft.x + 125f
		bottomRight.y = currentY
	}
	
	override fun mouseDown(mousePos: Vec2f, button: Mouse, consumed: Boolean): Boolean {
		var thisConsumed = false
		if (isBeingRenderered && isMouseInTitle(mousePos) && !consumed) {
			if (button == Mouse.MOUSE_LEFT) {
				dragOffset = mousePos - topLeft
				thisConsumed = true
			} else if (button == Mouse.MOUSE_RIGHT) {
				open = open.not()
				thisConsumed = true
			}
		} else {
			dragOffset = null
		}
		children.forEach {
			it.isBeingRenderered = this.isBeingRenderered && open
			if (it.mouseDown(mousePos, button, thisConsumed || consumed)) {
				thisConsumed = true
			}
		}
		return thisConsumed
	}
	
	override fun mouseUp(mousePos: Vec2f, button: Mouse, consumed: Boolean): Boolean {
		var thisConsumed = false
		dragOffset = null
		children.forEach {
			it.isBeingRenderered = this.isBeingRenderered && open
			if (it.mouseUp(mousePos, button, thisConsumed || consumed)) {
				thisConsumed = true
			}
		}
		return thisConsumed
	}
	
	override fun mouseDragged(mousePos: Vec2f, button: Mouse, consumed: Boolean): Boolean {
		var thisConsumed = false
		val dragOffset = dragOffset
		if (isBeingRenderered && !consumed && dragOffset != null) {
			this.topLeft.x = mousePos.x - dragOffset.x
			this.topLeft.y = mousePos.y - dragOffset.y
			thisConsumed = true
		}
		children.forEach {
			it.isBeingRenderered = this.isBeingRenderered && open
			if (it.mouseDragged(mousePos, button, thisConsumed || consumed)) {
				thisConsumed = true
			}
		}
		return thisConsumed
	}
	
	fun isMouseInTitle(mousePos: Vec2f): Boolean
		= mousePos.x in posX..rightX && mousePos.y in posY..(posY + titleHeight)
}
