package CosmicChaos.Core.Items.Effects

import CosmicChaos.Entities.CreatureEntity

trait OnKillEffect {
  def onKill(killed: CreatureEntity): Unit
}
