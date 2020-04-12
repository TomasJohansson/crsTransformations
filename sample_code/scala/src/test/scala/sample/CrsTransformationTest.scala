package sample

import org.scalatest.funsuite.AnyFunSuite

// the import below is from the Java library "crs-transformation-constants"
import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_5_4.EpsgNumber

// the two imports below are from the core Kotlin library "crs-transformation-adapter-core"
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory

// the imports below are from Kotlin adapter implementation libraries
import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J
import com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools

// cd sample_code/scala
// sbt test
class CrsTransformationTest extends AnyFunSuite {
  
  // VERY basic "test" below just to show that the Java and Kotlin types (retrieved with SBT for Scala)
  // can be referred to from this Scala code  
  
  test("Java and Kotlin types available") {
    
    assert(EpsgNumber.SWEDEN__SWEREF99_TM__3006 === 3006)
    
    // the above constant class is implemented with Java while the below types are implemented with Kotlin  

    new CrsTransformationAdapterProj4J
    new CrsTransformationAdapterGeoPackageNGA
    new CrsTransformationAdapterOrbisgisCTS
    new CrsTransformationAdapterGooberCTL
    new CrsTransformationAdapterGeoTools

    val coordinate: CrsCoordinate = CrsCoordinateFactory.latLon(59.330231, 18.059196)
  }
  
  // TODO some real test with coordinate transformation 
}
