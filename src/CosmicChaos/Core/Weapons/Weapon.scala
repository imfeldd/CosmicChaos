package CosmicChaos.Core.Weapons

import CosmicChaos.Entities.CreatureEntity
import com.badlogic.gdx.math.Vector2

import scala.util.Random

class Weapon(var projectile: Projectile, val isFullAuto: Boolean, var shotsPerSecond: Int, val holder: CreatureEntity, val baseAmmoCapacity: Int = -1, val reloadTime: Float = 0.0f, val inaccuracy: Float = 0.0f) {

  private var shootTimer: Float = 0
  private var triggerHeldLastFrame: Boolean = false
  private var shootingThisFrame: Boolean = false
  protected var ammoCount: Int = baseAmmoCapacity
  protected val hasAmmoCapacity: Boolean = baseAmmoCapacity != -1
  protected var reloadTimer: Float = 0.0f

  def isMagazineEmpty: Boolean = hasAmmoCapacity && ammoCount == 0
  def currentAmmoCount: Int = ammoCount
  def ammoCapacity: Int = (baseAmmoCapacity * holder.stats.attackCapacity).toInt
  def shotFrequency: Float = 1.0f/shotsPerSecond * holder.stats.attackSpeed


  def update(triggerHeld: Boolean, dt: Float): Unit = {
    if(shootTimer > 0)
      shootTimer -= dt

    if(reloadTimer > 0)
      reloadTimer -= dt

    shootingThisFrame = false

    // If we've reached the end of the reload timer, load a full mag
    if(reloadTimer <= 0 && ammoCount == 0)
      ammoCount = ammoCapacity

    if(triggerHeld && canShootThisFrame) {
      shootTimer = shotFrequency
      shootingThisFrame = true

      if(hasAmmoCapacity) {
        ammoCount -= 1

        // If we've reached the last round, start reloading
        if(ammoCount == 0)
          reload()
      }

      shootProjectile()
    }

    triggerHeldLastFrame = triggerHeld
  }

  def shootProjectile(): Unit = {
    // TODO: Implement a pool of projectiles to prevent creating a new instance each shot?
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
