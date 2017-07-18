package main.scala.com.bluedon.utils

import java.io.InputStream
import java.sql.{Connection, DriverManager}
import java.util.Properties

import org.apache.hadoop.hbase.client.ConnectionFactory
import org.apache.hadoop.hbase.{HBaseConfiguration, HConstants}

/**
  * Created by huoguang on 2017/6/5.
  */
object connManager {

  val properties:Properties = new Properties()
  val ipstream:InputStream=this.getClass().getResourceAsStream("/manage.properties")
  properties.load(ipstream)

  def getPosgreCon(): Connection = {
    val driver = s"org.postgresql.Driver"
    val url = properties.getProperty("aplication.sql.url")
    val username = properties.getProperty("aplication.sql.username")
    val password = properties.getProperty("aplication.sql.password")
    Class.forName(driver)
    val conn: Connection = DriverManager.getConnection(url, username, password)
    conn
  }

}
