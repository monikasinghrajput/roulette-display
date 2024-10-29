package model.roulette.data

case class RouletteGameData(group: Group = Group(),
                            coldNumbers: Seq[Int] = Seq.empty[Int],
                            lastWinners: Seq[Winner] = Seq.empty[Winner],
                            hotNumbers: Seq[Int] = Seq.empty[Int],
                            history: Seq[Win] = Seq.empty[Win],
                            statistics: Seq[Stat] = Seq.empty[Stat])
