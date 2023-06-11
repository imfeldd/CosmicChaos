package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Core.Stats.StatsModifier
import CosmicChaos.Entities.CreatureEntity
import com.badlogic.gdx.graphics.Texture

import scala.util.Random

abstract class Item extends StatsModifier {
  var stackSize: Int = 1
  val name: String
  val description: String
  val icon: Texture
  val rarity: ItemRarity
  var holder: CreatureEntity = null

  def update(dt: Float): Unit = {}
}

object Item {
  private val items: Array[Item] = Array[Item](
    new HealAfterGetHitItem,
    new HealOnCritItem,
    new ItemOnKillItem,
    new MoreAccuracyItem,
    new MoreAmmoItem,
    new MoreAttackSpeedItem,
    new MoreCritsItem,
    new MoreDamageItem,
    new MoreDamageSameEnemyItem,
    new MoreHealthItem,
    new MoreMoneyOnKill,
    new MoreSpeedItem,
    new RollbackHealthItem,
    new MoreHealthRegenItem,
    new FreeItemsItem,
    new MoneyOnGetHitItem,
    new ExplodeOnKillItem,
    new SpeedBoostOnKillItem
  )

  def getRandomItemOfRarity(rarity: ItemRarity): Item = {
    val filteredItems = items.filter(_.rarity == rarity)

    // Make a new clean instance of the Item
    // This is kinda hacky, but I couldn't figure out a more graceful way to implement this
    filteredItems(Random.nextInt(filteredItems.length)).getClass.getDeclaredConstructor().newInstance()
  }
}

object ItemRarity extends Enumeration {
  type ItemRarity = Value
  val common, rare, legendary = Value
}
