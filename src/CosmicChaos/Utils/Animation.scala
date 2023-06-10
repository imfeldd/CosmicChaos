package CosmicChaos.Utils

import com.badlogic.gdx.graphics.g2d.TextureRegion

class Animation(val frameTime: Float, val frames: Array[TextureRegion], var loop: Boolean = true, var reverse: Boolean = false) {

  val framesCount: Int = frames.length
  var animationCounter: Float = 0.0f

  def update(dt: Float): Unit = {
    animationCounter += dt
  }

  def getFrameIndex(t: Float): Int = {
    val frameIndex = if(loop) {
      (t / frameTime).toInt % framesCount
    }
    else {
      math.min(framesCount - 1, t / frameTime).toInt
    }

    if(reverse) {
      framesCount - frameIndex - 1
    }
    else {
      frameIndex
    }
  }

  def getFrame(t: Float): TextureRegion = {
    frames(getFrameIndex(t))
  }

  def getCurrentFrameIndex: Int = {
    getFrameIndex(animationCounter)
  }

  def getCurrentFrame: TextureRegion = {
    frames(getCurrentFrameIndex)
  }

  def reset(): Unit = {
    animationCounter = 0.0f
  }

  def isOver(t: Float): Boolean = {
    !loop && ((reverse && getCurrentFrameIndex - 1 == -1) || (!reverse && getCurrentFrameIndex + 1 == framesCount))
  }

  def isCurrentlyOver: Boolean = {
    isOver(animationCounter)
  }
}
