package model.roulette.message

import model.roulette.data.{Group, Stat, Win, Winner}

import java.time.Instant

case class GameResultMessage(MessageType: String = "GAME_RESULT",
                             tableId: String = "",
                             destination: String = "player",
                             clientId: String = "",
                             roundId: Long = 0,
                             gameType: String = "AutomatedRoulette",
                             roundTripStartTime: Long = Instant.now.getEpochSecond,
                             timestamp: String = "",
                             group: Group,
                             coldNumbers: Seq[Int],
                             hotNumbers: Seq[Int],
                             statistics: Seq[Stat],
                             lastWinners: Seq[Winner],
                             gameResults: Seq[Win],
                             winAmount: Double)
