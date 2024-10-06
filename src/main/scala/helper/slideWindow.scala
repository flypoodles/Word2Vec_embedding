package helper

object slideWindow {

  def computeLine(currentList: List[Int], windowSize:Int, currentIndex:Int) : List[Int]= {
    currentList match {
      case hd :: tl => hd :: computeLine(tl, windowSize, currentIndex + 1)
      case _ if (currentIndex < windowSize) => 0 :: computeLine(currentList, windowSize, currentIndex + 1)
      case _ => Nil
    }
  }

  def mymap(data: List[Int], windowSize:Int)(f: (List[Int], Int, Int)=> List[Int]): List[List[Int]] = {
    data match {
      case hd :: tl => f(data,windowSize,0) :: mymap(tl,windowSize)(computeLine)
      case _ => Nil
    }
  }

  def computeSlideWindow(tokenList : List[Int], windowSize : Int) : List[List[Int]]= {

    mymap(tokenList,windowSize)(computeLine)
  }


  private def ArraySum(arr1 : List[Double], arr2 : List[Double]) : List[Double] = {
    val zipped = arr1.zip(arr2)
    zipped.map{case (x,y) => x+y}
  }

  def calculateArraySum(arr: List[List[Double]]): List[Double] = {
    arr.reduce((arr1, arr2) => ArraySum(arr1, arr2))
  }

  def calculateAvg(arr: List[List[Double]]):List[Double] = {
    val length = arr.length

    val sumArray = calculateArraySum(arr)

    sumArray.map(value => value / length)
  }

}
