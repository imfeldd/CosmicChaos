package CosmicChaos.Entities

import CosmicChaos.Core.Weapons.Weapon
import CosmicChaos.Screens.GameScreen
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.KeyboardInterface
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.mutable

class PlayerEntity extends Entity with KeyboardInterface {

  private val imgBitmap = new BitmapImage("data/images/hei-pi.png").getImage
  private val spriteW = imgBitmap.getWidth
  private val spriteH = imgBitmap.getHeight

  private var weapon: Weapon = new Weapon(new Projectile(10, this), true, 14, this) {}

  override val name: String = "Player"
  override val baseStats: EntityStats = EntityStats(maxHealth = 100, maxSpeed = 550, acceleration = 40, baseDamage = 10)
  override var stats: EntityStats = baseStats

  private val keyStatus: mutable.HashMap[Int, Boolean] = mutable.HashMap[Int, Boolean]()

  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Draw player sprite
    g.draw(imgBitmap, position.x - spriteW/2, position.y - spriteH/2)
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
    aimVector = g.getCamera.unproject(new Vector3(mouseX, mouseY, 0)).sub(position)
  }

  override def onUpdate(dt: Float): Unit = {
    val upPressed = keyStatus.getOrElse(Input.Keys.W, false)
    val downPressed = keyStatus.getOrElse(Input.Keys.S, false)
    val leftPressed = keyStatus.getOrElse(Input.Keys.A, false)
    val rightPressed = keyStatus.getOrElse(Input.Keys.D, false)

    // Deceleration
    velocity = velocity.scl(0.9f)

    // Get acceleration based on input
    val acceleration = new Vector2(
      (if (rightPressed) 1 else 0) + (if (leftPressed) -1 else 0),
      (if (upPressed) 1 else 0) + (if (downPressed) -1 else 0)
    )

    // Apply acceleration to velocity
    velocity = velocity.add(acceleration.scl(stats.acceleration))

    // Limit speed to max speed
    velocity.clamp(0, stats.maxSpeed)

    // Update position based on velocity
    position.x += velocity.x * dt
    position.y += velocity.y * dt

    val leftMouseDown = Gdx.input.isButtonPressed(0)
    weapon.update(leftMouseDown, dt)
    if(weapon.isShootingThisFrame) {
      GameScreen.cameraShake = .5f
    }
  }

  override def onDeath(deathCause: Entity): Unit = {
    super.onDeath(deathCause)
  }

  override def onKeyDown(i: Int): Unit = {
    keyStatus(i) = true
  }

  override def onKeyUp(i: Int): Unit = {
    keyStatus(i) = false
  }
}
