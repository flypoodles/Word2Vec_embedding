import helper.cosineSim
import helper.cosineSim.{cosineSimilarity, dotProduct, findMostSimular, getTokenEmbedding, magnitude, processString}
import org.eclipse.collections.impl.block.factory.Predicates2.in
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest._



class CosineSimilarTest  extends AnyFlatSpec{
  class Fixture {
    //val builder = new StringBuilder("ScalaTest is ")
    //val buffer = new ListBuffer[String]
    val stringTester = "123,23,[12.2 13.3 14.4],headswas"
    val correctDoubleArray :Array[Double]= Array(12.2, 13.3, 14.4)
    val DoubleArray2 : Array[Double] = Array(2, 1, 3)
    val DoubleArray3 : Array[Double] = Array(10, 12, 8)

    val token1 = 1
    val token2 = 2
    val token3 = 3
    val embeddingList: List[(Integer, Array[Double])] = List((token1, correctDoubleArray), (token2, DoubleArray2), (token3, DoubleArray3))

  }
  def fixture = new Fixture
  behavior of "String"


  // test 1
  it should "properly parsed to array[Double]" in {
      val f = fixture

      val result = getTokenEmbedding(f.stringTester)
      info("result: " + result.mkString("[", ",","]"))
      info("actual: " + f.correctDoubleArray.mkString("[", ",","]"))
      assert(result.sameElements(f.correctDoubleArray))

  }

  it should "properly pared the line to a tuple" in {
    val f = fixture
    val result : (Integer, Array[Double]) = cosineSim.processString(f.stringTester)
    val actual : (Integer, Array[Double])= (123, Array(12.2,13.3,14.4))

    info("result integer: " + result._1)
    info("actual integer: " + actual._1)

    assert(result._1 == actual._1)
    info("result embedding: " + result._2.mkString(","))
    info("actual embedding: " + actual._2.mkString(","))
    assert(result._2 sameElements actual._2)

  }

  behavior of "cosine Similarity"
  // test 2
  it should "produce correct dot product between 2 array" in {
    val f = fixture

    val result = dotProduct(f.DoubleArray2,f.correctDoubleArray)
    val actual = 80.9
    info("result: " + result)
    info("actual: " + actual)
    assert(result== actual)

  }
  // test 3
  it should "produce correct magnitude of a array" in {
    val f = fixture
    val result1 = magnitude(f.correctDoubleArray)
    val actual1 = 23.088742
    val result2 = magnitude(f.DoubleArray2)
    val actual2 = 3.741657

    info("result: " + result1)
    info("actual: " + actual1)
    info("result: " + result2)
    info("actual: " + actual2)
    assert((result1- actual1).abs < .01)
    assert((result2- actual2).abs < .01)
  }
  // test 4
  it should "produce correct cosine of two array" in {
    val f = fixture
    val result = cosineSimilarity(f.correctDoubleArray, f.DoubleArray2)
    val actual=  0.936449
    info("result: " + result)
    info("actual: " + actual)
    assert((result-actual).abs<.01 )
  }

  it should "find the most similar token given a token embedding" in {
    val f = fixture
    val example :(Integer, Array[Double]) = (4, Array(-1,2,4))
    val result :(Integer,Double) = findMostSimular(example, f.embeddingList)

    val actual = 2
    info("result: " + result._1)
    info("actual: " + actual)
    assert(actual == result._1)

    assert((0.699854- result._2).abs < .01)
  }


}
