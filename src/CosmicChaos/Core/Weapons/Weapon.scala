package CosmicChaos.Core.Weapons

import CosmicChaos.Entities.CreatureEntity
import com.badlogic.gdx.math.Vector2

import scala.util.Random

abstract class Weapon(var projectile: Projectile, val isFullAuto: Boolean, val shotsPerSecond: Int, val holder: CreatureEntity, val ammoCapacity: Int = -1, val reloadTime: Float = 0.0f, val inaccuracy: Float = 0.0f) {

  private val shotFrequency: Float = 1/shotsPerSecond.toFloat
  private var shootTimer: Float = 0
  private var triggerHeldLastFrame: Boolean = false
  private var shootingThisFrame: Boolean = false
  private var ammoCount: Int = ammoCapacity
  private val hasAmmoCapacity: Boolean = ammoCapacity != -1
  private var reloadTimer: Float = 0.0f

  def isMagasineEmpty: Boolean = hasAmmoCapacity && ammoCount == 0

  def update(triggerHeld: Boolean, dt: Float): Unit = {
    if(shootTimer > 0)
      shootTimer -= dt

    if(reloadTimer > 0)
      reloadTimer -= dt

    shootingThisFrame = false

    // If we've reached the end of the reload timer, load a full mag
    if(reloadTimer <= 0 && ammoCount == 0)
      ammoCount = (ammoCapacity * holder.stats.attackCapacity).toInt

    if(triggerHeld && canShootThisFrame) {
      shootTimer = shotFrequency * (1.0f/holder.stats.attackSpeed)
      shootingThisFrame = true

      if(hasAmmoCapacity) {
        ammoCount -= 1

        // If we've reached the last round, start reloading
        if(ammoCount == 0)
          reload()
      }

      // TODO: Implement a pool of projectiles to prevent creating a new instance each shot?
      // TODO: Actual projectile firing shouldn't be done here. The holder entity should take care of that
      val shotInaccuracy =
        if(inaccuracy != 0)
          Random.between(-inaccuracy, inaccuracy) * (1.0f / holder.stats.attackAccuracy)
        else
          0.0f

      val proj: Projectile = projectile.copy
      proj.damage *= holder.stats.damage
      proj.velocity = new Vector2(holder.aimVector.x, holder.aimVector.y).nor().scl(700).rotate(shotInaccuracy) // TODO: Projectile speed should be handled in the Projectile class
      holder.parentGameWorld.addGameObject(proj)
    }

    triggerHeldLastFrame = triggerHeld
  }

  def canShootThisFrame: Boolean = {
    val timerOk = shootTimer <= 0
    val triggerOk = (!isFullAuto && !triggerHeldLastFrame) || isFullAuto
    val ammoOK = !hasAmmoCapacity || ammoCount > 0
    timerOk && triggerOk && ammoOK
  }

  def reload(): Unit = {
    ammoCount = 0
    reloadTimer = reloadTime
  }

  def isShootingThisFrame: Boolean = shootingThisFrame
}
