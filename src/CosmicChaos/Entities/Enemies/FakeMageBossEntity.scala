package CosmicChaos.Entities.Enemies

import CosmicChaos.Core.Items.Debuff.FlipMovement
import CosmicChaos.Core.{Collideable, GameObject, Renderable, Spatial}
import CosmicChaos.Core.Stats.{EntityStats, EntityStatsScaling}
import CosmicChaos.Core.Weapons.{MagicGun, MagicProjectile}
import CosmicChaos.Entities.CreatureEntity
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, Vector2, Vector3}

import scala.util.Random

class FakeMageBossEntity(parent: MageBossEntity, appearDelay: Float = 0.0f) extends CreatureEntity with GameObject with Collideable with Renderable with Spatial{
  private val adjWeapon = new MagicGun(new MagicProjectile(1,this), this, 50, 5, 99, 3, 2)

  override val baseStats: EntityStats = new EntityStats(
    maxHealth = 200,
    maxSpeed = 12,
    acceleration = 10,
    damage = 5,
    criticalChance = 0,
    attackSpeed = 1
  )
  override var statsScaling: EntityStatsScaling = EntityStatsScaling(
    maxHealthPerLevel = 65.0f,
    damagePerLevel = 8.0f,
    healthRegenPerItem = 0.0f
  )

  override var stats: EntityStats = baseStats
  override val name: String = "Fake Mage"
  override val nameSubtitle: String = "My fellow siblings"

  collisionLayer = CollisionLayers.enemy
  collisionMask = CollisionLayers.none
  cash = 10

  private val spriteScale = 3.0f
  private var teleportingTimer = 0.0f
  private val inversorItem = new FlipMovement
  private val fakeMage: Texture = new Texture("data/images/boss/magicMage/BossThirdForm.png")
  fakeMage.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (fakeMageFrameW, fakeMageFrameH) = (fakeMage.getWidth / 8, fakeMage.getHeight)
  private val fakeMageFrames: Array[Array[TextureRegion]] = TextureRegion.split(fakeMage, fakeMageFrameW, fakeMageFrameH)
  private val fakeMageAnimation = new Animation(0.09f, fakeMageFrames(0).tail, loop = true)
  private val collBoxSize: Vector2 = new Vector2(50 * spriteScale, 70 * spriteScale)
  override val collisionBox: Rectangle = new Rectangle((-fakeMageFrameW * spriteScale + collBoxSize.x) / 2, (-fakeMageFrameH * spriteScale + collBoxSize.y) / 2, collBoxSize.x, collBoxSize.y)
  private var phaseTimer: Float = 0.0f
  val newPos: Vector2 = new Vector2(1, 1).rotate(Random.between(0, 360)).nor().scl(Random.between(500, 600))

  override def onEnterGameWorld(): Unit = {
    parentGameWorld.playerEntity.addItemToInventory(inversorItem)

  }
  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)
    phaseTimer += dt
    adjWeapon.update(triggerHeld = true, dt)
    val (a, b) = (parentGameWorld.playerEntity.position, position)
    val vecToPlayer = new Vector2(a.x - b.x, a.y - b.y)
    aimVector = vecToPlayer
    teleportingTimer -= dt
    if (vecToPlayer.len() > 800 && teleportingTimer <= 0.0f) {
      // Move towards the player if we're too far away
      position = new Vector3(parentGameWorld.playerEntity.position.x, parentGameWorld.playerEntity.position.y, 0).add(new Vector3(newPos.x, newPos.y, 0))
      teleportingTimer = 3.0f
    }
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    if (!isDead) {
      fakeMageAnimation.update(Gdx.graphics.getDeltaTime)
      drawSprite(fakeMageAnimation.getCurrentFrame, g, spriteScale)
    }
    else drawDeathExplosionAnimation(g)
  }
}


