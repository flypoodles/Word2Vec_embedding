import com.knuddels.jtokkit._
import com.knuddels.jtokkit.api._
import com.typesafe.config.{Config, ConfigFactory}
import helper.{processFile, slideWindow}
import org.apache.hadoop.io.{ArrayPrimitiveWritable, IntWritable}
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.{VocabWord, Word2Vec}
import org.deeplearning4j.text.sentenceiterator.{CollectionSentenceIterator, FileSentenceIterator, LineSentenceIterator, SentenceIterator}
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.{DefaultTokenizerFactory, TokenizerFactory}

import java.io.File
import scala.jdk.CollectionConverters._
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingType
import helper.processFile.splitFileInput
object Entry{



  def main(args: Array[String]): Unit = {

    //println(args(0) + " " + args(1))

    if (args(0) == "token") {
      TokenEmbeddingProgram.runTokenEmbedding(args(1), args(2))
    } else if (args(0) == "cosine") {
      CosineSimMapReduce.runCosineSim(args(1),args(2))

      } else if (args(0) == "split"){

        splitFileInput(args(1),args(2))

      } else if (args(0) == "processEmbed") {
          helper.processFile.processEmbedFile(args(1),args(2))
    }


  }
}