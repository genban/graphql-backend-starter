package com.timzaak.action

import com.timzaak.dao.SmsDao
import very.util.alisms.WithAliSms
import ws.very.util.lang.Mockable

import scala.concurrent.{Future, Promise}
import scala.util.{Random, Success}

trait SmsAction extends Action with WithAliSms with Mockable {
  protected def smsDao: SmsDao

  protected def captchaCode(): S = {
    val captcha = Random.nextInt(99999).toString
    if (captcha.length < 4) {
      captchaCode()
    } else {
      captcha
    }
  }

  def getCaptcha(mobile: S) = smsDao.getCaptcha(mobile)

  def sendCaptcha(mobile: S) = {
    val code = captchaCode()
    mock2Default {
      sendCaptchaSms("code" -> code)(mobile)
    } {
      Success("TestId")
    }.map { resultId =>
      smsDao.saveCaptcha(mobile, code, resultId)
    }
  }


}
