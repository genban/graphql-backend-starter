package com.timzaak.chaos

import com.timzaak.di.WithTestConf
import org.scalatest.{ FlatSpec, Matchers }
import pdi.jwt.{ Jwt, JwtAlgorithm }

//test-only com.timzaak.chaos.jwtTest
class jwtTest extends FlatSpec with Matchers with WithTestConf {
  "jwt" should "decode 1 with our configure" in {
    val jwtSecretKey = conf.getString("jwt.secret")
    assert(
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.MQ.3GLuAeZtpOfdC1_q_9jqe-EtmZcO2ZZIvPb5gLuMrp0" ==
        Jwt.encode(s"""1""", jwtSecretKey, JwtAlgorithm.HS256)
    )
  }
}
