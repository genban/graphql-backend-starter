package com.timzaak.action

import com.timzaak.dao.UserAccountDao
import com.timzaak.entity.UserAccount
import ws.very.util.security.SHA

import scala.concurrent.Future

trait UserAccAction extends Action {

  protected def userAccDao: UserAccountDao

  protected def smsAction: SmsAction

  protected def secretKey: S

  protected[action] def secretPwd(acc: S, pwd: S) = {
    SHA(acc + pwd, SHA.SHA_256)
  }


  def loginWithPwd(acc: S, pwd: S) = {
    userAccDao.getByAccAndPwd(acc, secretPwd(acc, pwd)).map(_.flatMap(_.id)).map {
      case Some(userId) =>

      case None =>
    }
  }


  def register(acc: S, pwd: S, capture: S):Future[S] = {

    smsAction.getCaptcha(acc) match {
      case Some(`capture`) => userAccDao.newAcc(UserAccount(None, acc, secretPwd(acc, pwd)))
          .recoverWith {
            case _ => Future.failed(new Exception("账号已存在"))
          }
      case _ => Future.failed(new Exception("验证码错误"))
    }
  }

}
