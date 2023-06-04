package CosmicChaos.Entities

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

abstract class CreatureEntity extends Entity {
  val baseStats: EntityStats
  var stats: EntityStats

  var team: Int = -1

  var aimVector: Vector2 = new Vector2(0, 0)

  var currentHealth: Float = 50
  var deathCause: Entity = _

  def isDead: Boolean = currentHealth <= 0

  override def onEnterGameWorld(): Unit = {
    currentHealth = stats.maxHealth
  }

  def dealDamageTo(amount: Float, recipient: CreatureEntity): Unit = {
    recipient.onReceiveDamage(amount, this)
  }

  protected def onReceiveDamage(amount: Float, source: Entity): Unit = {
    if(isDead)
      return

    currentHealth -= amount

    if (isDead) {
      onDeath(source)
      deathCause = source
    }
  }

  protected def onDeath(deathCause: Entity): Unit = {
    parentGameWorld.removeGameObject(this)
  }

  protected def drawSprite(sprite: Texture, g: GdxGraphics, scale: Float = 1.0f): Unit = {
    val (spriteW, spriteH) = (sprite.getWidth*scale, sprite.getHeight*scale)
    val flipX = aimVector.angle() > 90 && aimVector.angle() < 280
    g.draw(sprite, position.x - spriteW / 2, position.y - spriteH / 2, spriteW, spriteH, 0, 0, sprite.getWidth, sprite.getHeight, flipX, false)
  }

  protected def drawSprite(sprite: TextureRegion, g: GdxGraphics, scale: Float): Unit = {
    val (spriteW, spriteH) = (sprite.getRegionWidth * scale, sprite.getRegionHeight * scale)
    val flipX = aimVector.angle() > 90 && aimVector.angle() < 280
    g.draw(sprite.getTexture, position.x - spriteW / 2, position.y - spriteH / 2, spriteW, spriteH, sprite.getRegionX, sprite.getRegionY, sprite.getRegionWidth, sprite.getRegionHeight, flipX, false)
  }

  protected def drawGun(sprite: Texture, distance: Float, g: GdxGraphics, scale: Float = 1.0f): Unit = {
    val (spriteW, spriteH) = (sprite.getWidth*scale, sprite.getHeight*scale)
    val flipY = aimVector.angle() > 90 && aimVector.angle() < 280
    val gunPos = new Vector2(position.x, position.y).add(new Vector2(aimVector.x, aimVector.y).nor().scl(distance)).add(spriteW / 2, 0)
    val angle = (aimVector.angle/8).toInt * 8.0f  // round the angle to its nearest multiple of 8 to make it feel a bit more retro
    g.draw(sprite, gunPos.x - spriteW/2, gunPos.y - spriteW/4, 22, 10, spriteW, spriteH, 1, 1, angle, 0, 0, sprite.getWidth, sprite.getHeight, true, flipY)
  }
}