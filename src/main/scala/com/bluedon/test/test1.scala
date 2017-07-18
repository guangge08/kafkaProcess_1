package main.scala.com.bluedon.test

import org.apache.spark.sql.SparkSession
import org.elasticsearch.spark.sql._
import scala.collection.Map


/**
  * Created by huoguang on 2017/6/20.
  */
object test1 extends App{

  val options = Map("pushdown" -> "true", "es.nodes" -> "172.16.12.38", "es.port" -> "9200")
  //val query="{\"query\":{\"storetime\":{\"recordtime\":{\"gte\":"+ begin +",\"lte\":"+ end +"}}}}"
  val spark: SparkSession = SparkSession
    .builder()
    .master("local[3]")
    .appName("es")
    .config("spark.sql.warehouse.dir", "F:/code/AnalyzeServer-Bigdata/trunk/spark-warehouse")
    .config("spark.testing.memory", "2147480000")
    .config("es.index.auto.create","true")
    .config("es.nodes","172.16.12.38")
    .config("es.port","9200")
    .getOrCreate()
  val startTime=System.currentTimeMillis()
//  val query = """{"query":{"match":{"_id": "14"}}}"""
//  val esdf = sc.esDF(s"netflow/netflow", query)
//  esdf.show()
//  val sqlContext = spark.sqlContext
//  import sqlContext.implicits._
//  val sc = spark.sparkContext
//  val numbers = Map("one" -> 1, "two" -> 2, "three" -> 3)
//  val airports = Map("OTP" -> "Otopeni", "SFO" -> "San Fran")
//  sc.makeRDD(Seq(numbers, airports)).saveToEs("spark/docs")
   val sqlContext = spark.sqlContext
  import sqlContext.implicits._
  val str = spark.sparkContext.textFile( "F:\\search\\DBauto.csv" )
    //val data1 = Map("id" -> 1, "score" -> "3", "srcip" -> "8888", "dstip" -> "9999", "bytesize" -> 12313, "storetime" -> 1111111)

  str.map(_.split(",")).map(p => Person(p(0), p(1), p(2), p(3))).toDF().saveToEs("netflow/netflow")

  val endTime = System.currentTimeMillis()
}

case class Person(srcip: String, dstip:String, bytesize:String, storetime:String)
case class netflowEsObj(rowkey:String)
//class A(in:String) extends Runnable {
//  override def run(): Unit = {
//    while (true){
//      println(s" !!!! $in !!!!")
//    }
//  }
//}
