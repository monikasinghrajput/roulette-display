package model.roulette.message

import java.time.Instant

case class PlaceYourBetsMessage(MessageType: String = "PLACE_YOUR_BETS",
                                tableId: String = "",
                                destination: String = "player",
                                clientId: String = "",
                                roundId: Long = 0,
                                gameType: String = "AutomatedRoulette",
                                roundTripStartTime: Long = Instant.now.getEpochSecond,
                                timestamp: String = "",
                                timerTimeLeft: Int = 0,
                                timerTime: Int = 30)
