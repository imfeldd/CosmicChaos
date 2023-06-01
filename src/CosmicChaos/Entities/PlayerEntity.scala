package CosmicChaos.Entities

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.KeyboardInterface
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.mutable

class PlayerEntity extends Entity with KeyboardInterface {

  private val imgBitmap = new BitmapImage("data/images/hei-pi.png").getImage
  private val spriteW = imgBitmap.getWidth
  private val spriteH = imgBitmap.getHeight

  private var dirX: Int= 0
  private var dirY: Int = 0
  private var velocityX: Float = 0
  private var velocityY: Float = 0

  override val name: String = "Player"
  override val baseStats: EntityStats = EntityStats(maxHealth = 100, maxSpeed = 250, acceleration = 30, baseDamage = 10)
  override var stats: EntityStats = baseStats

  private val keyStatus: mutable.HashMap[Int, Boolean] = mutable.HashMap[Int, Boolean]()

  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Draw player sprite
    g.draw(imgBitmap, position.x - spriteW/2, position.y - spriteH/2)
  }

  def centerCameraOnPlayer(g: GdxGraphics): Unit = {
    val (screenW, screenH) = (g.getScreenWidth, g.getScreenHeight)
    val (mouseX, mouseY) = (Gdx.input.getX, Gdx.input.getY)
    val (mxRelToCenter, myRelToCenter) = ((mouseX - screenW / 2.0f) / (screenW / 2.0f), (mouseY - screenH / 2.0f) / (screenH / 2.0f))

    g.moveCamera(
      position.x - screenW / 2 + 125 * mxRelToCenter,
      position.y - screenH / 2 - 125 * myRelToCenter
    )
  }

  override def onUpdate(dt: Float): Unit = {
    val upPressed = keyStatus.getOrElse(Input.Keys.W, false)
    val downPressed = keyStatus.getOrElse(Input.Keys.S, false)
    val leftPressed = keyStatus.getOrElse(Input.Keys.A, false)
    val rightPressed = keyStatus.getOrElse(Input.Keys.D, false)

    def getNextDir(currDir: Int, posPressed: Boolean, negPressed: Boolean): Int =
      (currDir, posPressed, negPressed) match {
      case (-1, _, true) => -1
      case (1, true, _) => 1
      case (_, true, true) => 0
      case (_, true, false) => 1
      case (_, false, true) => -1
      case _ => 0
    }

    dirY = getNextDir(dirY, upPressed, downPressed)
    dirX = getNextDir(dirX, rightPressed, leftPressed)

    velocityX += stats.acceleration * dirX
    velocityY += stats.acceleration * dirY

    velocityX -= velocityX * 0.1f
    velocityY -= velocityY * 0.1f

    position.x += velocityX * dt
    position.y += velocityY * dt

    // BULLET TEST
    val leftMouseDown = Gdx.input.isButtonPressed(0)
    if(leftMouseDown) {
      val (screenW, screenH) = (Gdx.graphics.getWidth, Gdx.graphics.getHeight)
      val (mouseX, mouseY) = (Gdx.input.getX, Gdx.input.getY)
      val (mxRelToCenter, myRelToCenter) = ((mouseX - screenW / 2.0f) / (screenW / 2.0f), (mouseY - screenH / 2.0f) / (screenH / 2.0f))
      val projVel = new Vector2(mxRelToCenter, -myRelToCenter).nor.scl(690)
      val projectile = new Projectile(10, projVel, this)
      parentGameWorld.addGameObject(projectile)
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
