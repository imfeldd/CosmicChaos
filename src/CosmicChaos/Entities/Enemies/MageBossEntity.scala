package CosmicChaos.Entities.Enemies

import CosmicChaos.Core.Stats.EntityStats
import CosmicChaos.Core.Weapons.{MagicProjectile, ShadowProjectile, MagicGun, Weapon}
import CosmicChaos.Entities.CreatureEntity
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, Vector2, Vector3}

import scala.util.Random

class MageBossEntity extends CreatureEntity{
  private val adjWeapon = new MagicGun(new MagicProjectile(1,this), this, 50, 5, 99, 3, 2)

  override val baseStats: EntityStats = new EntityStats(
    maxHealth = 1000,
    maxSpeed = 12,
    acceleration = 10,
    damage = 30,
    criticalChance = 0,
    attackSpeed = 1
  )

  override var stats: EntityStats = baseStats
  override val name: String = "Cosmic Mage"
  override val nameSubtitle: String = "Ruler of this world"

  collisionLayer = CollisionLayers.enemy
  collisionMask = CollisionLayers.none
  cash = 777

  private val spriteScale = 5.5f
  private var teleportingTimer = 0.0f

  private val  firstPhaseMage: Texture = new Texture("data/images/boss/magicMage/BossFirstForm.png")
  firstPhaseMage.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (firstFrameW,firstFrameH ) = ( firstPhaseMage.getWidth/8, firstPhaseMage.getHeight)
  private val firstFrames: Array[Array[TextureRegion]] = TextureRegion.split(firstPhaseMage,firstFrameW, firstFrameH)
  private val firstAnimation = new Animation(0.09f, firstFrames(0).tail, loop = true )

  private val secondPhaseMage: Texture = new Texture("data/images/boss/magicMage/BossSecondForm.png")
  secondPhaseMage.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (secondFrameW, secondFrameH) = (secondPhaseMage.getWidth / 8, secondPhaseMage.getHeight)
  private val secondFrames: Array[Array[TextureRegion]] = TextureRegion.split(secondPhaseMage, secondFrameW, secondFrameH)
  private val secondAnimation = new Animation(0.09f, secondFrames(0).tail, loop = true)

  private val thirdPhaseMage :Texture = new Texture("data/images/boss/magicMage/BossThirdForm.png")
  thirdPhaseMage.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (thirdFrameW, thirdFrameH) = (thirdPhaseMage.getWidth / 8, thirdPhaseMage.getHeight)
  private val thirdFrames: Array[Array[TextureRegion]] = TextureRegion.split(thirdPhaseMage, thirdFrameW, thirdFrameH)
  private val thirdAnimation = new Animation(0.5f, thirdFrames(0).tail, loop = true)

  private val collBoxSize: Vector2 = new Vector2(50 * spriteScale, 70 * spriteScale)
  override val collisionBox: Rectangle = new Rectangle((-firstFrameW * spriteScale + collBoxSize.x) / 2, (-firstFrameH * spriteScale + collBoxSize.y) / 2, collBoxSize.x, collBoxSize.y)
  private var phaseTimer: Float = 0.0f
  private var floatTimer: Float = 0.0f
  private var damageTakenThisPhase: Float = 0.0f
  val newPos: Vector2 = new Vector2(1, 1).rotate(Random.between(0, 360)).nor().scl(Random.between(500, 600))
  var limit = 0
  override def onEnterGameWorld(): Unit = {
    setLevel(30)
    super.onEnterGameWorld()
  }

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)
    phaseTimer += dt

    adjWeapon.update(triggerHeld = true, dt)
    val (a, b) = (parentGameWorld.playerEntity.position, position)
    val vecToPlayer = new Vector2(a.x - b.x, a.y - b.y)
    aimVector = vecToPlayer
    teleportingTimer -= dt


    if (currentHealth > baseStats.maxHealth * 0.8) {
      adjWeapon.mageState = 1
    } else if (currentHealth < baseStats.maxHealth * 0.8 && currentHealth > baseStats.maxHealth * 0.5) {
      adjWeapon.mageState = 2
    }
    else {
      adjWeapon.mageState = 3

      if ( limit < 2 ) {
        val fakeMage = new FakeMageBossEntity(this, appearDelay = Random.between(8f, 10f))
        val aroundPos: Vector2 = new Vector2(1, 1).rotate(Random.between(0, 360)).nor().scl(Random.between(120, 900))
        fakeMage.position = new Vector3(parentGameWorld.playerEntity.position.x, parentGameWorld.playerEntity.position.y, 0)
        fakeMage.position.add(new Vector3(aroundPos.x, aroundPos.y, 0))
        parentGameWorld.addGameObject(fakeMage)
        limit +=1
      }

    }

    if (vecToPlayer.len() > 800 && teleportingTimer <= 0.0f) {
      // Move towards the player if we're too far away
      position = new Vector3(parentGameWorld.playerEntity.position.x, parentGameWorld.playerEntity.position.y, 0).add(new Vector3(newPos.x, newPos.y, 0))
      teleportingTimer = 3.0f
    }


  }
  override def onGraphicRender(g: GdxGraphics): Unit = {
    if (!isDead) {

      if (currentHealth > baseStats.maxHealth * 0.8) {
        firstAnimation.update(Gdx.graphics.getDeltaTime)
        drawSprite(firstAnimation.getCurrentFrame, g, spriteScale)
      } else if (currentHealth < baseStats.maxHealth * 0.8 && currentHealth > baseStats.maxHealth * 0.5) {
        secondAnimation.update(Gdx.graphics.getDeltaTime)
        drawSprite(secondAnimation.getCurrentFrame, g, spriteScale)
      }
      else {
        thirdAnimation.update(Gdx.graphics.getDeltaTime)
        drawSprite(thirdAnimation.getCurrentFrame, g, spriteScale)


      }
    }
    else drawDeathExplosionAnimation(g)
  }

}


