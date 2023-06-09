package CosmicChaos.Core.Stats

case class EntityStats(
  maxHealth: StatValue,
  maxSpeed: StatValue,
  acceleration: StatValue,
  damage: StatValue,
  criticalChance: StatValue,
  attackSpeed: StatValue,
  attackAccuracy: StatValue,
  attackCapacity: StatValue,
  healthRegenerationAmount: StatValue,
) extends Stats {

  def this(maxHealth: Float, maxSpeed: Float, acceleration: Float, damage: Float, criticalChance: Float, attackSpeed: Float,
           attackAccuracy: Float = 1.0f, attackCapacity: Float = 1.0f, healthRegenAmount: Float = 0.0f) = {
    this(StatValue(maxHealth), StatValue(maxSpeed), StatValue(acceleration), StatValue(damage), StatValue(criticalChance), StatValue(attackSpeed),
      StatValue(attackAccuracy), StatValue(attackCapacity), StatValue(healthRegenAmount))
  }

  override def accept(modifier: StatsModifier): Unit = {
    modifier.modify(this)
  }

  def copy(): EntityStats = {
    new EntityStats(maxHealth.baseValue, maxSpeed.baseValue, acceleration.baseValue, damage.baseValue, criticalChance.baseValue,
      attackSpeed.baseValue, attackAccuracy.baseValue, attackCapacity.baseValue, healthRegenerationAmount.baseValue)
  }
}
