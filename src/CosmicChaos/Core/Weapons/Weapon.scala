package CosmicChaos.Core.Weapons

import CosmicChaos.Entities.{Entity, Projectile}
import com.badlogic.gdx.math.Vector2

abstract class Weapon(projectile: Projectile, isOneShot: Boolean, shotsPerSecond: Int, holder: Entity) {

  private val shotFrequency: Float = 1/shotsPerSecond.toFloat
  private var shootTimer: Float = 0
  private var triggerHeldLastFrame: Boolean = false
  private var shootingThisFrame: Boolean = false

  def update(triggerHeld: Boolean, dt: Float): Unit = {
    if(shootTimer > 0)
      shootTimer -= dt

    shootingThisFrame = false

    if(triggerHeld && canShootThisFrame) {
      shootTimer = shotFrequency
      shootingThisFrame = true

      // TODO: Implement a pool of projectiles to prevent creating a new instance each shot?
      val proj: Projectile = projectile.copy()
      proj.velocity = new Vector2(holder.aimVector.x, holder.aimVector.y).nor().scl(700).add(holder.velocity) // TODO: Projectile speed should be handled in the Projectile class
      holder.parentGameWorld.addGameObject(proj)
    }

    triggerHeldLastFrame = triggerHeld
  }

  def canShootThisFrame: Boolean = {
    val timerOk = shootTimer <= 0
    val stateOk = (isOneShot && !triggerHeldLastFrame) || !isOneShot
    timerOk && stateOk
  }

  def isShootingThisFrame: Boolean = shootingThisFrame
}
