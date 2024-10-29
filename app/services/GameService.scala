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
* |  22/09/2023   | Wilson Sam   |     Created     |  First Milestone
* **********************************************************************************************************************************************************************
* */
package services


import actors.LogManagerActor.Info
import play.api.Logger
import akka.actor.{ActorRef, ActorSystem}
import actors._
import dao.{LobbyDao, LogDao, PlayerDao}
import model.common.data.Player
import model.common.messages.{GameTransactionMsg, MoneyTransactionMsg, OperationTransactionMsg, RoundTransactionMsg}

class GameService(actorSystem: ActorSystem,
                  playerDao: PlayerDao,
                  lobbyDao: LobbyDao,
                  logDao: LogDao)  {

  val log: Logger = Logger(this.getClass)

  var actorMain: ActorRef = _
  val actorLogging: ActorRef = actorSystem.actorOf(LogManagerActor.props(logDao))

  def init(): Unit = {
    actorLogging ! Info(file = "GameService", str =  s"Actor System is : ${actorSystem.name}");
    actorLogging ! Info(file = "GameService", str =  s"Actor System Uptime : ${actorSystem.uptime}");
    actorMain = actorSystem.actorOf(MainActor.props(gameService = this), MainActor.name)

  }

  def getMainActor: ActorRef = actorMain
  def getLoggingActor: ActorRef = actorLogging


  /*
  * PLAYER DAO CRUD INTERFACE
  *
  * */
  def updatePlayerData(player: Player, uid: String): Unit = playerDao.updatePlayer(player, uid)
  def getPlayers: Seq[Player]                             = playerDao.getPlayersData
  def getPlayerData(clientIp: String): Player = {
    val playerData = playerDao.getPlayerData(clientIp)
    if(playerData.nickname == "Guest") {
      val updatedPlayerData = playerData.copy(nickname = s"Guest${clientIp}")
      playerDao.addPlayer(updatedPlayerData)
      updatedPlayerData
    } else {
      playerData
    }

  }

  def addOperationTransaction(transaction: OperationTransactionMsg): Unit = playerDao.addOperationTransaction(transaction)
  def getOperationTransactions: Seq[OperationTransactionMsg]               = playerDao.getOperationTransactionsData()

  /*
  * LOBBY DAO CRUD INTERFACE
  *
  * */

  //CREATE
  def addMoneyTransaction(transaction: MoneyTransactionMsg): Unit = lobbyDao.addMoneyTransaction(transaction)
  def addGameTransaction(transaction: GameTransactionMsg): Unit = lobbyDao.addGameTransaction(transaction)
  def addRoundTransaction(transaction: RoundTransactionMsg): Unit = lobbyDao.addRoundTransaction(transaction)

  //READ
  def getMoneyTransactions: Seq[MoneyTransactionMsg] = lobbyDao.getMoneyTransactionsData()
  def getGameTransactions: Seq[GameTransactionMsg]   = lobbyDao.getGameTransactionsData()
  def getRoundTransactions: Seq[RoundTransactionMsg] = lobbyDao.getRoundTransactionsData()

  //DELETE
  def clearGameTransactions(): Unit                  = lobbyDao.clearGameTransactionsData()
  def clearRoundTransactions(): Unit                 = lobbyDao.clearRoundTransactionsData()
  def clearMoneyTransactions(): Unit                 = lobbyDao.clearMoneyTransactionsData()


}
