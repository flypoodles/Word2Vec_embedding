import org.apache.hadoop.io.{ArrayPrimitiveWritable, IntWritable, Writable}

import java.io.{DataInput, DataOutput}
import java.util

// Define a custom class that implements Writable
class TokenOccurance(val occurance: IntWritable, val embedding: ArrayPrimitiveWritable) extends Writable {

  def this() = this(new IntWritable(0), new ArrayPrimitiveWritable(List.apply(0).toArray))


  def getOccurance : IntWritable = {
    new IntWritable(occurance.get())
  }

  def getEmbedding : ArrayPrimitiveWritable = {
    new ArrayPrimitiveWritable(embedding.get())
  }

  // Implement the write method (serialize the fields)
  override def write(out: DataOutput): Unit = {
    occurance.write(out)
    embedding.write(out)
  }


  // Implement the readFields method (deserialize the fields)
  override def readFields(in: DataInput): Unit = {
    occurance.readFields(in)
    embedding.readFields(in)
  }


  // Override the toString method for debugging or printing
  override def toString: String = {
    val obj: Array[Double] = embedding.get().asInstanceOf[Array[Double]]

    val String = s"${occurance.toString}, [${obj.mkString(" ")}]"
    String
  }
}