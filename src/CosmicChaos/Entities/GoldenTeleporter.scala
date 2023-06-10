package CosmicChaos.Entities
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle

abstract class GoldenTeleporter extends Warp {
  override val name: String = "GoldenTeleporter"
  override val collisionBox: Rectangle = new Rectangle(200, 200, 10, 10)
  override protected val textureScale: Float = 0.3f

  override def interact(player: PlayerEntity): Unit = {

  }

  override def getInteractText: String = "Teleporting you to a safer place"

  override def onGraphicRender(g: GdxGraphics): Unit = ???

}

