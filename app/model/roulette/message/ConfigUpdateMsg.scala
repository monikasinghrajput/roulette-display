package model.roulette.message

import model.roulette.data.ConfigData

case class ConfigUpdateMsg(MessageType: String = "configUpdate", //Important
                           configData: ConfigData,
                           timestamp: String = "")
