import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingType
import com.typesafe.config.ConfigFactory
import helper.processFile
import org.apache.hadoop.conf._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io._
import org.apache.hadoop.mapred._
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.util._
import org.slf4j.LoggerFactory
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.{VocabWord, Word2Vec}
import org.deeplearning4j.text.sentenceiterator.{CollectionSentenceIterator, FileSentenceIterator, LineSentenceIterator, SentenceIterator}
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.{DefaultTokenizerFactory, TokenizerFactory}
import java.io.IOException
import java.util
import scala.jdk.CollectionConverters._
import org.nd4j.linalg.factory.Nd4j
import com.typesafe.config.ConfigFactory


object TokenEmbeddingProgram {
  private val logger = LoggerFactory.getLogger(this.getClass)
  private val myConf = ConfigFactory.load()

  class Map extends MapReduceBase with Mapper[LongWritable, Text, Text, TokenOccurance]{
    private final val one = new IntWritable(1)
    private val word = new Text()

    private val registry = Encodings.newDefaultEncodingRegistry()

    private val encoding = registry.getEncoding(EncodingType.CL100K_BASE)

    private val word2VecConf = myConf.getConfig("myWordToVecConfig")

    private def processWord(word: String, word2Vec: Word2Vec, output: OutputCollector[Text, TokenOccurance]): Unit = {
      val embed = word2Vec.getWordVector(word);
      if (embed != null) {
        output.collect(new Text(word), new TokenOccurance(new IntWritable(one.get()), new ArrayPrimitiveWritable(embed)))
      } else {
        logger.info("Cannot find the embedding for token: {}", word)
      }
    }


    @throws[IOException]
    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, TokenOccurance], reporter: Reporter): Unit = {
      logger.info("Entering mapper")
      val myList = value.toString.split("[.!?]")
      val newList = myList.map(cur => encoding.encode(cur).toArray.mkString(" "))
      val collectIter = new CollectionSentenceIterator(newList.toSeq.asJava)
      val tokenizerFactory = new DefaultTokenizerFactory();

      // Build Word2Vec model
      val word2Vec = new Word2Vec.Builder()
        .minWordFrequency(word2VecConf.getInt("minWordFrequency")) // Minimum frequency of words to be included
        .iterations(word2VecConf.getInt("iterations")) // Number of training iterations
        .layerSize(word2VecConf.getInt("layerSize")) // Size of the word vectors
        .seed(word2VecConf.getInt("seed"))
        .windowSize(word2VecConf.getInt("windowSize")) // Context window size for embeddings
        .iterate(collectIter)
        .tokenizerFactory(tokenizerFactory)
        .build();

      // Train the model
      word2Vec.fit();
      newList.foreach(sentence => sentence.split(" ").foreach(word => processWord(word, word2Vec, output)))
      logger.info("Exiting mapper")
    }
  }

  class Reduce extends MapReduceBase with Reducer[Text, TokenOccurance, Text, TokenOccurance] {
    def reduceFunc(value1: TokenOccurance, value2: TokenOccurance) = {
      val valueOne = value1.getOccurance.get()
      val valueTwo = value2.getOccurance.get()
      new TokenOccurance(new IntWritable(valueOne + valueTwo), new ArrayPrimitiveWritable(value1.getEmbedding.get()))
    }


    override def reduce(key: Text, values: util.Iterator[TokenOccurance], output: OutputCollector[Text, TokenOccurance], reporter: Reporter): Unit = {
      logger.info("Entering Reducer")
      val tokenList = values.asScala.toList

      val doubleList: List[List[Double]] = tokenList.map(curToken => curToken.getEmbedding.get().asInstanceOf[Array[Double]].toList)
      val finalToken = tokenList.reduce((A, B) => reduceFunc(A, B))
      val avgList = helper.slideWindow.calculateAvg(doubleList)
      val actualToken = new TokenOccurance(new IntWritable(finalToken.getOccurance.get), new ArrayPrimitiveWritable(avgList.toArray))
      output.collect(new Text(key.toString), actualToken)
      logger.info("Exiting Reducer")
    }
  }

  def runTokenEmbedding(inputPath: String, outputPath: String): RunningJob ={

    //println(Nd4j.getBackend().getClass().getName())
    val conf: JobConf = new JobConf(this.getClass)
    val tokenEmbedConf = myConf.getConfig("myTokenEmbeddingConfig")
    conf.setJobName(tokenEmbedConf.getString("name"))
    // conf.set("fs.defaultFS", tokenEmbedConf.getString("fileSystem")) // comment this out for aws !
    conf.set("mapreduce.job.maps", tokenEmbedConf.getString("mapper"))
    conf.set("mapreduce.job.reduces", tokenEmbedConf.getString("reducer"))
    conf.set("mapred.textoutputformat.separator", tokenEmbedConf.getString("separator"));
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[TokenOccurance])
    conf.setMapperClass(classOf[Map])
    conf.setCombinerClass(classOf[Reduce])
    conf.setReducerClass(classOf[Reduce])
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, TokenOccurance]])
    FileInputFormat.setInputPaths(conf, new Path(inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(outputPath))
    JobClient.runJob(conf)
  }
}

