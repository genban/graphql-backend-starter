package com.timzaak.dao

import com.timzaak.entity.UserAccount
import very.util.db.postgre.PostgresPlainProfileWithJson4S.api._
import slick.jdbc.GetResult
import scala.concurrent.Future

trait UserAccountDao extends Dao {

  override protected val fieldList: List[String] =
    extractSnakeFields[UserAccount]

  protected val tableName = "user_accounts"

  implicit val getUserAccountResult = GetResult(r => UserAccount(r.<<, r.<<, r.<<))

  def getByAccAndPwd(account: S, pwd: S): Future[Option[UserAccount]] = {
    sql"select #${fields} from #$tableName where acc=$account and pwd=$pwd"
      .as[UserAccount]
      .headOption
  }

  def newAcc(acc: UserAccount): Future[L] =
    sql"insert into #${tableName}(acc,pwd) values (${acc.accountName},${acc.password}) returning id"
      .as[L]
      .head

  def getAccId(mobile: MobileNum): Future[Option[L]] =
    sql"select id from #${tableName} where acc=${mobile} limit 1"
      .as[L]
      .headOption
}
