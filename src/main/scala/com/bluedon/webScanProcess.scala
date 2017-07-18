package main.scala.com.bluedon

import java.io.InputStream
import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, Statement}
import java.util
import java.util.{Date, Properties}

import main.scala.com.bluedon.utils.{LogSupport, RowKeyUtils, connManager}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put, Row}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HConstants, TableName}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json4s.ShortTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.JsonMethods._
/**
  * Created by huoguang on 2017/6/5.
  */
object webScanProcess extends LogSupport{


  def main(args:Array[String]) = {

    val properties:Properties = new Properties()
    val ipstream:InputStream=this.getClass().getResourceAsStream("/manage.properties")
    properties.load(ipstream)
//    Logger.getLogger("org").setLevel(Level.ERROR)
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
    val topics = "yunfs"
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

        val table = s"T_WEBSCAN"
        var batch: util.ArrayList[Row] = new java.util.ArrayList[Row]()
        val hBaseTable = con.getTable(TableName.valueOf(table))
        //posgre 配置
        val conn = connManager.getPosgreCon()
        val templet =
          s"""INSERT INTO "T_WEBSCAN" ("task_uuid", "task_type", "task_action", "result_target", "result_risk", "result_name",
             |"result_description", "result_solution", "result_reference", "result_family", "result_cve","result_cnvd","result_cnnvd")
             |values (?,?,?,?,?,?,?,?,?,?,?,?,?)""".stripMargin

        val pstmt: PreparedStatement = conn.prepareStatement(templet)

        strs.foreach(str =>{
          implicit val formats = Serialization.formats(ShortTypeHints(List()))
          val obj: webScan = parse(str).extract[webScan]

          //hbase 插入
          webScanHbase(batch, obj)

          //posgre 插入
          webScanPosgre(obj, pstmt)
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

  def webScanHbase(batch: util.ArrayList[Row], obj: webScan) = {
    val put: Put = new Put(Bytes.toBytes(RowKeyUtils.genaralROW()))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("uuid"), Bytes.toBytes(obj.task_uuid))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("type"), Bytes.toBytes(obj.task_type))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("action"), Bytes.toBytes(obj.task_action))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("target"), Bytes.toBytes(obj.result_target))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("risk"), Bytes.toBytes(obj.result_risk))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("name"), Bytes.toBytes(obj.result_name))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("description"), Bytes.toBytes(obj.result_description))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("solution"), Bytes.toBytes(obj.result_solution))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("family"), Bytes.toBytes(obj.result_family))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("reference"), Bytes.toBytes(obj.result_reference))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("cve"), Bytes.toBytes(obj.result_cve))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("cnvd"), Bytes.toBytes(obj.result_cnvd))
    put.addColumn(Bytes.toBytes("WS"), Bytes.toBytes("cnnvd"), Bytes.toBytes(obj.result_cnnvd))
    batch.add(put)
  }

  def webScanPosgre(obj: webScan, pstmt: PreparedStatement) = {
    pstmt.setString(1, obj.task_uuid)
    pstmt.setString(2, obj.task_type)
    pstmt.setString(3, obj.task_action)
    pstmt.setString(4, obj.result_target)
    pstmt.setString(5, obj.result_risk)
    pstmt.setString(6, obj.result_name)
    pstmt.setString(7, obj.result_description)
    pstmt.setString(8, obj.result_solution)
    pstmt.setString(9, obj.result_family)
    pstmt.setString(10, obj.result_reference)
    pstmt.setString(11, obj.result_cve)
    pstmt.setString(12, obj.result_cnvd)
    pstmt.setString(13, obj.result_cnnvd)
    pstmt.addBatch()
  }

}

case class webScan(task_uuid:String, task_type:String, task_action:String, result_target:String, result_risk:String,
                   result_name:String, result_description:String, result_solution:String, result_family:String, result_reference:String,
                   result_cve:String, result_cnvd:String, result_cnnvd:String)
