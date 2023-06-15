package CosmicChaos.Entities
import CosmicChaos.Core.Collideable
import CosmicChaos.Entities.Enemies.DemonBossEntity
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle

class Fire(damage: Float, radius: Float, parent: CreatureEntity) extends Entity {
  override val name: String = "Hellfire"
  override val collisionBox: Rectangle = new Rectangle(0, -radius/2f, radius/2, radius/2)   // TODO: This is badly aligned
  collisionLayer = CollisionLayers.none
  collisionMask = CollisionLayers.all

  renderLayer = 2

  private var timer: Float = 0.0f

  private val (frameW, frameH) = (96, 96)
  private val (scaleX, scaleY) = (radius/frameW, radius/frameH)
  private val spritesheet: Texture = new Texture("data/images/fire.png")
  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(spritesheet, frameW, frameH)
  private val animation = new Animation(0.1f, frames(0), loop = true)
  private var hitTimer: Float = 0.0f

  override def onUpdate(dt: Float): Unit = {
    timer += dt
    hitTimer -= dt
    animation.update(dt)

    if(parent.isDead)
      parentGameWorld.removeGameObject(this)
  }

  override def onCollideWith(other: Collideable): Unit = {
    other match {
      case _: DemonBossEntity =>
      case c: CreatureEntity =>
        if(hitTimer <= 0.0f) {
          parent.dealDamageTo(damage, c)
          hitTimer = 1.0f
        }
      case _ =>
    }
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    val (w, h) = (frameW*scaleX, frameH*scaleY)
    g.draw(animation.getCurrentFrame, position.x - w/2, position.y - h/2, w, h)
  }
}
