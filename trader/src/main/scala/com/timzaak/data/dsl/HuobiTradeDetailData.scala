package com.timzaak.data.dsl

import com.joyrec.util.log.impl.slf4j.ClassSlf4j
import com.timzaak.api.huobi.HuobiWSClient
import com.timzaak.api.huobi.api.ws.response.ChResponse
import com.timzaak.api.huobi.api.ws.{HuobiWebSocketListener, RequestTopic}
import com.timzaak.api.huobi.entity.Trade
import monix.execution.Cancelable
import monix.execution.cancelables.RefCountCancelable
import monix.reactive.observers.Subscriber
import monix.reactive.Observable
import org.json4s.JValue

import scala.concurrent.ExecutionContext

//TODO: 重试策略精细化
class HuobiTradeDetailData(symbol: S)(implicit ec:ExecutionContext) extends Observable[Trade] with ClassSlf4j{
  protected var wsClient: Option[HuobiWSClient] = None
  protected var subscribers:Set[Subscriber[Trade]] = Set.empty
  val cancelable = RefCountCancelable{ () =>
    println("huobi Cancel")
    wsClient.foreach(_.close())
    wsClient = None
    subscribers = Set.empty
  }





  private val _listener = new HuobiWebSocketListener {
    override def onOpen(client: HuobiWSClient): U = {
      client.subscribe(RequestTopic.tradeDetail(symbol)).failed.map{ t=>
        error(t.getMessage)
      }
    }

    override def onMessage(client: HuobiWSClient, message: JValue): U = {
      val tick = ChResponse.extract(message) match {
        case ChResponse(_, _, tick: Trade) => tick
      }
      subscribers.foreach(_.onNext(tick))
    }
    override def onFailure(client:HuobiWSClient, t:Throwable):Unit = {
      if(!cancelable.isCanceled){
        //client.close()
        wsClient = Option(createWebSocketClient)
      }

    }

    override def onClosed(client: HuobiWSClient, code: I, reason: S): U = {
      if(!cancelable.isCanceled)
        wsClient = Option(createWebSocketClient)
    }
  }

  private def createWebSocketClient:HuobiWSClient = new HuobiWSClient(_listener)

  override def unsafeSubscribeFn(subscriber: Subscriber[Trade]): Cancelable = {
    wsClient = wsClient.orElse(Some(createWebSocketClient))
    subscribers += subscriber
    val ref = cancelable.acquire()
    Cancelable{() =>
      subscribers -= subscriber
      ref.cancel()
    }
  }

}
