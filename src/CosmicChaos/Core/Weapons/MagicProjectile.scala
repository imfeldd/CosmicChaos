package CosmicChaos.Core.Weapons

import CosmicChaos.Entities.CreatureEntity
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.{Texture}
import com.badlogic.gdx.math.Vector3

class MagicProjectile(damage: Float, holder: CreatureEntity) extends Projectile(damage, holder) {
  override val name: String = "PsyWave"
  private val projectile :Texture = new Texture("data/images/boss/magicMage/Projectile/fireball.png")
  private val (projectileFrameW, projectileFrameH) = ( projectile.getWidth/5, projectile.getHeight)
  private val projectileFrames: Array[Array[TextureRegion]] = TextureRegion.split(projectile, projectileFrameW, projectileFrameH)
  private val projectileAnimation = new Animation(0.09f, projectileFrames(0), loop = true)

  private val projectileBlueFire :Texture = new Texture ("data/images/boss/magicMage/Projectile/blueFire.png")
  private val (projectileBlueFireW, projectileBlueFireH) = ( projectileBlueFire.getWidth/5, projectileBlueFire.getHeight)
  private val projectileBlueFireFrames: Array[Array[TextureRegion]] = TextureRegion.split(projectileBlueFire, projectileBlueFireW, projectileBlueFireH)
  private val projectileBlueFireAnimation = new Animation(0.09f, projectileBlueFireFrames(0), loop = true)

  private val spriteScale = 5.5f
  private var initialPosition: Vector3 = _
  private var freezeTimer: Float = 0.2f
  var mageState = 1
  collisionLayer = CollisionLayers.bullet
  collisionMask = CollisionLayers.player + CollisionLayers.world + CollisionLayers.props

  override def onEnterGameWorld(): Unit = {
    super.onEnterGameWorld()
    initialPosition = new Vector3(position.x, position.y, 0)
  }

  override def onUpdate(dt: Float): Unit = {
    freezeTimer -= dt

    if (freezeTimer >= 0.0f)
      return

    position.x += velocity.x * dt
    position.y += velocity.y * dt

    // Despawn the projectile after 1 seconds
    if (timer >=1) {
      parentGameWorld.removeGameObject(this)
    }
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    if (mageState == 1) {
      projectileAnimation.update(Gdx.graphics.getDeltaTime)
      g.draw(projectileAnimation.getCurrentFrame, position.x, position.y, 100, 100)
    }
    else {
      projectileBlueFireAnimation.update(Gdx.graphics.getDeltaTime)
      g.draw(projectileBlueFireAnimation.getCurrentFrame, position.x, position.y, 100, 100)
    }
  }

  override def copy: Projectile = new MagicProjectile(damage, parent)



}
