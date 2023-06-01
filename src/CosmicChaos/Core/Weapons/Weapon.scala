package CosmicChaos.Core.Weapons

import CosmicChaos.Entities.{Entity, Projectile}

abstract class Weapon {
  val projectile: Projectile
  val oneShot: Boolean
  val fireFrequency: Float
  val holder: Entity
}
