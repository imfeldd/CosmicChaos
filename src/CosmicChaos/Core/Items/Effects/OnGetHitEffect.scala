package CosmicChaos.Core.Items.Effects

import CosmicChaos.Entities.CreatureEntity

trait OnGetHitEffect {
  def gotHit(by: CreatureEntity, amount: Float, wasCrit: Boolean): Unit
}
