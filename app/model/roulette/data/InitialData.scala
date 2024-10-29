package model.roulette.data

case class InitialData(tableId: String = "",
                       roundId: Long = 1,
                       gameType: String = "",
                       data: RouletteGameData = RouletteGameData(),
                       rouletteType: String = "",
                       physicalTableId: String = "")
