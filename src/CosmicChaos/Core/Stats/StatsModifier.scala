package CosmicChaos.Core.Stats

trait StatsModifier {
  def modify(entityStats: EntityStats) = {}
  def modify(playerStats: PlayerStats) = {}
}
