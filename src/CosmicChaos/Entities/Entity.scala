package CosmicChaos.Entities

import CosmicChaos.Core.{GameObject, Renderable, Spatial, Stats}
import ch.hevs.gdx2d.lib.GdxGraphics

abstract class Entity extends GameObject with Renderable with Spatial {
  val name: String
  val baseStats: Stats
  var stats: Stats

  var team: Int = -1

  var currentHealth: Float = 0
  def isDead: Boolean = currentHealth <= 0

  override def onInit = {
    currentHealth = stats.maxHealth
  }
  override def onUpdate(dt: Float): Unit = {}
  override def onGraphicRender(g: GdxGraphics): Unit = {}

  def dealDamageTo(amount: Int, recipient: Entity): Unit = {
    recipient.onReceiveDamage(amount, this)
  }

  protected def onReceiveDamage(amount: Int, source: Entity): Unit = {
    currentHealth -= amount

    if(isDead) {
      onDeath(source)
    }
  }

  protected def onDeath(deathCause: Entity): Unit = { }
}
