package CosmicChaos.Core.Items

import CosmicChaos.Core.Stats.StatsModifier
import CosmicChaos.Entities.CreatureEntity
import com.badlogic.gdx.graphics.Texture

abstract class Item extends StatsModifier {
  var stackSize: Int = 1
  val name: String
  val description: String
  val icon: Texture
  var holder: CreatureEntity = null

  def update(dt: Float): Unit = {}
}
