package CosmicChaos.Entities

import CosmicChaos.Core.Weapons.Weapon
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.math.{Vector2, Vector3}

class GunnerEnemyEntity extends Entity {
  override val name: String = "Gunner"
  override val baseStats: EntityStats = EntityStats(100, 12, 10, 9999999)
  override var stats: EntityStats = baseStats

  private val weapon: Weapon = new Weapon(Projectile(10, this), true, 4, this, ammoCapacity = 3, reloadTime = 1.5f) {}

  private val gunTexture = new BitmapImage("data/images/weapons/gun.png").getImage
  gunTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.drawRectangle(position.x - 32, position.y - 32, 64, 64, 0)
    drawGun(gunTexture, 10, g, 2)
  }

  override def onUpdate(dt: Float): Unit = {
    val (a, b) = (parentGameWorld.playerEntity.position, position)
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

  override def onReceiveDamage(amount: Float, source: Entity): Unit = {}
}
