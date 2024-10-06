import helper.slideWindow.{calculateArraySum, calculateAvg}
import org.scalatest.flatspec.AnyFlatSpec
class ArrayAvgTest extends AnyFlatSpec {


  class Fixture {
    //val builder = new StringBuilder("ScalaTest is ")
    //val buffer = new ListBuffer[String]
    val stringTester = "123,[12.2 13.3 14.4],headswas"
    val doubleArray1 :Array[Double]= Array(4,5,6)
    val doubleArray2 : Array[Double] = Array(4, 1, 3)
    val doubleArray3 : Array[Double] = Array(10, 12, 9)



  }

  def fixture = new Fixture

  behavior of "Array Sum"

  it should "caculate the sum of all array" in {
    val f = fixture
    val ArrList : List[List[Double]] = List( f.doubleArray2.toList, f.doubleArray3.toList)
    val result = calculateArraySum(ArrList)
    val actual = List(14,13,12)

    info("result: " + result.mkString("["," ","]"))
    info("actual: " + actual.mkString("["," ","]"))

    assert(result == actual)
  }

  behavior of "Array Average"
  it should "compute the average of all array" in {
    val f = fixture
    val ArrList : List[List[Double]] = List(f.doubleArray1.toList ,f.doubleArray2.toList, f.doubleArray3.toList)
    val result = calculateAvg(ArrList)
    val actual :List[Double]= List(6,6,6)

    info("result: " + result.mkString("["," ","]"))
    info("actual: " + actual.mkString("["," ","]"))
    assert(result == actual)
  }
}
