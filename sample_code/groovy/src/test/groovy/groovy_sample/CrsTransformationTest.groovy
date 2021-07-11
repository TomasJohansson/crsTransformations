package groovy_sample

import spock.lang.Shared
import spock.lang.Specification
import com.programmerare.crsConstants.constantsByAreaNameNumber.v10_027.EpsgNumber
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory
import com.programmerare.crsTransformations.CrsTransformationAdapterLeafFactory

// cd sample_code/groovy 
// gradlew test 
class CrsTransformationTest extends Specification {
    
    @Shared expectedOutputCoordinate
    @Shared maxDeltaValueWhenComparingXYvalues
    
    def setupSpec() {
        expectedOutputCoordinate = CrsCoordinateFactory.xy(674032, 6580822, EpsgNumber.SWEDEN__SWEREF99_TM__3006)
        maxDeltaValueWhenComparingXYvalues = 0.5d // 0.5 meters
    }
    
    def "Transform coordinates from EPSG 4326 to EPSG 3006 with all CrsTransformationAdapters"() {
        setup:
        def crsTransformation = new CrsTransformation()
        def allCrsTransformationAdapters = crsTransformation.getAllCrsTransformationAdapters()
        def inputCoordinate = crsTransformation.getInputCoordinate()
        def targetCrsIdentifier = crsTransformation.getTargetCrsIdentifier()
        def expectedTotalNumberOfCrsTransformationAdapters = 10
        def expectedNumberOfLeafCrsTransformationAdapters = 6
        
        when:
        def results = crsTransformation.transform(inputCoordinate, targetCrsIdentifier, allCrsTransformationAdapters)
        
        then:
        expectedNumberOfLeafCrsTransformationAdapters == CrsTransformationAdapterLeafFactory.getInstancesOfAllKnownAvailableImplementations().size
        expectedTotalNumberOfCrsTransformationAdapters == allCrsTransformationAdapters.size
        expectedTotalNumberOfCrsTransformationAdapters == results.size 
        results.each {
            assertTransformationResult(it)
        }
    }

    // http://spockframework.org/spock/docs/
    // http://spockframework.org/spock/docs/1.3/spock_primer.html    
    // Please note that the code below is NOT idiomatic spock test code
    // so if you want to learn better spock testing then please check out the above links.
    
    private def assertTransformationResult(crsTransformationResult) {
        def errorMessageWithInformationAboutFailingImplementation = " Failing implementation: ${crsTransformationResult.crsTransformationAdapterResultSource.implementationType}"

        assertWithErrorMessage(crsTransformationResult.isSuccess, "isSuccess", errorMessageWithInformationAboutFailingImplementation)

        def outputCoordinate = crsTransformationResult.outputCoordinate
        assertIntegerWithErrorMessage(EpsgNumber.SWEDEN__SWEREF99_TM__3006, outputCoordinate.crsIdentifier.epsgNumber, "EpsgNumber", errorMessageWithInformationAboutFailingImplementation)

        assertDecimalValueWithErrorMessage(expectedOutputCoordinate.x, outputCoordinate.x, maxDeltaValueWhenComparingXYvalues, " x/Eastern/Longitude", errorMessageWithInformationAboutFailingImplementation)
        assertDecimalValueWithErrorMessage(expectedOutputCoordinate.y, outputCoordinate.y, maxDeltaValueWhenComparingXYvalues, " y/Northern/Latitude", errorMessageWithInformationAboutFailingImplementation)
    }
    // the methods below are more similar to what you would do with junit i.e. this is NOT proper testing as it is intended to be done with groovy's "spock" framework  
    private def assertWithErrorMessage(boolConditionExpectedToBeTrue, errorMessage, errorMessageWithInformationAboutFailingImplementation) {
        if(!boolConditionExpectedToBeTrue) {
            throw new AssertionError("Was false but should have been true: $errorMessage , $errorMessageWithInformationAboutFailingImplementation")
        }
    }
    
    private def assertIntegerWithErrorMessage(expectedInt, actualInt, errorMessage, errorMessageWithInformationAboutFailingImplementation) {
        if(expectedInt != actualInt) {
            throw new AssertionError("Expected $expectedInt but was $actualInt , $errorMessage , $errorMessageWithInformationAboutFailingImplementation")
        }
    }

    private def assertDecimalValueWithErrorMessage(expectedDecimalValue, actualDecimalValue, maxDeltaValue, errorMessage, errorMessageWithInformationAboutFailingImplementation) {
        // the type of "decimal" is "Double" from the Kotlin/Java library
        def diff = Math.abs(expectedDecimalValue-actualDecimalValue)
        if(diff > maxDeltaValue) {
            throw new AssertionError("Too big difference: $diff (max delta diff: $maxDeltaValue ) , expectedDecimalValue : $expectedDecimalValue , actualDecimalValue : $actualDecimalValue , $errorMessage , $errorMessageWithInformationAboutFailingImplementation")
        }
    }
}
