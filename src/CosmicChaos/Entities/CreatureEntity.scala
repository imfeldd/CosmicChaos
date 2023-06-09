package CosmicChaos.Entities

import CosmicChaos.Core.Items.Effects.{BeforeDealDamageEffect, DealtDamageEffect, OnGetHitEffect, OnKillEffect}
import CosmicChaos.Utils.Animation
import CosmicChaos.Core.Items.Item
import CosmicChaos.Core.Stats.EntityStats
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

abstract class CreatureEntity extends Entity {

  private val deathExplosionTexture: Texture = new Texture("data/images/diseappear.png")
  private val (frameW, frameH) = (100, 100)
  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(deathExplosionTexture, frameW, frameH)
  protected val deathExplosionAnimation: Animation = new Animation(0.008f, frames(0), loop = false)

  val baseStats: EntityStats
  var stats: EntityStats
  var cash: Float = 0.0f
  val itemsInventory: ArrayBuffer[Item] = new ArrayBuffer[Item]()

  var team: Int = -1

  var aimVector: Vector2 = new Vector2(0, 0)

  var currentHealth: Float = 50
  var deathCause: Entity = _

  def isDead: Boolean = currentHealth <= 0

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)

    stats = baseStats.copy()
    for(itm <- itemsInventory) {
      itm.update(dt)
      itm.modify(stats)
    }

    heal(stats.healthRegenerationAmount * dt)
  }

  override def onEnterGameWorld(): Unit = {
    currentHealth = stats.maxHealth
  }

  def heal(amount: Float): Unit = {
    currentHealth = math.min(stats.maxHealth, currentHealth + amount)
  }

  def dealDamageTo(amount: Float, recipient: CreatureEntity): Unit = {
    // Roll for critical
    var amnt = if(Random.nextFloat() <= math.min(1.0f, stats.criticalChance.value)) amount * 2 else amount
    val crit = amnt != amount

    val beforeDealDamageEffect = itemsInventory.filter(_.isInstanceOf[BeforeDealDamageEffect]).map(_.asInstanceOf[BeforeDealDamageEffect])
    for (i <- beforeDealDamageEffect) {
      amnt = i.beforeDealDamage(amnt, crit, recipient)
    }

    recipient.onReceiveDamage(amnt, this, crit)

    val onDealDamageItems = itemsInventory.filter(_.isInstanceOf[DealtDamageEffect]).map(_.asInstanceOf[DealtDamageEffect])
    for (i <- onDealDamageItems) {
      i.dealtDamage(amnt, crit, recipient)
    }
  }

  def addItemToInventory(item: Item, amount: Int = 1): Unit = {
    // If there's already an item of that type in the inventory, increment the stack size
    // otherwise just add the item to the inventory
    val i = itemsInventory.find(_.name == item.name)
    item.holder = this
    i match {
      case Some(itm) => itm.stackSize += amount
      case None => itemsInventory.addOne(item)
    }
  }

  protected def onReceiveDamage(amount: Float, source: CreatureEntity, wasCrit: Boolean): Unit = {
    if(isDead)
      return

    currentHealth -= amount

    if (isDead) {
      source.onKill(this)
      deathCause = source
      onDeath(source)
    }
    else {
      val onGetHitItems = itemsInventory.filter(_.isInstanceOf[OnGetHitEffect]).map(_.asInstanceOf[OnGetHitEffect])
      for (i <- onGetHitItems) {
        i.gotHit(source, amount, wasCrit)
      }
    }
  }

  protected def onDeath(deathCause: Entity): Unit = {
    parentGameWorld.removeGameObject(this)
  }

  protected def onKill(killed: CreatureEntity): Unit = {
    val onKillEffectItems = itemsInventory.filter(_.isInstanceOf[OnKillEffect]).map(_.asInstanceOf[OnKillEffect])
    for(i <- onKillEffectItems) {
      i.onKill(killed)
    }
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

  protected def drawGun(sprite: Texture, distance: Float, g: GdxGraphics, scale: Float = 1.0f, offset: Vector2 = new Vector2(0, 0)): Unit = {
    val (spriteW, spriteH) = (sprite.getWidth*scale, sprite.getHeight*scale)
    val flipY = if(aimVector.angle() > 90 && aimVector.angle() < 280) -1 else 1
    val gunPos = new Vector2(position.x, position.y).add(new Vector2(aimVector.x, aimVector.y).nor().scl(distance)).add(spriteW / 2, 0)
    val angle = (aimVector.angle/8).toInt * 8.0f  // round the angle to its nearest multiple of 8 to make it feel a bit more retro

    // Draw the gun sprite, rotated around the creature's position
    g.draw(sprite, gunPos.x - spriteW/2 + offset.x, gunPos.y - spriteH/2 + offset.y, 0, spriteH/2, spriteW, spriteH, 1, flipY, angle, 0, 0, sprite.getWidth, sprite.getHeight, false, false)
  }

  protected def drawDeathExplosionAnimation(g: GdxGraphics): Unit = {
    deathExplosionAnimation.update(Gdx.graphics.getDeltaTime)
    val scale = 3.0f
    g.draw(deathExplosionAnimation.getCurrentFrame, position.x - frameW*scale/2, position.y - frameH*scale/2, frameW * scale, frameH * scale)
  }
}
