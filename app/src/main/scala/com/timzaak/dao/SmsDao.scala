package com.timzaak.dao

import java.time.LocalDateTime

import very.util.db.postgrel.{ BaseSqlDSL, WithSlick }
import very.util.db.postgrel.PostgresProfileWithJson4S.api._

import scala.concurrent.Future

trait SmsDao extends Dao {

  def saveCaptcha(mobile: S, code: S, reId: S): Future[L] = {
    sql"insert into #${tableName}(mobile,code,re_id) values(${mobile},${code},${reId}) returning id"
      .as[L]
      .head
  }

  def getCaptcha(mobile: S): Future[O[(Captcha, LocalDateTime)]] = {
    sql"select re_id,created_at from #${tableName} where mobile=${mobile} order by created_at desc limit 1"
      .as[(Captcha, LocalDateTime)]
      .headOption
  }
}
