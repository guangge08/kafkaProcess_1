package main.scala.com.bluedon.test


import java.io.{BufferedReader, File, InputStreamReader}
import java.nio.charset.StandardCharsets
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.{Document, Field, StringField, TextField}
import org.apache.lucene.index.{DirectoryReader, IndexWriter, IndexWriterConfig}
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{IndexSearcher, Query, ScoreDoc, TopDocs}
import org.apache.lucene.store.FSDirectory


/**
  * Created by huoguang on 2017/6/27.
  */
object LuceneTest extends App{


//  indexAllFiles
  searchIndex
  /**
    * Index all text files under a directory.
    * */

  def indexAllFiles() = {
    val index = s"F:\\search"
    val docsPath = s"F:\\lucene\\try.txt"
    val docDir: Path = Paths.get(docsPath)

    if (!Files.isReadable(docDir)) {
      println("Document directory '" +docDir.toAbsolutePath()+
        "' does not exist or is not readable, please check the path")
    }
    print(s"Go on !!!")
    //标准分词器
    val analyzer: StandardAnalyzer = new StandardAnalyzer()
    //本地储存所以/内存储存索引
    val directory =  FSDirectory.open(Paths.get(index))
    val iwc = new IndexWriterConfig(analyzer)
    iwc.setOpenMode(OpenMode.CREATE)

    val writer = new IndexWriter(directory, iwc)
    indexDoc(writer, docDir)
    writer.close()
  }

  def indexDoc(writer:IndexWriter, file:Path , lastModified:Long = 1000) = {
    val stream = Files.newInputStream(file)
    val doc = new Document()
    doc.add(new TextField("path", "gogogo", Store.YES))
    doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))))
    writer.addDocument(doc)
  }

  def searchIndex() = {
    val index = s"F:\\search"
    val field = s"contents"
    val queries = null
    val repeat = 0
    val raw = false
    val querryString = null
    val hitsPerPage = 10

    val reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)))
    val search = new IndexSearcher(reader)
    val analyzer: StandardAnalyzer = new StandardAnalyzer()
//    val in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
    val parser = new QueryParser(field, analyzer)
    val query: Query = parser.parse("go")
    val result: TopDocs = search.search(query, 1000)
    val hits: Array[ScoreDoc] = result.scoreDocs
    println(s"num is " + hits.length)
    for (i <- 0 until hits.length) {
      val doc: Document = search.doc(hits(i).doc)
      println(s"!!! " + doc.get("contents"))
      println(s"!!! " + doc.get("path"))
    }
  }


//  indexDoc(writer, file, attrs.lastModifiedTime().toMillis())
  def listFiles() = {
    println("打印所有的子目录")

    implicit def makeFileVisitor(f: (Path) => Unit) = new SimpleFileVisitor[Path] {

      override def visitFile(p: Path, attrs: BasicFileAttributes) = {
        f(p)
        FileVisitResult.CONTINUE
      }
    }
    val dir: File = new File(s"F:\\search")
    Files.walkFileTree(dir.toPath, (f: Path) => println(f))
  }
}
