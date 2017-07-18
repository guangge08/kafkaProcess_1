package main.scala.com.bluedon.entity

import java.io.{File, FileInputStream}

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFSheet, XSSFWorkbook}

/**
  * Created by huoguang on 2017/6/12.
  */
object PoiExcel2k7 {

  def main(args:Array[String]) = {

    val filePath = s"E:\\logs"
    val file = new File(filePath)
    val xsls = file.listFiles().map(_.getAbsolutePath).head
    val stream = new FileInputStream(xsls)
      val wk = new HSSFWorkbook(stream)
//    val wk: XSSFWorkbook = new XSSFWorkbook(stream)
    stream.close()
    val sheet = wk.getSheetAt(0)
    val row = sheet.getRow(20)
    val cell = row.getCell(6)
    println(s"${cell.getCellStyle}")
    val a = s"a"
    val bat = a.getBytes().head
    val num = bat-96
    println(num)

  }
}
