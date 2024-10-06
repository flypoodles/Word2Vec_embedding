import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.{EncodingType, IntArrayList}
import org.apache.hadoop.io.{ArrayPrimitiveWritable, DoubleWritable, IntWritable, Text, Writable}

import java.io.{DataInput, DataOutput}

// Define a custom class that implements Writable
class CloseToken(val closeWord: Text, val value: DoubleWritable) extends Writable {
  def this() = this(new Text(""), new DoubleWritable(0))



  def getCloseWord : Text = {
    new Text(closeWord.toString)
  }

  def getValue : DoubleWritable = {
    new DoubleWritable(value.get())
  }

  // Implement the write method (serialize the fields)
  override def write(out: DataOutput): Unit = {
    closeWord.write(out)
    value.write(out)
  }


  // Implement the readFields method (deserialize the fields)
  override def readFields(in: DataInput): Unit = {
    closeWord.readFields(in)
    value.readFields(in)
  }


  // Override the toString method for debugging or printing
  override def toString: String = {
//    val arr1 = new IntArrayList()
//    arr1.add(word.toString.toInt)
//    val vocab1 = encoder.decode(arr1)
//    val arr2 = new IntArrayList()
//    arr2.add(closeWord.toString.toInt)
//    val vocab2 = encoder.decode(arr2)
    val String = s"${closeWord.toString}, ${value}"
    String
  }
}