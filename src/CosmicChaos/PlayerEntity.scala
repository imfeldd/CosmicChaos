package CosmicChaos
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.KeyboardInterface
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

  override val baseStats: Stats = Stats(health = 100, maxSpeed = 250, acceleration = 30)
  override var stats: Stats = baseStats

  private val keyStatus: mutable.HashMap[Int, Boolean] = mutable.HashMap[Int, Boolean]()

  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Draw player sprite
    g.draw(imgBitmap, x - spriteW/2, y - spriteH/2)

    cameraStuff(g)
  }

  private def cameraStuff(g: GdxGraphics): Unit = {
    val (screenW, screenH) = (g.getScreenWidth, g.getScreenHeight)
    val (mouseX, mouseY) = (Gdx.input.getX, Gdx.input.getY)
    val (mxRelToCenter, myRelToCenter) = ((mouseX - screenW / 2.0f) / (screenW / 2.0f), (mouseY - screenH / 2.0f) / (screenH / 2.0f))

    g.moveCamera(
      x - screenW / 2 + 125 * mxRelToCenter,
      y - screenH / 2 - 125 * myRelToCenter
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

    x += velocityX * dt
    y += velocityY * dt
  }

  override def onKeyDown(i: Int): Unit = {
    keyStatus(i) = true
  }

  override def onKeyUp(i: Int): Unit = {
    keyStatus(i) = false
  }
}
