package main.scala.com.bluedon

import java.io.InputStream
import java.util
import java.util.Properties

import main.scala.com.bluedon.utils.{LogSupport, RowKeyUtils}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put, Row}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HConstants, TableName}
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json4s.ShortTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.JsonMethods._
/**
  * Created by huoguang on 2017/6/2.
  */
//case class webScan(uuid:String, webType:String, action:String, target:String, risk:String,
//                   name:String, description:String, solution:String, family:String, reference:String,
//                   cve:String, cnvd:String, cnnvd:String)

object kafkaReceive extends LogSupport {

//  val properties:Properties = new Properties()
//  val ipstream:InputStream=this.getClass().getResourceAsStream("/manage.properties")
//  properties.load(ipstream)
//
//  val spark = SparkSession
//    .builder()
//    .master("local[2]")
//    .appName("localTest")
//    .config("spark.sql.warehouse.dir", "F:/code/AnalyzeServer-Bigdata/trunk/spark-warehouse")
//    .config("spark.testing.memory", "2147480000")
//    .getOrCreate()
//
//  val sc = spark.sparkContext
//  val sql = spark.sqlContext
//
//  val ssc = new StreamingContext(sc, Seconds(10))

//  val kafkaPara = Map(
//    "zookeeper.connect" -> "172.16.12.38:2181",
//    "group.id" -> "1",
//    "zookeeper.connection.timeout.ms" -> "1000"
//  )
//  val zook = "172.16.12.38:2181"
//  //数据量不大单线程
//  val numThreads = 1
//  val group = "1"
//  val topics = "event-test"
//  val topicMap: Map[String, Int] = topics.split(",").map((_, numThreads.toInt)).toMap
//
//  val DStream: DStream[String] = KafkaUtils.createStream(ssc, zook, group,topicMap,StorageLevel.MEMORY_AND_DISK_SER).map(_._2)
//
//
//
//  ssc.start()
//  ssc.awaitTermination()

  def main(args:Array[String]) = {
    log.info(s"begin !!!")
    implicit val formats = Serialization.formats(ShortTypeHints(List()))
    val json = "{\"uuid\":\"7f06e968-f93c-5a3e-1b49-baceb5b5abb6\",\"webType\":\"web\",\"action\":\"result\",\"target\":\"http://www.baidu.com\",\"risk\":\"H\",\"name\":\"df\",\"description\":\"i\",\"solution\":\"ii\",\"family\":\"uy\",\"reference\":\"jj\",\"cve\":\"i\",\"cnvd\":\"ii\",\"cnnvd\":\"uy\"}"
    val obj = parse(json).extract[webScan]




    val table = s"T_WEBSCAN"
    val hbaseConf = HBaseConfiguration.create()

    hbaseConf.set("hbase.zookeeper.quorum","hadoop")
    hbaseConf.set("hbase.zookeeper.property.clientPort", "2181")
    hbaseConf.set(HConstants.HBASE_RPC_TIMEOUT_KEY, "2000000")

    var batch = new util.ArrayList[Row]()

    val con=ConnectionFactory.createConnection(hbaseConf)
    val hBaseTable = con.getTable(TableName.valueOf(table))
//    val put: Put = new Put(Bytes.toBytes(RowKeyUtils.genaralROW()))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("uuid"), Bytes.toBytes("7f06e968-f93c-5a3e-1b49-baceb5b5abb6"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("type"), Bytes.toBytes("web"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("action"), Bytes.toBytes("result"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("target"), Bytes.toBytes("http://www.baidu.com"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("risk"), Bytes.toBytes("H"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("name"), Bytes.toBytes("a"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("description"), Bytes.toBytes("b"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("solution"), Bytes.toBytes("c"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("family"), Bytes.toBytes("d"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("reference"), Bytes.toBytes("e"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("cve"), Bytes.toBytes("f"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("cnvd"), Bytes.toBytes("g"))
//    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("cnnvd"), Bytes.toBytes("h"))
//    batch.add(put)
//
//    hBaseTable.batch(batch)
//    hBaseTable.close()
//    con.close()
  }

  def process(ds: DStream[String]) = {

  }

}
