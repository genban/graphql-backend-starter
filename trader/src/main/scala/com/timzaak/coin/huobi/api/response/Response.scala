package com.timzaak.coin.huobi.api.response

trait Response {
  def status: S

  def isSuccess: B = status == "ok"

}

