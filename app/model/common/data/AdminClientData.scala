package model.common.data

import akka.actor.ActorRef

case class AdminClientData(actor:ActorRef = null ,
                           client:ActorRef = null,
                           name: String)
