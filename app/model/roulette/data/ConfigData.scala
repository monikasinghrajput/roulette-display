package model.roulette.data

case class ConfigData(tableLimit: TableLimit = TableLimit(),
                      tableId : String = "221002",
                      limitId : Int = 758359,
                      tableName: String = "Auto Roulette",
                      timerTime: Int = 30,
                      showInfoPaper: Boolean = false,
                      autoDraw: Boolean = false,
                      autoPlay: Boolean = true,
                      isOppositeBettingAllowed: Boolean = true)
