package model.common.data

import akka.actor.ActorRef

case class TableState(actor: ActorRef, data: Table)
