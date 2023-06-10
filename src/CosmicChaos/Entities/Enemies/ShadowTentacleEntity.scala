package CosmicChaos.Entities.Enemies

import CosmicChaos.Core.{Collideable, GameObject, Renderable, Spatial}
import CosmicChaos.Entities.PlayerEntity
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, Vector2}

class ShadowTentacleEntity(parent: ShadowBossEntity, appearDelay: Float = 0.0f) extends GameObject with Collideable with Renderable with Spatial {

  private val spriteScale = 2.0f
  private val spriteSheet: Texture = new Texture("data/images/boss/shadow/tentacle.png")
  spriteSheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (frameW, frameH) = (spriteSheet.getWidth / 28, spriteSheet.getHeight)
  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(spriteSheet, frameW, frameH)
  private val animation = new Animation(0.09f, frames(0).tail, loop = false)

  collisionLayer = CollisionLayers.none
  collisionMask = CollisionLayers.player

  private val collBoxSize: Vector2 = new Vector2(20 * spriteScale, 50 * spriteScale)
  override val collisionBox: Rectangle = new Rectangle((-frameW * spriteScale + collBoxSize.x) / 2, (-frameH * spriteScale + collBoxSize.y) / 2, collBoxSize.x, collBoxSize.y)

  private var appearTimer: Float = appearDelay
  private var dealDamageTimer: Float = 0.0f

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)
    dealDamageTimer -= dt
    appearTimer -= dt

    if(animation.isCurrentlyOver) {
      parentGameWorld.removeGameObject(this)
    }
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    if(appearTimer >= 0.0f)
      return

    animation.update(Gdx.graphics.getDeltaTime)
    g.draw(animation.getCurrentFrame, position.x, position.y, frameW*spriteScale, frameH*spriteScale)
  }

  override def onCollideWith(other: Collideable): Unit = {
    super.onCollideWith(other)
    other match {
      case p: PlayerEntity =>
        // Only hurt player if we've not hurt him too recently, or if the tentacle has not just appeared
        if(dealDamageTimer <= 0.0f && animation.getCurrentFrameIndex >= 8) {
          parent.dealDamageTo(parent.stats.damage * 0.66f, p)
          dealDamageTimer = 1.33f
        }
      case _ =>
    }
  }
}
