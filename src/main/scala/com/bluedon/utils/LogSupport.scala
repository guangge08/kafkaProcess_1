package main.scala.com.bluedon.utils

import org.apache.log4j.Logger

/**
  * Created by huoguang on 2017/6/2.
  */
trait LogSupport {
  val log = Logger.getLogger(this.getClass)
}
