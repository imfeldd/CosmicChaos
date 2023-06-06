package CosmicChaos.Entities

import CosmicChaos.Core.Weapons.{Rocket, Weapon}
import CosmicChaos.Screens.GameScreen
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.KeyboardInterface
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, Vector2, Vector3}
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.mutable

class PlayerEntity extends CreatureEntity with KeyboardInterface {

  private val gunTexture = new BitmapImage("data/images/weapons/gun.png").getImage
  gunTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  private val spritesheet: Texture = new Texture("data/images/entities/spacemarine_run.png")
  private val deathSpritesheet: Texture = new Texture("data/images/entities/spacemarine_die.png")
  private val (frameW, frameH) = (48, 48)
  private val spriteScale = 3.0f
  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(spritesheet, frameW, frameH)
  private val deathFrames: Array[Array[TextureRegion]] = TextureRegion.split(deathSpritesheet, frameW, frameH)
  private val frameTime: Float = 0.066f
  private var animCounter: Float = 0.0f

  //private val weapon: Weapon = new Weapon(new Projectile(10, this), true, 14, this, inaccuracy = 4.5f, ammoCapacity = 14, reloadTime = 0.66f) {}

  private val weapon: Weapon = new Weapon(
    new Rocket(64, this),
    isFullAuto = false,
    shotsPerSecond = 3,
    ammoCapacity = 3,
    reloadTime = 1.33f,
    holder = this
  ) {}

  override val name: String = "Player"
  override val baseStats: EntityStats = EntityStats(maxHealth = 100, maxSpeed = 550, acceleration = 40, baseDamage = 10, criticalChance = 0.02f)
  override var stats: EntityStats = baseStats
  private val collBoxSize: Vector2 = new Vector2(25*spriteScale, 30*spriteScale)
  override val collisionBox: Rectangle = new Rectangle((-frameW*spriteScale + collBoxSize.x)/2, (-frameH*spriteScale + collBoxSize.y)/2, collBoxSize.x, collBoxSize.y)

  private val keyStatus: mutable.HashMap[Int, Boolean] = mutable.HashMap[Int, Boolean]()

  override def onGraphicRender(g: GdxGraphics): Unit = {
    val sprite = if(isDead) {
      deathFrames(0)(math.min(deathFrames(0).length - 1, animCounter*0.5 / frameTime).toInt)
    }
    else {
      frames(0)(if (velocity.len() <= 75.0f) {
        // If we're almost stopped, show the idle frame
        animCounter = 0
        0
      } else {
        // Show the frame corresponding to the current animCounter
        (animCounter / frameTime).toInt % (frames(0).length - 1) + 1 // skip frame 0
      })
    }

    if(!isDead) {
      drawGun(gunTexture, 8, g, scale = 2, offset = new Vector2(0, 5))

      if(weapon.isMagasineEmpty) {
        g.drawString(position.x, position.y + 50, "RELOADING", 1)
      }
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
    val aimVector3 = g.getCamera.unproject(new Vector3(mouseX, mouseY, 0)).sub(position)
    aimVector = new Vector2(aimVector3.x, aimVector3.y)
  }

  override def onUpdate(dt: Float): Unit = {
    animCounter += dt

    doMovement(dt)

    if(isDead)
      return

    doShooting(dt)
  }

  private def doMovement(dt: Float): Unit = {
    val upPressed = keyStatus.getOrElse(Input.Keys.W, false)
    val downPressed = keyStatus.getOrElse(Input.Keys.S, false)
    val leftPressed = keyStatus.getOrElse(Input.Keys.A, false)
    val rightPressed = keyStatus.getOrElse(Input.Keys.D, false)

    // Deceleration
    velocity = velocity.scl(0.9f)

    // Get acceleration based on input
    val acceleration = if(!isDead) new Vector2(
      (if (rightPressed) 1 else 0) + (if (leftPressed) -1 else 0),
      (if (upPressed) 1 else 0) + (if (downPressed) -1 else 0)
    ) else new Vector2(0, 0)

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

    val leftMouseDown = Gdx.input.isButtonPressed(0)
    weapon.update(leftMouseDown, dt)
    if (weapon.isShootingThisFrame) {
      GameScreen.cameraShake = .5f
    }
  }

  override def onReceiveDamage(amount: Float, source: Entity): Unit = {
    if(source == this)
      return

    super.onReceiveDamage(amount, source)
  }

  override def onDeath(deathCause: Entity): Unit = {
    animCounter = 0.0f  // Reset the animCounter so that death anim starts at the first frame
  }

  override def onKeyDown(i: Int): Unit = {
    keyStatus(i) = true
  }

  override def onKeyUp(i: Int): Unit = {
    keyStatus(i) = false
  }
}
