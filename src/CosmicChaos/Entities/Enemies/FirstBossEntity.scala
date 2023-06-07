package CosmicChaos.Entities.Enemies
import CosmicChaos.Core.Weapons.{Projectile, Weapon}
import CosmicChaos.Entities.EntityStats
import com.badlogic.gdx.graphics.Texture
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import CosmicChaos.Core.World.CellularAutomata

import scala.util.Random

class FirstBossEntity extends GunnerEnemyEntity {
  override protected val weapon: Weapon = new Weapon(new Projectile(10,this), true, 1,this, ammoCapacity = 10, reloadTime = 1.5f, inaccuracy = 5.5f ) {}
  override val baseStats: EntityStats = EntityStats(1000,12,10,100,0.0f)
  override var stats: EntityStats = baseStats
  override val name: String = "MAGIC MAGE"

  private val (frameW, frameH) = (83, 64)
  override val collisionBox: Rectangle = new Rectangle(-32,-32,64,64)
  private val firstFormspritesheet: Texture = new Texture("data/images/boss/BossFirstForm.png")
  private val firstFormFrames: Array[Array[TextureRegion]] = TextureRegion.split(firstFormspritesheet, frameW, frameH)
  private val firstFormAnimation = new Animation(0.2f, firstFormFrames(0).tail, loop = true )
  private val spriteScale = 2.3f

  private val secondFormspritesheet: Texture = new Texture("data/images/boss/BossSecondForm.png")
  private val secondFormFrames: Array[Array[TextureRegion]] = TextureRegion.split(secondFormspritesheet, frameW, frameH)
  private val secondFormAnimation = new Animation(0.2f, secondFormFrames(0).tail, loop = true)

  private val thirdFormspritesheet: Texture = new Texture("data/images/boss/BossThirdForm.png")
  private val thirdFormFrames: Array[Array[TextureRegion]] = TextureRegion.split(thirdFormspritesheet, frameW,frameH)
  private val thirdFormAnimation = new Animation(0.2f, thirdFormFrames(0).tail, loop = true)


  cash = 1000
  currentHealth = 100

  override def onGraphicRender(g: GdxGraphics): Unit = {
    val frame = if (currentHealth == 100) firstFormAnimation.getCurrentFrame else if (currentHealth < 100 && currentHealth > 50) secondFormAnimation.getCurrentFrame else thirdFormAnimation.getCurrentFrame
    g.draw(frame, position.x,position.y, 300,300)
  }

  override def onUpdate(dt: Float): Unit = {
    if(parentGameWorld.playerEntity.position.dst(position) < 150) {
      position.x = Random.between(0, parentGameWorld.MyAlgo.width)
      position.y = Random.between(0, parentGameWorld.MyAlgo.height)
    }


  }
}
