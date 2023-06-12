package CosmicChaos.Entities

import CosmicChaos.Core.{Collideable, Interactable}
import CosmicChaos.Core.Items.Item
import CosmicChaos.Core.Stats.EntityStats
import CosmicChaos.Core.Weapons.{Projectile, Weapon}
import CosmicChaos.HUD.GameplayHUD
import CosmicChaos.Screens.GameScreen
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.components.audio.MusicPlayer
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.KeyboardInterface
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.math.{Circle, Rectangle, Vector2, Vector3}
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.mutable

class PlayerEntity extends CreatureEntity with KeyboardInterface {

  private val gunTexture = new BitmapImage("data/images/weapons/gun.png").getImage
  gunTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  private val (frameW, frameH) = (48, 48)
  private val spriteScale = 3.0f

  private val runSpritesheet: Texture = new Texture("data/images/entities/spacemarine_run.png")
  private val runFrames: Array[Array[TextureRegion]] = TextureRegion.split(runSpritesheet, frameW, frameH)
  private val runAnimation = new Animation(0.066f, runFrames(0).tail, loop = true)

  private val deathSpritesheet: Texture = new Texture("data/images/entities/spacemarine_die.png")
  private val deathFrames: Array[Array[TextureRegion]] = TextureRegion.split(deathSpritesheet, frameW, frameH)
  private val deathAnimation = new Animation(0.120f, deathFrames(0), loop = false)

  val weapon: Weapon = new Weapon(new Projectile(.7f, this), true, 14, this, inaccuracy = 4.5f, baseAmmoCapacity = 14, reloadTime = 0.66f) {}


  override val name: String = "Player"
  override val baseStats: EntityStats = new EntityStats(
    maxHealth = 100,
    maxSpeed = 550,
    acceleration = 40,
    damage = 15,
    criticalChance = 0.02f,
    attackSpeed = 1.0f,
    healthRegenAmount = 1.0f
  )
  override var stats: EntityStats = baseStats

  collisionLayer = CollisionLayers.player
  collisionMask = CollisionLayers.world + CollisionLayers.props + CollisionLayers.interactable

  private var lastPos: Vector3 = new Vector3(0, 0, 0)

  private val collBoxSize: Vector2 = new Vector2(25*spriteScale, 30*spriteScale)
  override val collisionBox: Rectangle = new Rectangle((-frameW*spriteScale + collBoxSize.x)/2, (-frameH*spriteScale + collBoxSize.y)/2, collBoxSize.x, collBoxSize.y)
  var interactableOfInterest: Option[Interactable] = None

  private val keyStatus: mutable.HashMap[Int, Boolean] = mutable.HashMap[Int, Boolean]()

  override def onEnterGameWorld(): Unit = {
    super.onEnterGameWorld()
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    //position.x = positionInAnotherWorld.x
    //position.y = positionInAnotherWorld.y

    val sprite = if(isDead) {
      deathAnimation.update(Gdx.graphics.getDeltaTime)
      deathAnimation.getCurrentFrame
    }
    else {
      runAnimation.update(Gdx.graphics.getDeltaTime)
      if (velocity.len() <= 75.0f) {
        // Stop animating when slowing down
        runAnimation.reset()  // Reset the animation so we start from the first frame when we start moving
        runFrames(0)(0)       // Frame #0 is character standing still
      } else {
        runAnimation.getCurrentFrame
      }
    }

    // Make it look like the character is running backwards if we're aiming in the other direction we're moving
    runAnimation.reverse = math.abs(aimVector.angle(velocity)) > 90.0f

    if(!isDead) {
      drawGun(gunTexture, 6, g, scale = 2, offset = new Vector2(0, 5))
    }

    drawSprite(sprite, g, spriteScale)
    g.drawFilledCircle(position.x, position.y, 2, Color.PINK)
  }

  def centerCameraOnPlayer(g: GdxGraphics): Unit = {
    val (screenW, screenH) = (g.getScreenWidth*g.getCamera.zoom, g.getScreenHeight*g.getCamera.zoom)
    val (mouseX, mouseY) = (Gdx.input.getX, Gdx.input.getY)
    val (mxRelToCenter, myRelToCenter) = ((mouseX - screenW / 2.0f) / (screenW / 2.0f), (mouseY - screenH / 2.0f) / (screenH / 2.0f))

    g.moveCamera(
      position.x - screenW / 2 + 125 * mxRelToCenter,
      position.y - screenH / 2 - 125 * myRelToCenter
    )

    // Get the vector that points from the player's position to the mouse position
    if(!isDead) {
      val aimVector3 = g.getCamera.unproject(new Vector3(mouseX, mouseY, 0)).sub(position)
      aimVector = new Vector2(aimVector3.x, aimVector3.y)
    }
  }

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)

    interactableOfInterest = None

    lastPos = new Vector3(position.x, position.y, 0)

    doMovement(dt)

    if(isDead)
      return

    doShooting(dt)

    parentGameWorld.getCollideablesWithinCircle(new Circle(position.x, position.y, 40))
      .find(x => x.isInstanceOf[Interactable] && x.asInstanceOf[Interactable].isInteractable)
      .map(_.asInstanceOf[Interactable])
      .foreach(i => {
        interactableOfInterest = Some(i)
        if (keyStatus.getOrElse(Input.Keys.E, false))
          i.interact(this)
      })
  }

  private def doMovement(dt: Float): Unit = {
    val upPressed = keyStatus.getOrElse(Input.Keys.W, false)
    val downPressed = keyStatus.getOrElse(Input.Keys.S, false)
    val leftPressed = keyStatus.getOrElse(Input.Keys.A, false)
    val rightPressed = keyStatus.getOrElse(Input.Keys.D, false)

    // Get acceleration based on input
    val acceleration = if(!isDead) new Vector2(
      (if (rightPressed) 1 else 0) + (if (leftPressed) -1 else 0),
      (if (upPressed) 1 else 0) + (if (downPressed) -1 else 0)
    ) else new Vector2(0, 0)

    // Apply deceleration if there's no acceleration
    if(acceleration.len() == 0.0f) {
      velocity = velocity.scl(0.9f)
    }

    // Apply acceleration to velocity
    velocity = velocity.add(acceleration.scl(stats.acceleration))

    // Limit speed to max speed
    velocity.clamp(0, stats.maxSpeed)

    // Update position based on velocity
    position.x += velocity.x * dt
    position.y += velocity.y * dt
  }

  private def doShooting(dt: Float): Unit = {
    if(keyStatus.getOrElse(Input.Keys.R, false)) {
      weapon.reload()
    }

    val leftMouseDown = Gdx.input.isButtonPressed(0) || keyStatus.getOrElse(Input.Keys.X, false)
    weapon.update(leftMouseDown, dt)
    if (weapon.isShootingThisFrame) {
      GameScreen.cameraShake = .5f
    }
  }

  override def addItemToInventory(item: Item, amount: Int): Unit = {
    super.addItemToInventory(item, amount)
    GameplayHUD.showItemNotification(item)
  }

  protected override def onReceiveDamage(amount: Float, source: CreatureEntity, wasCrit: Boolean): Unit = {
    if(source == this)
      return

    super.onReceiveDamage(amount, source, wasCrit)
  }

  override def onKill(killed: CreatureEntity): Unit = {
    super.onKill(killed)

    cash += killed.cash
  }

  override def onDeath(deathCause: Entity): Unit = {

    val soundEffect = new MusicPlayer("data/music/death.wav")
    soundEffect.play()
  }

  override def onKeyDown(i: Int): Unit = {
    keyStatus(i) = true
  }

  override def onKeyUp(i: Int): Unit = {
    keyStatus(i) = false
  }

  override def onCollideWith(other: Collideable): Unit = {
    super.onCollideWith(other)
    if((other.collisionLayer & CollisionLayers.world) != 0) {
      position = lastPos
    }
  }
}
