package model.roulette.data

import model.common.data._

case class GameData(roundId: Long = 1,
                    gameData: RouletteGameData = RouletteGameData(),
                    configData: ConfigData = ConfigData(),
                    licenseData: LicenseData = LicenseData(),

                    gameStatus: String = "CLOSED",
                     )
