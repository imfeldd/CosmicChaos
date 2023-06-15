package CosmicChaos.Core.Weapons

import CosmicChaos.Entities.CreatureEntity
import com.badlogic.gdx.math.Vector2

class Shotgun(projectile: Projectile, holder: CreatureEntity, spreadAngle: Float, shotsCount: Int = 2, baseAmmoCapacity: Int = 4, reloadTime: Float = 1.0f, shotsPerSecond: Int = 16)
  extends Weapon(projectile = projectile, isFullAuto = false, holder = holder, shotsPerSecond = shotsPerSecond, inaccuracy = 0, baseAmmoCapacity = baseAmmoCapacity, reloadTime = reloadTime) {

  override def ammoCapacity: Int = shotsCount
  protected def realAmmoCapacity: Int = (baseAmmoCapacity * holder.stats.attackCapacity).toInt

  override def shootProjectile(): Unit = {
    for(i <- 0 until realAmmoCapacity) {
      val angle = i*(spreadAngle/realAmmoCapacity) - spreadAngle/2
      val proj: Projectile = projectile.copy
      proj.damage *= holder.stats.damage
      proj.velocity = new Vector2(holder.aimVector.x, holder.aimVector.y).nor().scl(700).rotate(angle)
      holder.parentGameWorld.addGameObject(proj)
    }
  }
}
