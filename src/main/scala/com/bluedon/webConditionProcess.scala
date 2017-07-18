package main.scala.com.bluedon

import java.io.InputStream
import java.sql.PreparedStatement
import java.util
import java.util.Properties

import main.scala.com.bluedon.utils.{RowKeyUtils, connManager}
import org.apache.hadoop.hbase.{HBaseConfiguration, HConstants, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put, Row}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.json4s.ShortTypeHints
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization

/**
  * Created by huoguang on 2017/6/9.
  */
object webConditionProcess {

  def main(args:Array[String]) = {
    val log: Logger = Logger.getLogger("org.apache.spark")
    Logger.getLogger("org").setLevel(Level.ERROR)
    val properties:Properties = new Properties()
    val ipstream:InputStream=this.getClass().getResourceAsStream("/manage.properties")
    properties.load(ipstream)

    val spark = SparkSession
      .builder()
      .master("local[2]")
      .appName("localTest")
      .config("spark.sql.warehouse.dir", "F:/code/AnalyzeServer-Bigdata/trunk/spark-warehouse")
      .config("spark.testing.memory", "2147480000")
      .getOrCreate()

    val sparkConf = spark.sparkContext
    val sql = spark.sqlContext
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val zook = "172.16.12.38:2181"
    val numThreads = 1
    val group = "1"
    val topics = "web_con"
    val topicMap: Map[String, Int] = topics.split(",").map((_, numThreads.toInt)).toMap

    val DStream: DStream[String] = KafkaUtils.createStream(ssc, zook, group,topicMap,StorageLevel.MEMORY_AND_DISK_SER).map(_._2)

    DStream.foreachRDD(rdd => {

      rdd.foreachPartition(strs => {

        //hbase 配置
        val hbaseConf = HBaseConfiguration.create()
        hbaseConf.set("hbase.zookeeper.quorum","hadoop")
        hbaseConf.set("hbase.zookeeper.property.clientPort", "2181")
        hbaseConf.set(HConstants.HBASE_RPC_TIMEOUT_KEY, "2000000")
        val con=ConnectionFactory.createConnection(hbaseConf)

        val table = s"T_WEBCONDITION"
        var batch: util.ArrayList[Row] = new java.util.ArrayList[Row]()
        val hBaseTable = con.getTable(TableName.valueOf(table))
        //posgre 配置
        val conn = connManager.getPosgreCon()
        val templet =
          s"""INSERT INTO "T_WEBCONDITION" ("response_timeout_times", "cdn_id", "host", "date", "response_time",
             |"hour", "url", "cause", "disabled_times", "check_times")
             |values (?,?,?,?,?,?,?,?,?,?)""".stripMargin

        val pstmt: PreparedStatement = conn.prepareStatement(templet)

        strs.foreach(str =>{
          implicit val formats = Serialization.formats(ShortTypeHints(List()))
          val obj: webCondition = parse(str).extract[webCondition]

          //hbase 插入
          webConditionHbase(batch, obj)

          //posgre 插入
          webConditionPosgre(obj, pstmt)
        })
        hBaseTable.batch(batch)
        hBaseTable.close()
        con.close()

        pstmt.executeBatch()
        pstmt.close()
        conn.close()
      })
    })
    ssc.start()
    ssc.awaitTermination()
  }


  def webConditionHbase(batch: util.ArrayList[Row], obj: webCondition) = {
    val put: Put = new Put(Bytes.toBytes(RowKeyUtils.genaralROW()))
    put.addColumn(Bytes.toBytes("WC"), Bytes.toBytes("response_timeout_times"), Bytes.toBytes(obj.response_timeout_times.toString))
    put.addColumn(Bytes.toBytes("WC"), Bytes.toBytes("cdn_idP"), Bytes.toBytes(obj.cdn_id.toString))
    put.addColumn(Bytes.toBytes("WC"), Bytes.toBytes("host"), Bytes.toBytes(obj.host))
    put.addColumn(Bytes.toBytes("WC"), Bytes.toBytes("date"), Bytes.toBytes(obj.date))
    put.addColumn(Bytes.toBytes("WC"), Bytes.toBytes("response_time"), Bytes.toBytes(obj.response_time))
    put.addColumn(Bytes.toBytes("WC"), Bytes.toBytes("hour"), Bytes.toBytes(obj.hour.toString))
    put.addColumn(Bytes.toBytes("WC"), Bytes.toBytes("url"), Bytes.toBytes(obj.url))
    put.addColumn(Bytes.toBytes("WC"), Bytes.toBytes("cause"), Bytes.toBytes(obj.cause))
    put.addColumn(Bytes.toBytes("WC"), Bytes.toBytes("disabled_times"), Bytes.toBytes(obj.disabled_times.toString))
    put.addColumn(Bytes.toBytes("WC"), Bytes.toBytes("check_times"), Bytes.toBytes(obj.check_times.toString))
    batch.add(put)
  }



  def webConditionPosgre(obj: webCondition, pstmt: PreparedStatement) = {
    pstmt.setInt(1, obj.response_timeout_times)
    pstmt.setInt(2, obj.cdn_id)
    pstmt.setString(3, obj.host)
    pstmt.setString(4, obj.date)
    pstmt.setString(5, obj.response_time)
    pstmt.setInt(6, obj.hour)
    pstmt.setString(7, obj.url)
    pstmt.setString(8, obj.cause)
    pstmt.setInt(9, obj.disabled_times)
    pstmt.setInt(10, obj.check_times)
    pstmt.addBatch()
  }

}

case class webCondition(response_timeout_times: Int, cdn_id:Int, host:String, date:String,
                        response_time: String, hour:Int, url:String, cause:String, disabled_times:Int,
                        check_times:Int)