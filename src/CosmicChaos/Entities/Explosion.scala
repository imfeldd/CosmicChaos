package CosmicChaos.Entities
import CosmicChaos.Screens.GameScreen
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Circle, Rectangle}

class Explosion(damage: Float, radius: Float, parent: CreatureEntity) extends Entity {
  override val name: String = "Explosion"
  override val collisionBox: Rectangle = new Rectangle(0, 0, 0, 0)
  renderLayer = 2

  private var timer: Float = 0.0f

  private val spritesheet: Texture = new Texture("data/images/explosion.png")
  private val (frameW, frameH) = (96, 96)
  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(spritesheet, frameW, frameH)
  private val frameTime: Float = 0.1f

  override def onEnterGameWorld(): Unit = {
    super.onEnterGameWorld()
    parentGameWorld
      .getCollideablesWithinCircle(new Circle(position.x, position.y, radius))
      .filter(_.isInstanceOf[CreatureEntity])
      .map(_.asInstanceOf[CreatureEntity])
      .foreach(enemy => {
        val distRatio = enemy.position.dst(position) / radius
        val splashReduction = math.max(0.0f, 1.0f - distRatio*distRatio)  // 1 - x^2 --> downwards curve
        parent.dealDamageTo(damage*splashReduction, enemy)
      })

    GameScreen.cameraShake = 0.7f
  }

  override def onUpdate(dt: Float): Unit = {
    timer += dt
    if(timer >= frames(0).length * frameTime){
      parentGameWorld.removeGameObject(this)
    }
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    val (scaleX, scaleY) = (radius*2/frameW, radius*2/frameH)
    val (w, h) = (frameW*scaleX, frameH*scaleY)
    g.draw(frames(0)(math.min(frames(0).length - 1, (timer/frameTime).toInt)), position.x - w/2, position.y - h/2, w, h)
  }
}
