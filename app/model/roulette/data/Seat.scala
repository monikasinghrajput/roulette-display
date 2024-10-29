package model.roulette.data

case class Seat(uid: String = "-1",
                connected: Boolean = false,
                totalBet: Double = 0,
                winAmount: Double = 0,
                lastWin: Double = 0,
                history: Seq[SeatHistory] = Seq.empty[SeatHistory],
                betList: Seq[Bet] = Seq.empty[Bet],
                winningBets: Seq[WinBet] = Seq.empty[WinBet],
                isTurn : Boolean = false,
                gameStatus: String = "Ready",
                isDealer: Boolean = false,
                isSmallBet: Boolean = false,
                isBigBet: Boolean = false,
                cards: Seq[String] = Seq.empty[String],
                bets: Seq[Double] = Seq.empty[Double],
                actions: Seq[String] = Seq.empty[String])
