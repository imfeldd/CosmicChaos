package CosmicChaos.Entities.Enemies

import CosmicChaos.Core.Stats.EntityStats
import CosmicChaos.Core.Weapons.{Projectile, Weapon}
import CosmicChaos.Entities.Entity
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class FlyingAlienEnemyEntity extends GunnerEnemyEntity {
  override protected val weapon: Weapon = new Weapon(new Projectile(1, this), true, 8, this, baseAmmoCapacity = 4, reloadTime = 1.5f, inaccuracy = 5.5f) {}
  override val baseStats: EntityStats = new EntityStats(
    maxHealth = 100,
    maxSpeed = 180,
    acceleration = 10,
    damage = 10,
    criticalChance = 0.0f,
    attackSpeed = 1.0f
  )
  override var stats: EntityStats = baseStats
  override val name: String = "Flying Alien"

  private val spritesheet: Texture = new Texture("data/images/entities/flyingAlien.png")
  private val (frameW, frameH) = (83, 64)
  private val spriteScale = 2.3f
  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(spritesheet, frameW, frameH)
  private val animation: Animation = new Animation(0.066f, frames(0), loop = true)

  cash = 100

  override def onEnterGameWorld(): Unit = {
    super.onEnterGameWorld()
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    animation.update(Gdx.graphics.getDeltaTime)
    if(!isDead){
      drawSprite(animation.getCurrentFrame, g, spriteScale)
    }
    else {
      drawDeathExplosionAnimation(g)
    }
  }

  override def onDeath(deathCause: Entity): Unit = {}

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)

    if(isDead && deathExplosionAnimation.isCurrentlyOver) {
      parentGameWorld.removeGameObject(this)
    }
  }
}
