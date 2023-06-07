package CosmicChaos.Entities.Enemies

import CosmicChaos.Core.Weapons.Weapon
import CosmicChaos.Entities.{CreatureEntity, Entity}
import com.badlogic.gdx.math.{Rectangle, Vector2, Vector3}

abstract class GunnerEnemyEntity extends CreatureEntity {
  override val name: String = "Gunner"
  override val collisionBox: Rectangle = new Rectangle(-32, -32, 64, 64)

  protected val weapon: Weapon
  protected var aggro: Entity = null

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)

    if(aggro == null)
      // Aggro the player by default
      aggro = parentGameWorld.playerEntity
    else aggro match {
      case entity: CreatureEntity if entity.isDead => aggro = parentGameWorld.playerEntity
      case _ =>
    }

    val (a, b) = (aggro.position, position)
    val vecToPlayer = new Vector2(a.x - b.x, a.y - b.y)

    aimVector = vecToPlayer

    if(vecToPlayer.len() < 500) {
      // Move away from player if we're too close
      aimVector = vecToPlayer.rotate(180).nor
      position.add(new Vector3(aimVector.x, aimVector.y, 0).scl(stats.maxSpeed*dt))
    }
    else {
      // Shoot at the player
      // TODO: Lead the shot if the player is moving
      weapon.update(triggerHeld = true, dt)
    }
  }

  protected override def onReceiveDamage(amount: Float, source: CreatureEntity): Unit = {
    super.onReceiveDamage(amount, source)
    aggro = source
  }
}
