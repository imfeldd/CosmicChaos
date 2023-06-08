package CosmicChaos.Core.Items.Effects

import CosmicChaos.Entities.CreatureEntity

trait BeforeDealDamageEffect {
  def beforeDealDamage(amountToDeal: Float, isCrit: Boolean, target: CreatureEntity): Float
}
