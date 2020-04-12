package sample

// the package below is from the Java library "crs-transformation-constants"
import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_5_4.EpsgNumber

// cd sample_code/scala
// sbt run
object CrsTransformation {
  def main(args: Array[String]): Unit = {
    println("EpsgNumber.SWEDEN__SWEREF99_TM__3006 " + EpsgNumber.SWEDEN__SWEREF99_TM__3006)    
  }
}
