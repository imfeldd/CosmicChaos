package CosmicChaos.Entities

import CosmicChaos.Core.Interactable
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture

abstract class Chest extends Entity with Interactable {
  protected val closedTexture: Texture
  protected val openedTexture: Texture
  protected val textureScale: Float = 1.0f
  override val collisionLayer: Int = CollisionLayers.props
  override val collisionMask: Int = CollisionLayers.none

  var basePrice: Float
  var opened: Boolean = false

  override def onGraphicRender(g: GdxGraphics): Unit = {
    val tex = if(opened) openedTexture else closedTexture
    val (w, h) = (tex.getWidth*textureScale, tex.getHeight*textureScale)
    g.draw(tex, position.x - w/2, position.y - h/2, w, h)
  }

  override def interact(player: PlayerEntity): Unit = {
    if(player.cash < basePrice)
      return

    opened = true
    isInteractable = false
    player.cash -= basePrice
  }

  override def getInteractText: String = f"Open $name for $basePrice%.0f$$"
}
