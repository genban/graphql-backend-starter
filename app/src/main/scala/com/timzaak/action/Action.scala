package com.timzaak.action

import scala.concurrent.ExecutionContext

abstract class Action(implicit val ec: ExecutionContext) {}
