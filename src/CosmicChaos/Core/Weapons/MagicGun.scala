package CosmicChaos.Core.Weapons

import CosmicChaos.Entities.CreatureEntity
import CosmicChaos.Entities.Enemies.MageBossEntity
import com.badlogic.gdx.math.Vector2


import scala.util.Random

class MagicGun(projectile: MagicProjectile, holder: CreatureEntity, spreadAngle: Float, var shotsCount: Int = 2, baseAmmoCapacity: Int = 4, reloadTime: Float = 1.0f, _shotsPerSecond: Int = 16)
  extends Weapon(projectile = projectile, isFullAuto = true, holder = holder, shotsPerSecond = _shotsPerSecond, inaccuracy = 0, baseAmmoCapacity = baseAmmoCapacity, reloadTime = reloadTime) {

  private val direction = 4
  var mageState = 1
  var movingAngle = 0
  override def shootProjectile(): Unit = {
    //Patch projectile not moving
    if (holder.aimVector.len() == 0) return
    if (mageState == 1) {
      for (i <- 0 until direction) {
        val spread = Random.between(-spreadAngle / 2, spreadAngle / 2)
        val angle = i * (360 / direction) + spread
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
      val angle = 0
      val proj: MagicProjectile = projectile.copy.asInstanceOf[MagicProjectile]
      proj.damage *= holder.stats.damage
      proj.mageState = 2
      proj.velocity = new Vector2(holder.position.x , holder.position.y ).nor().scl(300).rotate(angle+movingAngle)
      holder.parentGameWorld.addGameObject(proj)
    }
  }

}
