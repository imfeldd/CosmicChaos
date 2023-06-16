package CosmicChaos.Core.Weapons

import CosmicChaos.Entities.CreatureEntity
import com.badlogic.gdx.math.Vector2

import scala.util.Random

class MagicGun(projectile: MagicProjectile, holder: CreatureEntity, spreadAngle: Float, var shotsCount: Int = 2, baseAmmoCapacity: Int = 4, reloadTime: Float = 1.0f, _shotsPerSecond: Int = 16)
  extends Weapon(projectile = projectile, isFullAuto = true, holder = holder, shotsPerSecond = _shotsPerSecond, inaccuracy = 0, baseAmmoCapacity = baseAmmoCapacity, reloadTime = reloadTime) {

  var mageState = 1
  var movingAngle = 0
  override def shootProjectile(): Unit = {
    // Ignore shooting a projectile with a null aimVector, meaning a projectile that wouldn't move
    if (holder.aimVector.len() == 0)
      return

    val directionsCount = 4
    if (mageState == 1) {
      for (i <- 0 until directionsCount) {
        val spread = Random.between(-spreadAngle / 2, spreadAngle / 2)
        val angle = i * (360 / directionsCount) + spread
        val proj: MagicProjectile = projectile.copy.asInstanceOf[MagicProjectile]
        proj.damage *= holder.stats.damage
        proj.mageState = 1
        proj.velocity = new Vector2(holder.aimVector.x, holder.aimVector.y).nor().scl(700).rotate(angle)
        holder.parentGameWorld.addGameObject(proj)
      }
    }
    else {
      shotsPerSecond = 20
      shotsCount = 50
      movingAngle +=20

      val proj: MagicProjectile = projectile.copy.asInstanceOf[MagicProjectile]
      proj.damage *= holder.stats.damage
      proj.mageState = 2
      proj.velocity = new Vector2(holder.position.x , holder.position.y ).nor().scl(300).rotate(movingAngle)

      holder.parentGameWorld.addGameObject(proj)
    }
  }

}
