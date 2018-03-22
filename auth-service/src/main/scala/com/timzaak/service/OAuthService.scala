package com.timzaak.service

import com.timzaak.dao.{AuthTokenDao, UserDao}
import com.timzaak.oauth.OAuthDataProvider
import com.timzaak.oauth.entity.{OAuthAuthorization, OAuthClient, OAuthToken, OAuthUser}
import com.timzaak.oauth.error.OAuthException

import scala.concurrent.Future

trait OAuthService extends Service {
  def userDao: UserDao
  def authTokenDao: AuthTokenDao

  protected val provider = new OAuthDataProvider {
    override def getUser(username: String, password: String): Future[O[OAuthUser]] = {
      Future.successful(userDao.where('name -> username, 'password -> password).apply().headOption)
    }

    override def createToken(client_id: String, user_id: Int): Future[OAuthToken] = {
      authTokenDao.createWithAttributes('client_id -> client_id, 'user_id -> user_id)
    }

    override def getOAuthClientByClientId(client_id: _root_.com.timzaak.oauth.S): Future[_root_.com.timzaak.oauth.O[OAuthClient]] = ???

    override def createCode(user_id: _root_.com.timzaak.oauth.I, client: OAuthClient): Future[OAuthAuthorization] = ???

    override def getAuthorizationCode(client_id: _root_.com.timzaak.oauth.S, code: _root_.com.timzaak.oauth.S): Future[_root_.com.timzaak.oauth.O[OAuthAuthorization]] = ???

    override def refreshToken(token: String, refreshToken: String): Future[Either[OAuthException, OAuthToken]] = ???
  }
}
