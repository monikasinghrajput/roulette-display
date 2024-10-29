/*
* *********************************************************************************************************************************************************************
* Copyright (c) 2017 Tykhe Gaming Private Limited - All Rights Reserved

Licensed under the Software License Agreement (the "License") of Tykhe Gaming Private Limited.
You may not use this file except in compliance with the License.
You may obtain a copy of the License at http://tykhegaming.github.io/LICENSE.txt.

NOTICE
ALL INFORMATION CONTAINED HEREIN IS, AND REMAINS THE PROPERTY OF TYKHE GAMING PRIVATE LIMITED.
THE INTELLECTUAL AND TECHNICAL CONCEPTS CONTAINED HEREIN ARE PROPRIETARY TO TYKHE GAMING PRIVATE LIMITED AND ARE PROTECTED BY TRADE SECRET OR COPYRIGHT LAW.
DISSEMINATION OF THIS INFORMATION OR REPRODUCTION OF THIS MATERIAL IS STRICTLY FORBIDDEN UNLESS PRIOR WRITTEN PERMISSION IS OBTAINED FROM TYKHE GAMING PRIVATE LIMITED.

* **********************************************************************************************************************************************************************
* Change History
* **********************************************************************************************************************************************************************
* |     Date      |     Name     |      Change     |      Details
* |  01/08/2022   | Wilson Sam   |     Created     |  First Milestone
* |  10/10/2023   | Wilson Sam   |     Version     |  Packaged For Demo
* **********************************************************************************************************************************************************************
* */
package dao

import model.roulette.RouletteJsonCodecs
import model.roulette.data.InitialData
import os.Path
import play.api.Logger
import play.api.libs.json.{JsValue, Json}


class RouletteDao extends RouletteJsonCodecs  {

  val log: Logger = Logger(this.getClass)

  def authenticationParamsJsonString: String = os.read(os.pwd / "conf" / "jsons" / "config" / "roulette" / "authenticationParams.json")

  var authenticationParams: JsValue = Json.parse(authenticationParamsJsonString)

  def definitionsJsonString: String = os.read(os.pwd / "conf" / "jsons" / "config" / "roulette" / "definitions.json")

  val definitions: JsValue = Json.parse(definitionsJsonString)

  def initialConfigString: String = os.read(os.pwd / "conf" / "jsons" / "config" / "roulette" / "config.json")

  val initialConfig: JsValue = Json.parse(initialConfigString)

  def gameJsonString: String = os.read(os.pwd / "conf" / "jsons" / "config" / "roulette" / "game.json")

  val game: JsValue = Json.parse(gameJsonString)

  def gameTypesString: String = os.read(os.pwd / "conf" / "jsons" / "config" / "roulette" / "gameTypes.json")

  val gameTypes: JsValue = Json.parse(gameTypesString)

  def groupsString: String = os.read(os.pwd / "conf" / "jsons" / "config" / "roulette" / "groups.json")

  val groups: JsValue = Json.parse(groupsString)

  def localeString: String = os.read(os.pwd / "conf" / "jsons" / "config" / "roulette" / "locale.json")

  val locale: JsValue = Json.parse(localeString)

  def tablesString: String = os.read(os.pwd / "conf" / "jsons" / "config" / "roulette" / "tables.json")

  val tables: JsValue = Json.parse(tablesString)

  def userDataString: String = os.read(os.pwd / "conf" / "jsons" / "config" / "roulette" / "user_data.json")

  val userData: JsValue = Json.parse(userDataString)

  def videoStreamsString: String = os.read(os.pwd / "conf" / "jsons" / "config" / "roulette" / "video_streams.json")

  val videoStreams: JsValue = Json.parse(videoStreamsString)


  val fileName: Path = os.home / "roulette" /  "game_data.json"

  if (!os.exists(fileName)) {
    os.write.over(
      fileName,
      Json.prettyPrint(Json.toJson(InitialData())),
      createFolders = true
    )
  }
  val dataJson: JsValue = Json.parse(os.read(fileName))
  var gameData: InitialData = dataJson.validate[InitialData].fold(
    invalid = { fieldErrors =>
      log.logger.info(s"Roulette Backup Game Data Read failed..")
      fieldErrors.foreach { x =>
        log.logger.info(s"field: ${x._1}, errors: ${x._2}")
      }
      InitialData()
    },
    valid = { data =>
      //log.logger.info(s"Backup Game Data Read Success..")
      data
    }
  )

  def setGameData(data: InitialData): Unit = {
    os.write.over(
      fileName,
      Json.prettyPrint(Json.toJson(data)),
      createFolders = true
    )
  }

  def getGameData(): InitialData = gameData



  def getInitialDataJsonString: String = {
    Json.prettyPrint(
      Json.obj(
        "errorCode" -> 0,
        "skin" -> "legacy",
        "language" -> "en",
        "boAuthenticated" -> false,
        "initialAuthenticationTarget" -> "lobby",
        "authErros" -> Json.obj(),
        "authenticationParams" -> authenticationParams,
        "definitions" -> definitions,
        "game" -> game,
        "gameTypes" -> gameTypes,
        "initialConfig" -> initialConfig,
        "locale" -> locale,
      )
    );
  }

  def authenticateJsonString: String = {
    Json.prettyPrint(
      Json.obj(
        "error_code" -> 0,
        "error_message" -> "",
        "result" -> Json.obj(
          "authentication_params" -> authenticationParams,
          "config" -> initialConfig,
          "definitions" -> definitions,
          "favorite" -> Json.arr(),
          "game_types" -> gameTypes,
          "groups" -> groups,
          "language" -> "en",
          "locale" -> locale,
          "recent" -> Json.arr(),
          "tables" -> tables,
          "user_data" -> userData
        )
      )
    );
  }

  def sendStreamsJsonString: String = {
    Json.prettyPrint(
      videoStreams
    );
  }



  def getTableLimits(tableId: String, limitId: Int): JsValue = {
    val table = tables(tableId)
    val tableLimit: JsValue = table("limits")(limitId.toString)
    tableLimit
  }



}
