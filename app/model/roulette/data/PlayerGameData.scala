package model.roulette.data

case class PlayerGameData(group: Group,
                          playerBetsOfThisRound: Seq[Bet],
                          coldNumbers: Seq[Int],
                          balance: Double,
                          lastWinners: Seq[Winner],
                          hotNumbers: Seq[Int],
                          history: Seq[Win],
                          statistics: Seq[Stat])
