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
package actors

import actors.MainActor.GuestConnectAccepted
import akka.actor.{Actor, ActorRef, Props}
import model.common.data
import model.common.data.{MoneyTransaction, Table}
import model.common.messages.{GameTransactionMsg, MoneyTransactionMsg, OperationTransactionMsg, PlayerUpdatedMsg, RoundTransactionMsg}
import play.api.Logger
import services.GameService

import java.text.SimpleDateFormat
import java.util.Calendar
import scala.language.postfixOps
import model.lobby.data.{Client, State}


object MainActor {
  val name = "main-actor"
  val path = s"/usr/$name"

  def props(gameService: GameService): Props = Props(new MainActor(gameService))

  case class CageClientConnected(ip: String, actor: ActorRef, out: ActorRef)

  case class CageClientInitDataRequest(ip: String)

  case class CageClientDisconnected(ip: String)

  case class CageClientMoneyTransaction(moneyTransaction: MoneyTransaction, out: ActorRef)

  case class TableServiceAdded(tableId: String,
                               gameName: String,
                               autoPlay: Boolean,
                               pybTimer: Int,
                               cardDrawTimer: Int,
                               gameResultTimer: Int,
                               tableActorRef: ActorRef)

  case class GuestConnectRequest(playerIp: String, actor: ActorRef, client: ActorRef, tableActorRef: ActorRef)

  case class GuestConnectAccepted(ip: String, actor: ActorRef, client: ActorRef)

  case class PlayerBalanceUpdated(uid: String, newBalance: Double)

  case class PlayerStatusOnline(playerIp: String)

  case class PlayerStatusOffline(playerIp: String)

  case class TablePlayerBetPlaced(tableId: String, gameName: String, roundId: Long, details: String, playerIp: String, amount: Double, tableActorRef: ActorRef)

  case class TablePlayerBetWon(tableId: String, gameName: String, roundId: Long, details: String, betDetails: String, resultDetails: String, playerIp: String, amount: Double, tableActorRef: ActorRef)

  case class TablePlayerBetLost(tableId: String, gameName: String, roundId: Long, details: String, betDetails: String, resultDetails: String, playerIp: String, amount: Double, tableActorRef: ActorRef)

  case class TableRoundResultIndication(tableId: String, gameName: String, roundId: Long, winningHand: String, gameResult: String,
                                        playersTotalBet: List[(String, Double)], playersTotalWin: List[(String, Double)],
                                        playerBetsList: String, playerWonBetsList: String)


  case class AutoPlayToggleCmd(tableId: String = "")

  case object ClearRecordsCmd

}

final class MainActor(gameService: GameService)
  extends CageHandling(gameService = gameService)
  with Actor
{

   override def preStart(): Unit = {
     super.preStart()
   }

   override def postStop(): Unit = {
     super.postStop()
   }


  import MainActor._

  override def receive: Receive = STATE_0(state = State())

  def STATE_0(state: State): Receive = {
    case CageClientConnected(ip, actor, out) => context become STATE_0(state = handleCageClientConnected(state, ip, actor, out))
    case CageClientDisconnected(ip)          => context become STATE_0(state = handleCageClientDisconnected(state, ip))
    case CageClientInitDataRequest(ip)       => context become STATE_0(state = handleCageClientDataRequest(state, ip))
    case ClearRecordsCmd                     => context become STATE_0(state = handleClearRecordsCmd(state))
    case AutoPlayToggleCmd(tableId)          =>
      log.info(s"Auto Play Toggle received for table ${tableId}")
      if (state.tables.exists(p => p._1 == tableId)) {
        val table = state.tables.find(p => p._1 == tableId).get
        val currentAutoPlay = table._2.data.autoPlay
        val updatedData = table._2.data.copy(autoPlay = !currentAutoPlay)
        val updatedTable = table.copy(_2 = table._2.copy(data = updatedData))

        table._2.actor ! AutoPlayToggleCmd

        val updatedTables = state.tables.map((table) => {
          if (tableId == table._1) {
            updatedTable
          } else {
            table
          }
        })

        state.sendTableUpdatedMsg(updatedTable._2.data)
        context become STATE_0(state = state.copy(tables = updatedTables))
      }

    case TableServiceAdded(tableId, gameName, autoPlay, pybTimer, cardDrawTimer, gameResultTimer, tableActorRef) =>
      context become STATE_0(state = handleTableServiceAdded(state, tableId, gameName, autoPlay, pybTimer, cardDrawTimer, gameResultTimer, tableActorRef))

    case CageClientMoneyTransaction(moneyTransaction, out) => handleCageClientMoneyTransaction(state, moneyTransaction, out)
    case GuestConnectRequest(playerIp, actor, client, tableActorRef) => handleGuestConnectRequest(state, playerIp, actor, client, tableActorRef)
    case PlayerStatusOnline(playerIp) => handlePlayerStatusOnline(state, playerIp)
    case PlayerStatusOffline(playerIp) => handlePlayerStatusOffline(state, playerIp)
    case TablePlayerBetPlaced(tableId, gameName, roundId, details, playerIp, amount, tableActorRef) =>
      handleTablePlayerBetPlaced(state = state, gameName = gameName, tableId = tableId, roundId = roundId, details = details, playerIp = playerIp, amount = amount, tableActorRef = tableActorRef)

    case TablePlayerBetWon(tableId, gameName, roundId, details, betDetails, resultDetails, playerIp, amount, tableActorRef) =>
      handleTablePlayerBetWon(state = state, gameName = gameName, tableId = tableId, roundId = roundId, details = details, betDetails = betDetails, resultDetails = resultDetails, playerIp = playerIp, amount = amount, tableActorRef = tableActorRef)

    case TablePlayerBetLost(tableId, gameName, roundId, details, betDetails, resultDetails, playerIp, amount, tableActorRef) =>
      handleTablePlayerBetLost(state = state, gameName = gameName, tableId = tableId, roundId = roundId, details = details, betDetails = betDetails, resultDetails = resultDetails, playerIp = playerIp, amount = amount, tableActorRef = tableActorRef)


    case TableRoundResultIndication(tableId, gameName, roundId, winningHand, gameResult, playersTotalBet, playersTotalWin, playerBetsList, playerWonBetsList) =>
      handleTableRoundResultIndication(state, tableId, gameName, roundId, winningHand, gameResult, playersTotalBet, playersTotalWin, playerBetsList, playerWonBetsList)



  }


}

abstract class CageHandling(gameService: GameService) {
  val log: Logger = Logger(this.getClass)
  val dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss.SSS z")


  def handleTablePlayerBetPlaced(state: State,
                                 tableId: String,
                                 gameName: String,
                                 roundId: Long,
                                 details: String,
                                 playerIp: String,
                                 amount: Double,
                                 tableActorRef: ActorRef
                                ): Unit = {

    val accountPlayer = gameService.getPlayerData(playerIp);
    val oldBalance = accountPlayer.balance
    val newBalance = oldBalance - amount

    gameService.updatePlayerData(accountPlayer.copy(balance = newBalance), accountPlayer.uid)
    state.sendPlayerUpdatedMsgToClients(accountPlayer.copy(balance = newBalance), MessageType = "PLAYER_UPDATED")

    state.sendPlayerBalanceToAllTables(accountPlayer.uid, newBalance)

    val gameTransactionMsg = GameTransactionMsg(
      MessageType = "PLAYER_BET_PLACED",
      transType = "Bet",
      tableId = tableId,
      gameName = gameName,
      roundId = roundId,
      betDetails = details,
      uid = accountPlayer.uid,
      amount = amount,
      oldBalance = oldBalance,
      newBalance = newBalance,
      timestamp = dateFormat.format(Calendar.getInstance().getTime))

    gameService.addGameTransaction(gameTransactionMsg)
    state.sendGameTransactionMsgToClients(gameTransactionMsg)

  }

  def handleTablePlayerBetWon(state: State,
                              tableId: String,
                              gameName: String,
                              roundId: Long,
                              details: String,
                              betDetails: String,
                              resultDetails: String,
                              playerIp: String,
                              amount: Double,
                              tableActorRef: ActorRef
                             ): Unit = {

    val accountPlayer = gameService.getPlayerData(playerIp);
    val oldBalance = accountPlayer.balance
    val newBalance = oldBalance + amount

    gameService.updatePlayerData(accountPlayer.copy(balance = newBalance), accountPlayer.uid)
    state.sendPlayerUpdatedMsgToClients(accountPlayer.copy(balance = newBalance), MessageType = "PLAYER_UPDATED")

    state.sendPlayerBalanceToAllTables(accountPlayer.uid, newBalance)

    val gameTransactionMsg = GameTransactionMsg(
      MessageType = "PLAYER_BET_WON",
      transType = "Win",
      tableId = tableId,
      gameName = gameName,
      roundId = roundId,
      betDetails = betDetails,
      winDetails = details,
      resultDetails = resultDetails,
      uid = accountPlayer.uid,
      amount = amount,
      oldBalance = oldBalance,
      newBalance = newBalance,
      timestamp = dateFormat.format(Calendar.getInstance().getTime))

    gameService.addGameTransaction(gameTransactionMsg)
    state.sendGameTransactionMsgToClients(gameTransactionMsg)

  }

  def handleTableRoundResultIndication(state: State,
                                       tableId: String,
                                       gameName: String,
                                       roundId: Long,
                                       winningHand: String,
                                       gameResult: String,
                                       playersTotalBet: List[(String, Double)],
                                       playersTotalWin: List[(String, Double)],
                                       playerBetsList: String,
                                       playerWonBetsList: String) : Unit = {

    //Create a round transaction record
    val roundTransactionRecord = RoundTransactionMsg(
      MessageType = "ROUND_RESULT",
      transType = "Win",
      tableId = tableId,
      gameName = gameName,
      roundId = roundId,
      winningHand = winningHand,
      gameResult = gameResult,
      playersTotalBet = playersTotalBet,
      playersTotalWin = playersTotalWin,
      playerBetsList = playerBetsList,
      playerWonBetsList = playerWonBetsList,
      timestamp = dateFormat.format(Calendar.getInstance().getTime)
    )

    //Add it to local records db
    gameService.addRoundTransaction(roundTransactionRecord)
    //Send it to clients
    state.sendRoundTransactionMsgToClients(roundTransactionRecord)
  }



  def handleTablePlayerBetLost(state: State,
                               tableId: String,
                               gameName: String,
                               roundId: Long,
                               details: String,
                               betDetails: String,
                               resultDetails: String,
                               playerIp: String,
                               amount: Double,
                               tableActorRef: ActorRef
                              ): Unit = {

    val accountPlayer = gameService.getPlayerData(playerIp);
    val oldBalance = accountPlayer.balance + amount
    val newBalance = accountPlayer.balance

    val gameTransactionMsg = GameTransactionMsg(
      MessageType = "PLAYER_BET_LOST",
      transType = "NoWin",
      tableId = tableId,
      gameName = gameName,
      roundId = roundId,
      betDetails = betDetails,
      winDetails = details,
      resultDetails = resultDetails,
      uid = accountPlayer.uid,
      amount = amount,
      oldBalance = oldBalance,
      newBalance = accountPlayer.balance,
      timestamp = dateFormat.format(Calendar.getInstance().getTime))

    gameService.addGameTransaction(gameTransactionMsg)
    state.sendGameTransactionMsgToClients(gameTransactionMsg)

  }

  def handlePlayerStatusOffline(state: State, playerIp: String): Unit = {

    val accountPlayer = gameService.getPlayerData(playerIp);
    gameService.updatePlayerData(accountPlayer.copy(status = "offline"), accountPlayer.uid)
    state.sendPlayerUpdatedMsgToClients(accountPlayer.copy(status = "offline"), "PLAYER_OFFLINE")

    val operationTransactionMsg = OperationTransactionMsg(
      MessageType = "PLAYER_OFFLINE",
      transType = "Operation",
      uid = accountPlayer.uid,
      nickname = accountPlayer.nickname,
      client_ip = accountPlayer.clientIp,
      status = "offline",
      usage = accountPlayer.usage,
      timestamp = dateFormat.format(Calendar.getInstance().getTime))

    gameService.addOperationTransaction(operationTransactionMsg)

  }


  def handlePlayerStatusOnline(state: State, playerIp: String): Unit = {
    val accountPlayer = gameService.getPlayerData(playerIp);
    gameService.updatePlayerData(accountPlayer.copy(status = "online"), accountPlayer.uid)
    state.sendPlayerUpdatedMsgToClients(accountPlayer.copy(status = "online"), "PLAYER_ONLINE")

    val operationTransactionMsg = OperationTransactionMsg(
      MessageType = "PLAYER_ONLINE",
      transType = "Operation",
      uid = accountPlayer.uid,
      nickname = accountPlayer.nickname,
      client_ip = accountPlayer.clientIp,
      status = "online",
      usage = accountPlayer.usage,
      timestamp = dateFormat.format(Calendar.getInstance().getTime))

    gameService.addOperationTransaction(operationTransactionMsg)

  }


  def handleGuestConnectRequest(state: State, playerIp: String, actor: ActorRef, client: ActorRef, tableActorRef: ActorRef): Unit = {

    val guestPlayer = gameService.getPlayerData(playerIp);
    state.sendPlayerCreatedMsgToClients(guestPlayer)

    val operationTransactionMsg = OperationTransactionMsg(
      MessageType = "PLAYER_CREATED",
      transType = "Operation",
      uid = guestPlayer.uid,
      nickname = guestPlayer.nickname,
      client_ip = guestPlayer.clientIp,
      status = "offline",
      usage = guestPlayer.usage,
      timestamp = dateFormat.format(Calendar.getInstance().getTime))

    gameService.addOperationTransaction(operationTransactionMsg)

    tableActorRef ! GuestConnectAccepted(ip = playerIp, actor = actor, client = client)
  }


  def handleTableServiceAdded(state: State,
                              tableId: String,
                              gameName: String,
                              autoPlay: Boolean,
                              pybTimer: Int,
                              cardDrawTimer: Int,
                              gameResultTimer: Int,
                              tableActorRef: ActorRef): State = {

    var tables = state.tables
    if (tables.contains(tableId)) {
      tables(tableId) = data.TableState(actor = tableActorRef, data = Table(tableId, gameName, autoPlay, pybTimer, cardDrawTimer, gameResultTimer))
    } else {
      tables = tables ++ Map(tableId -> data.TableState(actor = tableActorRef, data = Table(tableId, gameName, autoPlay, pybTimer, cardDrawTimer, gameResultTimer)))
    }
    state.copy(tables = tables)
  }

  def handleCageClientMoneyTransaction(state: State, moneyTransaction: MoneyTransaction, out: ActorRef): Unit = {
    val uid = moneyTransaction.uid
    val transBy = moneyTransaction.playerIp

    val playerOpt = gameService.getPlayers.find(p => p.uid == uid)
    playerOpt match {
      case Some(player) =>
        val currentBalance = player.balance
        val amount = moneyTransaction.amount


        moneyTransaction.transType match {
          case "DEPOSIT" =>
            val newBalance = currentBalance + amount

            //Part 1:
            val moneyTransactionMsgReq = MoneyTransactionMsg(
              MessageType = "DEPOSIT_REQ",
              transBy = transBy,
              uid = moneyTransaction.uid,
              amount = moneyTransaction.amount,
              oldBalance = currentBalance,
              newBalance = newBalance,
              timestamp = dateFormat.format(Calendar.getInstance().getTime))

            state.sendMoneyTransactionMsg(moneyTransactionMsgReq, out)
            gameService.addMoneyTransaction(moneyTransactionMsgReq)

            //Part 2:
            gameService.updatePlayerData(player.copy(balance = newBalance), uid)
            state.sendPlayerBalanceToAllTables(uid, newBalance)
            val playerUpdatedMsg = PlayerUpdatedMsg(MessageType = "PLAYER_UPDATED",
              player = player.copy(balance = newBalance),
              timestamp = dateFormat.format(Calendar.getInstance().getTime))
            state.sendPlayerUpdatedMsgToClients(playerUpdatedMsg, out)

            //Part 3:
            val moneyTransactionMsgResp = MoneyTransactionMsg(
              MessageType = "DEPOSIT_SUCCESS",
              transBy = transBy,
              uid = moneyTransaction.uid,
              amount = moneyTransaction.amount,
              oldBalance = currentBalance,
              newBalance = newBalance,
              timestamp = dateFormat.format(Calendar.getInstance().getTime))
            state.sendMoneyTransactionMsg(moneyTransactionMsgResp, out)
            gameService.addMoneyTransaction(moneyTransactionMsgResp)

          case "WITHDRAW" =>
            val newBalance = currentBalance - amount

            //Part 1:
            val moneyTransactionMsgReq = MoneyTransactionMsg(
              MessageType = "WITHDRAW_REQ",
              transBy = transBy,
              uid = moneyTransaction.uid,
              amount = moneyTransaction.amount,
              oldBalance = currentBalance,
              newBalance = newBalance,
              timestamp = dateFormat.format(Calendar.getInstance().getTime))
            state.sendMoneyTransactionMsg(moneyTransactionMsgReq, out)
            gameService.addMoneyTransaction(moneyTransactionMsgReq)

            //Part 2:
            gameService.updatePlayerData(player.copy(balance = newBalance), uid)
            state.sendPlayerBalanceToAllTables(uid, newBalance)
            val playerUpdatedMsg = PlayerUpdatedMsg(MessageType = "PLAYER_UPDATED",
              player = player.copy(balance = newBalance),
              timestamp = dateFormat.format(Calendar.getInstance().getTime))
            state.sendPlayerUpdatedMsgToClients(playerUpdatedMsg, out)

            //Part 3:
            val moneyTransactionMsgResp = MoneyTransactionMsg(
              MessageType = "WITHDRAW_SUCCESS",
              transBy = transBy,
              uid = moneyTransaction.uid,
              amount = moneyTransaction.amount,
              oldBalance = currentBalance,
              newBalance = newBalance,
              timestamp = dateFormat.format(Calendar.getInstance().getTime))
            state.sendMoneyTransactionMsg(moneyTransactionMsgResp, out)
            gameService.addMoneyTransaction(moneyTransactionMsgResp)
        }

      case None =>
    }
  }

  def handleCageClientDataRequest(state: State, ip: String): State = {
    if (state.clients.contains(ip)) {
      val tables = state.tables.map((entry) => entry._2.data).toSeq
      val players = gameService.getPlayers
      val moneyTransactions = gameService.getMoneyTransactions
      val gameTransactions = gameService.getGameTransactions
      val roundTransactions = gameService.getRoundTransactions
      val operations = gameService.getOperationTransactions
      val out = state.clients(ip).out

      state.sendInitialDataMsg(
        tables = tables,
        players = players,
        moneyTransactions = moneyTransactions,
        gameTransactions = gameTransactions,
        roundTransactions = roundTransactions,
        operations = operations,
        out = out)

    }

    state
  }
  def handleClearRecordsCmd(state: State): State = {
    val tables = state.tables.map((entry) => entry._2.data).toSeq
    val players = gameService.getPlayers
    val moneyTransactions = gameService.getMoneyTransactions
    val operations = gameService.getOperationTransactions

    gameService.clearGameTransactions()
    gameService.clearRoundTransactions()

    state.sendInitialDataMsgToAll(
      tables = tables,
      players = players,
      moneyTransactions = moneyTransactions,
      gameTransactions = Seq.empty[GameTransactionMsg],
      roundTransactions = Seq.empty[RoundTransactionMsg],
      operations = operations
    )

    state
  }

  def handleCageClientConnected(state: State, ip: String, actor: ActorRef, out: ActorRef): State = {
    var clients = state.clients
    if (clients.contains(ip)) {
      clients(ip) = clients(ip).copy(actor = actor, out = out)
      //      log.info("Client reconnected")
    } else {
      clients = clients ++ Map(ip -> Client(actor = actor, out = out))
      //      log.info("Client Fresh Connection")

    }
    state.copy(clients = clients)
  }

  def handleCageClientDisconnected(state: State, ip: String): State = {
    var clients = state.clients
    if (clients.contains(ip)) clients -= ip
    state.copy(clients = clients)
  }

}
class RoundReadDao {
  var date: Int = new SimpleDateFormat("ydM").format(Calendar.getInstance.getTime).drop(2).toInt
  var round = 0

  def getNextRound: Int = {
    val today = new SimpleDateFormat("ydM").format(Calendar.getInstance.getTime).drop(2).toInt
    if (date != today) {
      date = today
      round = 0
    }
    round += 1
    date + round
  }
}


