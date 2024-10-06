package helper

import com.typesafe.config.ConfigFactory

import java.io._
import scala.annotation.tailrec
import scala.util.Using

object processFile {

  def createAndWriteFile(str: String,name:String ): Unit= {
    val file = new File(name)
    file.createNewFile();
    val writer = new PrintWriter(file.getPath)
    writer.write(str)
    writer.close()
  }


  @tailrec
  def splitFile(xs: Iterator[String], fileLimit: Int, curLine:Int, fileNum:Int,shardDir:String, content: String): Unit = {
    xs.isEmpty match {
      case false if curLine < fileLimit=> splitFile(xs, fileLimit, curLine+1, fileNum,shardDir, content+xs.next())
      case false => {

        println(content)
        createAndWriteFile(content, shardDir + "shard" + fileNum + ".txt")
        splitFile(xs, fileLimit, 0, fileNum+1, shardDir, "")
      }
      case true => {
        createAndWriteFile(content, shardDir + "shard" + fileNum + ".txt")
      }
    }
  }

  def splitFileInput (rawInput:String, shardDir:String):Unit = {

    val totalLines = Using(scala.io.Source.fromFile(rawInput)) { reader =>
       reader.getLines().length
    }.get

    Using(scala.io.Source.fromFile(rawInput)) { reader => {
      val source = reader.getLines()
      val myConf = ConfigFactory.load().getConfig("myTokenEmbeddingConfig")

      val fileLength = totalLines / myConf.getInt("mapper")

      splitFile(source, fileLength, 0, 0,shardDir,"")
    }
    }
  }
  def processEmbedFile(input: String, output: String): Unit ={
    Using(scala.io.Source.fromFile(input)){ reader => {
      val content = reader.getLines().mkString("|")
      createAndWriteFile(content, output)
    }}
  }



}
