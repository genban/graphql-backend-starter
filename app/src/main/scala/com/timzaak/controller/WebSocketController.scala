package com.timzaak.controller

import java.util.UUID

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.stream.scaladsl._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import ws.very.util.akka.websocket.{ClosingMsg, PingMsg, ConnectedMsg, EnableHeartBeat}

import scala.concurrent.{ExecutionContext, Future}

abstract class WebSocketController {
  implicit protected def system: ActorSystem

  implicit protected def materializer: ActorMaterializer

  implicit protected def executionContext: ExecutionContext

  protected def genConnectionActor(sessionId:S): ActorRef

  //-Dwebsocket.frame.maxLength=1024k
  def webSocketFlow: Flow[Message, TextMessage.Strict, NotUsed] = {
    //TODO: identify ConnectedActor
    val uuid = UUID.randomUUID().toString
    val actorRef = genConnectionActor(uuid)
    val in = Flow[Message].collect {
      case TextMessage.Strict(text) =>
        println("ws..s")
        Future.successful(PingMsg(uuid, text))
      case TextMessage.Streamed(textStream) =>
        println("ws..messs")
        textStream.runFold("")(_ + _).map(PingMsg(uuid, _))
    }.mapAsync(1)(identity).to(Sink.actorRef(actorRef, ClosingMsg(uuid)))
    val out = {
      Source.actorRef(32, OverflowStrategy.fail).mapMaterializedValue { outActorRef =>
        actorRef ! ConnectedMsg(uuid, outActorRef)
        actorRef ! EnableHeartBeat(uuid, 3)
      }.map((txt: String) => TextMessage(txt))
    }
    Flow.fromSinkAndSource(in, out)
  }
}

