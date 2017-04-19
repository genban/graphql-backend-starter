package com.timzaak.di

import com.timzaak.action._
import com.timzaak.dao.{SmsDao, UserAccountDao}

trait ActionDI extends DaoDI {di =>
  val enableMock: B = true

  object smsAction extends SmsAction {
    override def enableMock: B = di.enableMock

    override protected def smsDao: SmsDao = di.smsDao
  }
  val jwtSecretKey = conf.getString("jwt.secrete")

  object userAccAction extends UserAccAction {
    override protected def userAccDao: UserAccountDao = di.userAccountDao

    override protected def secretKey: S = jwtSecretKey

    override protected def smsAction: SmsAction = di.smsAction
  }



}
