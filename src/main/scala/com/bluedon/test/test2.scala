package main.scala.com.bluedon.test

/**
  * Created by huoguang on 2017/6/15.
  */
import java.io.StringReader
import java.sql.{Connection, DriverManager, ResultSet}

import org.wltea.analyzer.core.{IKSegmenter, Lexeme}
import org.wltea.analyzer.lucene.IKAnalyzer

object Iktest {

  def main(args: Array[String]) {
    val a = Seq(1)
    val b = Seq(2)
    val str = s"这个中文分词用着不方便。"
    var str1 :List[String] = Nil
    val reader = new StringReader(str)

    val ik: IKSegmenter = new IKSegmenter(reader, true)
    var lexeme: Lexeme = null
    val ikAnaly = new IKAnalyzer()

    while ((lexeme = ik.next()) != null) {

      println(s"!!! " + lexeme.getLexemeText)
//      str1 = str1 :+ lexeme.getLexemeText
    }
//    str1.foreach(println(_))

//    val driver = "com.mysql.jdbc.Driver"
//    //连接本地Mysql下名为lizhuoliang的库
//    val url = "jdbc:mysql://localhost/lizhuoliang"
//    //用户名
//    val username = "root"
//    //密码
//    val password = "root123"
//
//    var connection:Connection = null
//
//    try {
////初始化mysql的connection驱动
//      Class.forName(driver)
//      connection = DriverManager.getConnection(url, username, password)
////建立操作的控制台
//      val statement = connection.createStatement()
//      //执行查询语句 查询test表
//      val resultSet: ResultSet = statement.executeQuery("select name, age from test")
//      //遍历查询回来的结果集合，直到没有next元素为止
//      while ( resultSet.next() ) {
//        val name = resultSet.getString("name")
//        val age = resultSet.getString("age")
//        //打印出来所有查询结果
//        println("name, age = " +  name + ", " + age)
//      }
//    } catch {
//      case e => e.printStackTrace
//    }
//    //用完了管理相关连接回收资源
//    connection.close()
  }
}
