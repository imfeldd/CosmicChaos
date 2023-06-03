package CosmicChaos.Entities

import CosmicChaos.Core.Weapons.Weapon
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.math.{Rectangle, Vector2, Vector3}

class GunnerEnemyEntity extends CreatureEntity {
  override val name: String = "Gunner"
  override val baseStats: EntityStats = EntityStats(100, 12, 10, 100)
  override var stats: EntityStats = baseStats
  override val collisionBox: Rectangle = new Rectangle(-32, -32, 64, 64)

  private val weapon: Weapon = new Weapon(new Projectile(10, this), true, 4, this, ammoCapacity = 3, reloadTime = 1.5f) {}
  private var aggro: Entity = null

  private val gunTexture = new BitmapImage("data/images/weapons/gun.png").getImage
  gunTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.drawRectangle(position.x, position.y, 64, 64, 0)
    drawGun(gunTexture, 10, g, 2)
  }

  override def onUpdate(dt: Float): Unit = {
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
      position.add(new Vector3(aimVector.x, aimVector.y, 0).scl(90*dt))
    }
    else {
      // Shoot at the player
      // TODO: Lead the shot if the player is moving
      weapon.update(triggerHeld = true, dt)
    }
  }

  override def onReceiveDamage(amount: Float, source: Entity): Unit = {
    super.onReceiveDamage(amount, source)
    aggro = source
  }
}
