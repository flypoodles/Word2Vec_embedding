package helper

object cosineSim {


  // takes "[12.32 123,55]" => [12,32 123,55]
  def processStringArray(str: String) : Array[Double] = {
    //println("hello world: " + str)
    val result = str.split(" ").map((value) => {value.toDouble})

    //println("hello world: " + result.mkString(","))
    result
  }

  def processString(str:String) : (Integer, Array[Double]) = {
    val splitedStr = str.split(",")
    //println(splitedStr.mkString("|"))
    val token = splitedStr(0).trim().toInt
    val embeddingStr = splitedStr(2)
    val embedding:Array[Double] = getTokenEmbedding(embeddingStr)
    (token, embedding)
  }

  def getTokenEmbedding(str:String) : Array[Double] = {

    val begin = str.indexOf("[")
    val end = str.length- str.indexOf("]")
    //println("hello world: " + str.dropRight(end).drop(begin+1))
    processStringArray(str.dropRight(end).drop(begin+1))
  }


  def cosineSimilarity(x: Array[Double], y: Array[Double]): Double = {
    require(x.length == y.length)
    dotProduct(x, y)/(magnitude(x) * magnitude(y))
  }

  /*
   * Return the dot product of the 2 arrays
   * e.g. (a[0]*b[0])+(a[1]*a[2])
   */
  def dotProduct(x: Array[Double], y: Array[Double]): Double = {
    x.zip(y).map { case (x, y) => x * y }.sum
  }

  /*
   * Return the magnitude of an array
   * We multiply each element, sum it, then square root the result.
   */
  def magnitude(x: Array[Double]): Double = {
    math.sqrt(x.map(i => i*i).sum)
  }


  def findMostSimular( theToken: (Integer, Array[Double]), allTokens: List[(Integer,Array[Double])]) : (Integer, Double) = {

    val similarToken :(Integer, Array[Double])= allTokens.fold(allTokens.head)((op1 : (Integer, Array[Double]), op2: (Integer, Array[Double])) => {
      val cos1 = cosineSimilarity(theToken._2, op1._2)
      val cos2 = cosineSimilarity(theToken._2, op2._2)
      if(cos1 < cos2){
        op2
      } else{
        op1
      }
    })

    (similarToken._1, cosineSimilarity(theToken._2, similarToken._2))
  }




}
