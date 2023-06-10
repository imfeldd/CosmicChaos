package CosmicChaos.Entities
import CosmicChaos.Core.Interactable
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle

class Teleporter extends Warp with Interactable {
  override val name: String = "RegularTeleporter"
  override val collisionBox: Rectangle = new Rectangle(0, 0, 300, 300)
  override protected val textureScale: Float = 2.0f
  private val (frameW, frameH) = (256, 128)
  private val teleportedUncharged: Texture = new Texture("data/images/warp/teleporter_uncharged.png")
  private val teleporterSpritesheet: Texture = new Texture("data/images/warp/teleporter_charged.png")
  private val teleporterFrame: Array[Array[TextureRegion]] = TextureRegion.split(teleporterSpritesheet, frameW,frameH)
  private val teleporterAnimation = new Animation(0.15f, teleporterFrame(0), loop = true)
  private var charged: Boolean = false


  override protected def getTexture =  teleporterSpritesheet
  override def onGraphicRender(g: GdxGraphics): Unit = {
    val tex = getTexture
    val (w,h) = (frameW*textureScale, frameH*textureScale)

    if(charged) {
      teleporterAnimation.update(Gdx.graphics.getDeltaTime)
      g.draw(teleporterAnimation.getCurrentFrame, position.x, position.y, w, h)
    }
    else {
      g.draw(teleportedUncharged, position.x, position.y, w, h)
    }
  }

  override def onUpdate(dt: Float): Unit = {

    position.add(5*dt,0*dt,0)
  }

  override def interact(player: PlayerEntity): Unit = {
    if(charged)
      super.interact(player)
    else
      charged = true
  }

  override def getInteractText: String =
    if(charged)
      "Teleport..."
    else
      "Activate teleporter..."
}
