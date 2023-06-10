package CosmicChaos.Entities
import CosmicChaos.Core.Interactable
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle

class Teleporter extends Warp with Interactable {
  override val name: String = "RegularTeleporter"
  override val collisionBox: Rectangle = new Rectangle(200, 200, 10, 10)
  override protected val textureScale: Float = 0.2f
  private val (frameW, frameH) = (99, 99)
  private val teleporterSpritesheet: Texture = new Texture("data/images/warp/RegularTeleporter.png")
  private val teleporterFrame: Array[Array[TextureRegion]] = TextureRegion.split(teleporterSpritesheet, frameW,frameH)
  private val teleporterAnimation = new Animation(0.05f, teleporterFrame(0).tail, loop = true)


  override protected def getTexture =  teleporterSpritesheet
  override def onGraphicRender(g: GdxGraphics): Unit = {
    val tex = getTexture
    val (w,h) = (tex.getWidth*textureScale,tex.getHeight*textureScale)

    g.draw(teleporterAnimation.getCurrentFrame, position.x, position.y,w,h)
  }

  override def onUpdate(dt: Float): Unit = {

    position.add(5*dt,0*dt,0)
  }

  override def getInteractText: String = f"Teleporting you$$"
}
