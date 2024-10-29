package model.roulette.message


import model.roulette.data.{Client, Roulette8SeaterData}


case class InitialAdminDataMessage(MessageType: String = "InitialData",
                                   tableId: String = "",
                                   clientId: String = "",
                                   roundId: Long = 0,
                                   gameType: String = "AutomatedRoulette",
                                   timestamp: String = "",
                                   data: Roulette8SeaterData = null,
                                   players: Seq[Client] = Seq.empty[Client])
