package CosmicChaos.Core.Items.Effects

import CosmicChaos.Entities.CreatureEntity

trait DealtDamageEffect {
  def dealtDamage(amountDealt: Float, wasCrit: Boolean, target: CreatureEntity): Unit
}
