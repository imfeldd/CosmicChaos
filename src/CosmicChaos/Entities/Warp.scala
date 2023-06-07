package CosmicChaos.Entities

import CosmicChaos.Core.Interactable
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture

abstract class Warp extends Entity with Interactable {

  protected val basicTexture: Texture
  protected val textureScale: Float


  override def onGraphicRender(g: GdxGraphics): Unit = {
    val tex = basicTexture
    val (w, h) = (tex.getWidth * textureScale, tex.getHeight * textureScale)
    g.draw(tex, position.x - w / 2, position.y - h / 2, w, h)
  }

  override def interact(player: PlayerEntity): Unit = {
    isInteractable = false
  }

  override def getInteractText: String = f"$name is travelling you to the next world"
}
