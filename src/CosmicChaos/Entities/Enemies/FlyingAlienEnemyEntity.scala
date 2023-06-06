package CosmicChaos.Entities.Enemies
import CosmicChaos.Core.Weapons.{Projectile, Weapon}
import CosmicChaos.Entities.EntityStats
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class FlyingAlienEnemyEntity extends GunnerEnemyEntity {
  override protected val weapon: Weapon = new Weapon(new Projectile(10, this), true, 8, this, ammoCapacity = 4, reloadTime = 1.5f, inaccuracy = 5.5f) {}
  override val baseStats: EntityStats = EntityStats(100, 12, 10, 100, 0.0f)
  override var stats: EntityStats = baseStats

  private val spritesheet: Texture = new Texture("data/images/entities/flyingAlien.png")
  private val (frameW, frameH) = (83, 64)
  private val spriteScale = 2.3f
  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(spritesheet, frameW, frameH)
  private val frameTime: Float = 0.066f
  private var animCounter: Float = 0.0f

  override def onGraphicRender(g: GdxGraphics): Unit = {
    val sprite = frames(0)((animCounter / frameTime).toInt % frames(0).length)

    drawSprite(sprite, g, spriteScale)
  }

  override def onUpdate(dt: Float): Unit = {
    animCounter += dt
    super.onUpdate(dt)
  }
}
