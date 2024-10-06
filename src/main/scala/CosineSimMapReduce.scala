import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.{EncodingType, IntArrayList}
import com.typesafe.config.ConfigFactory
import helper.cosineSim.processString
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{ArrayPrimitiveWritable, DoubleWritable, IntWritable, LongWritable, Text}
import org.apache.hadoop.mapred.{FileInputFormat, FileOutputFormat, JobClient, JobConf, MapReduceBase, Mapper, OutputCollector, Reducer, Reporter, RunningJob, TextInputFormat, TextOutputFormat}
import org.apache.hadoop.fs.FileSystem
import org.nd4j.common.collection.IntArrayKeyMap.IntArray
import org.slf4j.LoggerFactory

import java.io.{BufferedReader, IOException, InputStreamReader}
import java.net.URI
import java.util
import scala.reflect.internal.util.NoPosition.source

object CosineSimMapReduce {


  private val logger = LoggerFactory.getLogger(this.getClass)
  private val conf: JobConf = new JobConf(this.getClass)
  private  val regis = Encodings.newDefaultEncodingRegistry();
  private  val encoder = regis.getEncoding(EncodingType.CL100K_BASE);
  private val myConf = ConfigFactory.load()
  class Map extends MapReduceBase with Mapper[LongWritable, Text, Text, CloseToken]{

    private def getVocabulary(tokenID:Integer) :String= {
      val array = new IntArrayList()
      array.add(tokenID)
      encoder.decode(array)
    }
    @throws[IOException]
    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, CloseToken], reporter: Reporter): Unit = {
      logger.info("Enter Mapper for cosine:")

      logger.info(value.toString)
      val allEmbed = value.toString.split("[|]").map(line => processString(line)).toList


      allEmbed.foreach( currentToken => {val mostSimilar = helper.cosineSim.findMostSimular(currentToken, allEmbed.filter(thisToken => thisToken._1 != currentToken._1))
                                          val exodus = new CloseToken(new Text(getVocabulary(mostSimilar._1)), new DoubleWritable(mostSimilar._2))
                                       output.collect(new Text(getVocabulary(currentToken._1)), exodus)})

    }
  }

  class Reduce extends MapReduceBase with Reducer[Text, CloseToken, Text, CloseToken] {

    override def reduce(key: Text, values: util.Iterator[CloseToken], output: OutputCollector[Text, CloseToken], reporter: Reporter): Unit = {

      val pair = values.next()
      output.collect(new Text(key.toString), new CloseToken(new Text(pair.getCloseWord.toString), new DoubleWritable (pair.getValue.get() )))
    }
  }

  def runCosineSim(inputPath: String, outputPath: String): RunningJob ={

    //println(Nd4j.getBackend().getClass().getName())
    val CosineConf = myConf.getConfig("myCosineSimilarConfig")
    conf.setJobName(CosineConf.getString("name"))
    //conf.set("fs.defaultFS", CosineConf.getString("fileSystem")) // comment this out for aws
    conf.set("mapreduce.job.maps", CosineConf.getString("mapper"))
    conf.set("mapreduce.job.reduces",CosineConf.getString("reducer"))
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[CloseToken])
    conf.setMapperClass(classOf[Map])
    conf.setCombinerClass(classOf[Reduce])
    conf.setReducerClass(classOf[Reduce])
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, CloseToken]])
    conf.set("mapred.textoutputformat.separator", CosineConf.getString("separator"));
    FileInputFormat.setInputPaths(conf, new Path(inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(outputPath))
    JobClient.runJob(conf)
  }
}
