package sample

import com.programmerare.crsTransformations.CrsTransformationAdapterLeafFactory
import org.scalatest.matchers.should.Matchers._
import org.scalatest.funsuite.AnyFunSuite

// the import below is from the Java library "crs-transformation-constants"
import com.programmerare.crsConstants.constantsByAreaNameNumber.v10_027.EpsgNumber

// the three imports below are from the core Kotlin library "crs-transformation-adapter-core"
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory
import com.programmerare.crsTransformations.CrsTransformationResult

// cd sample_code/scala
// sbt test
class CrsTransformationTest extends AnyFunSuite {

  test("Transform coordinates from EPSG 4326 to EPSG 3006 with all CrsTransformationAdapters") {
    val allCrsTransformationAdapters = CrsTransformation.getAllCrsTransformationAdapters()
    val inputCoordinate = CrsTransformation.getInputCoordinate()
    val targetCrsIdentifier = CrsTransformation.getTargetCrsIdentifier()

    // Transformation from EPSG 4326 to EPSG 3006:
    assert(inputCoordinate.getCrsIdentifier.getEpsgNumber === EpsgNumber.WORLD__WGS_84__4326) // 4326 is implicit/default EPSG for latitude/longitude
    assert(targetCrsIdentifier.getEpsgNumber === EpsgNumber.SWEDEN__SWEREF99_TM__3006)

    // The transformations with all 10 crsTransformationAdapters
    
    val expectedTotalNumberOfCrsTransformationAdapters = 10
    
    assertResult(expectedTotalNumberOfCrsTransformationAdapters)(allCrsTransformationAdapters.size)

    val results = CrsTransformation.transform(inputCoordinate, targetCrsIdentifier, allCrsTransformationAdapters)
    assertResult(expectedTotalNumberOfCrsTransformationAdapters)(results.size)
    
    results.foreach { result =>
      assertTransformationResult(result)
    }

    val expectedNumberOfLeafCrsTransformationAdapters = 6
    assertResult(expectedNumberOfLeafCrsTransformationAdapters)(CrsTransformationAdapterLeafFactory.getInstancesOfAllKnownAvailableImplementations().size)
    
  }

  private lazy val expectedOnputCoordinate: CrsCoordinate = CrsCoordinateFactory.yx(6580822.0, 674032.0, EpsgNumber.SWEDEN__SWEREF99_TM__3006)

  private def assertTransformationResult(result: CrsTransformationResult) = {
    val errorMessageWithInformationAboutFailingImplementation = s" Failing implementation: ${result.getCrsTransformationAdapterResultSource.getImplementationType}"

    assertResult(true, errorMessageWithInformationAboutFailingImplementation) {
      result.isSuccess
    }

    val outputCoordinate = result.getOutputCoordinate
    assertResult(EpsgNumber.SWEDEN__SWEREF99_TM__3006, errorMessageWithInformationAboutFailingImplementation) {
      outputCoordinate.getCrsIdentifier.getEpsgNumber
    }

    withClue(errorMessageWithInformationAboutFailingImplementation){
      outputCoordinate.getX shouldEqual expectedOnputCoordinate.getX +- 0.5
    }

    withClue(errorMessageWithInformationAboutFailingImplementation){
      outputCoordinate.getY shouldEqual expectedOnputCoordinate.getY +- 0.5
    }
  }
  
}
