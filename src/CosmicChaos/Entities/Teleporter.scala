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
  override val collisionBox: Rectangle = new Rectangle(-150, -100, 300, 200)
  override protected val textureScale: Float = 2.0f
  private val (frameW, frameH) = (256, 128)
  private val teleportedUncharged: Texture = new Texture("data/images/warp/teleporter_uncharged.png")
  private val teleporterSpritesheet: Texture = new Texture("data/images/warp/teleporter_charged.png")
  private val teleporterFrame: Array[Array[TextureRegion]] = TextureRegion.split(teleporterSpritesheet, frameW,frameH)
  private val teleporterAnimation = new Animation(0.15f, teleporterFrame(0), loop = true)
  renderLayer = -2

  val chargeTime: Float = 100.0f  // seconds
  var charge: Float = 0.0f
  var charging: Boolean = false

  def charged: Boolean = charge >= chargeTime
  def chargePercent: Float = charge/chargeTime

  override protected def getTexture =  teleporterSpritesheet

  override def onGraphicRender(g: GdxGraphics): Unit = {
    val tex = getTexture
    val (w,h) = (frameW*textureScale, frameH*textureScale)
    val (x, y) = (position.x - (frameW*textureScale)/2, position.y - (frameH*textureScale)/2)

    if(charged) {
      teleporterAnimation.update(Gdx.graphics.getDeltaTime)
      g.draw(teleporterAnimation.getCurrentFrame, x, y, w, h)
    }
    else {
      g.draw(teleportedUncharged, x, y, w, h)
    }
  }

  override def onUpdate(dt: Float): Unit = {
    if(charging && charge < chargeTime)
      charge += dt
  }

  override def interact(player: PlayerEntity): Unit = {
    if(parentGameWorld.isTeleporterEventActive)
      return

    if(charged) {
      parentGameWorld.teleportToNextLevel()
    } else if(!charging) {
      charging = true
      parentGameWorld.startTeleporterEvent()
    }
  }

  override def getInteractText: String = {
    if(parentGameWorld.isTeleporterEventActive)
      return ""

    if(charged)
      "Teleport..."
    else
      "Activate teleporter..."
  }
}
