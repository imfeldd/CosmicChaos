package CosmicChaos.Entities
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle

class Teleporter extends Warp {
  override val name: String = "Teleporter"
  override val collisionBox: Rectangle = new Rectangle(200, 200, 10, 10)
  override protected val basicTexture: Texture = new Texture("data/images/warp/teleporter.png")
  override protected val textureScale: Float = 0.05f
}
