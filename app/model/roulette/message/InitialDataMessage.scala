package model.roulette.message


import model.roulette.data.PlayerGameData


case class InitialDataMessage(MessageType: String = "InitialData",
                              tableId: String = "",
                              destination: String = "player",
                              clientId: String = "",
                              roundId: Long = 0,
                              gameType: String = "AutomatedRoulette",
                              timestamp: String = "",
                              data: PlayerGameData = null)
