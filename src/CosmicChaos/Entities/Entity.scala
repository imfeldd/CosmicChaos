package CosmicChaos.Entities

import CosmicChaos.Core.{GameObject, Renderable, Spatial}
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.math.{Vector2, Vector3}

abstract class Entity extends GameObject with Renderable with Spatial {
  val name: String
  val baseStats: EntityStats
  var stats: EntityStats

  var team: Int = -1

  var aimVector: Vector3 = Vector3.Zero
  var velocity: Vector2 = Vector2.Zero

  var currentHealth: Float = 50
  def isDead: Boolean = currentHealth <= 0

  override def onEnterGameWorld(): Unit = {
    currentHealth = stats.maxHealth
  }
  override def onUpdate(dt: Float): Unit = {}
  override def onGraphicRender(g: GdxGraphics): Unit = {}

  def dealDamageTo(amount: Float, recipient: Entity): Unit = {
    recipient.onReceiveDamage(amount, this)
  }

  protected def onReceiveDamage(amount: Float, source: Entity): Unit = {
    currentHealth -= amount

    if(isDead) {
      onDeath(source)
    }
  }

  protected def onDeath(deathCause: Entity): Unit = { }
}
