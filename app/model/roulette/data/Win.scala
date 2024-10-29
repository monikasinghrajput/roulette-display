package model.roulette.data

case class Win(winningNUmber: Int, roundId: Long) {
  override def toString: String = s"${roundId} -> $winningNUmber"
}
