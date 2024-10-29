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
package actors.clients

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import model.common.data.MoneyTransaction
import play.api.libs.json.{JsString, JsValue}

object CageClientActor {
  val name = "seater-admin-actor"
  val path = s"/usr/$name"

  def props(out: ActorRef,
            supervisor: ActorRef,
            remoteAddress: String
           ): Props = Props(new CageClientActor(out = out, supervisor = supervisor, remoteAddress = remoteAddress))
}

class CageClientActor(out: ActorRef, supervisor: ActorRef, remoteAddress: String)
  extends Actor
    with ActorLogging {

  import actors.MainActor._

  private var clientIp = remoteAddress

  override def preStart(): Unit = {
    if (clientIp != "") supervisor ! CageClientConnected(clientIp, self, out)
    super.preStart()
  }

  override def postStop(): Unit = {
    if (clientIp != "") supervisor ! CageClientDisconnected(clientIp);
    super.postStop()
  }


  override def receive: Receive = {
    case pingMsg: JsString =>
    case clientMsg: JsValue =>
      clientMsg("MessageType") match {
        case JsString("INITIALIZE") => {
//          log.info(s"INITIALIZE received from ${clientIp}!")
          supervisor ! CageClientInitDataRequest(clientIp)
        }



        case JsString("DEPOSIT_REQ") => {
          val playerIp = clientMsg("clientIp").validate[String].fold(
            invalid = { fieldErrors =>
              fieldErrors.foreach { x =>
                log.info(s"field: ${x._1}, errors: ${x._2}")
              }
              ""
            },
            valid = { data =>
              data
            }
          )
          val uid = clientMsg("uid").validate[String].fold(
            invalid = { fieldErrors =>
              fieldErrors.foreach { x =>
                log.info(s"field: ${x._1}, errors: ${x._2}")
              }
              "-1"
            },
            valid = { data =>
              data
            }
          )
          val amount: Double = clientMsg("amount").validate[Double].fold(
            invalid = { fieldErrors =>
              fieldErrors.foreach { x =>
                log.info(s"field: ${x._1}, errors: ${x._2}")
              }
              0
            },
            valid = { data =>
              data
            }
          )

          log.info(s"DEPOSIT_REQ ${amount}  for ${uid} received from ${clientIp}!")

          if((uid != "-1") && (amount > 0) ) {
            supervisor ! CageClientMoneyTransaction(
              MoneyTransaction(
                transType = "DEPOSIT",
                admin = clientIp,
                playerIp = playerIp,
                uid = uid,
                amount = amount,
              ),
              out
            )
          }

        }
        case JsString("WITHDRAW_REQ") => {
          val playerIp = clientMsg("clientIp").validate[String].fold(
            invalid = { fieldErrors =>
              fieldErrors.foreach { x =>
                log.info(s"field: ${x._1}, errors: ${x._2}")
              }
              ""
            },
            valid = { data =>
              data
            }
          )
          val uid = clientMsg("uid").validate[String].fold(
            invalid = { fieldErrors =>
              fieldErrors.foreach { x =>
                log.info(s"field: ${x._1}, errors: ${x._2}")
              }
              "-1"
            },
            valid = { data =>
              data
            }
          )
          val amount: Double = clientMsg("amount").validate[Double].fold(
            invalid = { fieldErrors =>
              fieldErrors.foreach { x =>
                log.info(s"field: ${x._1}, errors: ${x._2}")
              }
              0
            },
            valid = { data =>
              data
            }
          )

          log.info(s"WITHDRAW_REQ ${amount}  for ${uid}  received from ${clientIp}!")

          if ((uid != "-1") && (amount > 0)) {
            supervisor ! CageClientMoneyTransaction(
              MoneyTransaction(
                transType = "WITHDRAW",
                admin = clientIp,
                playerIp = playerIp,
                uid = uid,
                amount = amount
              ),
              out
            )
          }

        }

        case JsString("TOGGLE_AUTO_PLAY") =>
          val tableId = clientMsg("tableId").validate[String].fold(
            invalid = { fieldErrors =>
              fieldErrors.foreach { x =>
                log.info(s"field: ${x._1}, errors: ${x._2}")
              }
              ""
            },
            valid = { data =>
              data
            }
          )

          supervisor ! AutoPlayToggleCmd(tableId = tableId)

        case JsString("CLEAR_RECORDS_COMMAND") => {
          log.info(s"CLEAR_RECORDS_COMMAND received from ${clientIp}!")
          supervisor ! ClearRecordsCmd
        }

        case _ => log.info(s"Unknown MessageType ${clientMsg("MessageType")} Received!")
      }

    case _ => log.error("Message unknown to CageClientActor!")
  }
}

