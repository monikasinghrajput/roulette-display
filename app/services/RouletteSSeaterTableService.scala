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
package services

import actors.MainActor.TableServiceAdded
import akka.actor.{ActorRef, ActorSystem}
import play.api.Logger
import dao.{LogDao, RouletteDao}
import actors.roulette.AutoRouletteActor
import model.roulette.RouletteJsonCodecs
import model.roulette.data._

class RouletteSSeaterTableService(actorSystem: ActorSystem,
                                  gameService: GameService,
                                  logDao: LogDao,
                                  rouletteDao: RouletteDao) extends RouletteJsonCodecs {

  val log: Logger = Logger(this.getClass)

  var actorAutoRoulette: ActorRef = _

  def init(): Unit = {
      actorAutoRoulette = actorSystem.actorOf(
          AutoRouletteActor.props(
            tableId = "221002",
            tableName = "Auto Roulette",
            limitId = 758359,
            gameService = gameService,
            tableService = this),
        AutoRouletteActor.name)

    gameService.getMainActor ! TableServiceAdded(
      tableId = "221002",
      gameName = "Auto Roulette",
      autoPlay = false,
      pybTimer = 30,
      cardDrawTimer = 10,
      gameResultTimer = 10,
      actorAutoRoulette
    );
  }

  def getAutoRouletteActor: ActorRef = actorAutoRoulette

  def getInitialDataJsonString: String = rouletteDao.getInitialDataJsonString

  def authenticateJsonString: String = rouletteDao.authenticateJsonString

  def sendStreamsJsonString: String = rouletteDao.sendStreamsJsonString

  def sendLogsJsonString(count: Int, filter: String): String = logDao.sendLogsJsonString(count, filter)

  def getTableLimits(tableId: String, limitId: Int): TableLimit = {
    val limits = rouletteDao.getTableLimits(tableId, limitId)
    limits.as[TableLimit]
  }

  def getGameData(tableId: String): InitialData = rouletteDao.getGameData()

  def setGameData(tableId: String, data: InitialData): Unit = rouletteDao.setGameData(data)


}
