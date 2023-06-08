package CosmicChaos.Core.Stats

case class PlayerStats(
  goldMultipler: StatValue
) extends Stats {

  def this(goldMultiplier: Float) = {
    this(StatValue(goldMultiplier))
  }

  override def accept(modifier: StatsModifier): Unit = {
    modifier.modify(this)
  }

  def copy(): PlayerStats = {
    new PlayerStats(goldMultipler.baseValue)
  }
}
