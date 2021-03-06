package com.programmerare.crsTransformations;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CrsTransformationResultTest extends CrsTransformationResultTestBase {

    private CrsTransformationResult transformResultWithSuccessFalse;
    private CrsCoordinate inputCoordinate;
    private CrsCoordinate outputCoordinate;

//    private CrsTransformationAdapter crsTransformationAdapter;

    @BeforeEach
    void beforeEach() {
        inputCoordinate = CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(0.0, 0.0, 1234);
        outputCoordinate = inputCoordinate;
    }

    @Test
    void transformResult_shouldNotReturnSuccess_whenSuccessParameterIsFalse() {
        transformResultWithSuccessFalse = CrsTransformationResult._createCrsTransformationResult(
            inputCoordinate,
            null,
            null,
            false, // parameter success = false !
            super.compositeAdapterForResultTest,
            super.listOfSubresultsForStatisticsTest,
            null
        );
        assertFalse(transformResultWithSuccessFalse.isSuccess()); // because of success parameter false
    }

    @Test
    void transformResult_shouldReturnSuccess_whenSuccessParameterIsTrue() {
        transformResultWithSuccessFalse = CrsTransformationResult._createCrsTransformationResult(
            inputCoordinate,
            outputCoordinate,
            null,
            true,
            super.compositeAdapterForResultTest,
            super.listOfSubresultsForStatisticsTest,
            null
        );
        assertTrue(transformResultWithSuccessFalse.isSuccess());
    }

    @Test
    void transformResultConstruction_shouldThrowException_whenParametersSuccessFalseAndOutputCoordinateNotNull() {
        assertThrows(
            IllegalStateException.class,
            () -> CrsTransformationResult._createCrsTransformationResult(
                inputCoordinate, 
                outputCoordinate, // not null (which it should be when success false as below)
                null,
                false,
                super.compositeAdapterForResultTest,
                super.listOfSubresultsForStatisticsTest,
                null
            ),
            "unvalid TransformResult object construction should throw exception when success false is combined with output coordinate not being null"
        );
    }

    @Test
    void transformResultConstruction_shouldThrowException_whenParametersSuccessTrueAndOutputCoordinateNull() {
        final CrsCoordinate outputCoordinateNull = null;
        assertThrows(
            IllegalStateException.class,
            () -> CrsTransformationResult._createCrsTransformationResult(
                inputCoordinate,
                outputCoordinateNull, // outputCoordinate = null, then success should be false !
                null,
                true,
                super.compositeAdapterForResultTest,
                super.listOfSubresultsForStatisticsTest,
                null
            ),
            "unvalid TransformResult object construction should throw exception when success true is combined with null as output coordinate"
        );
    }

    @Test
    void transformResultConstruction_shouldThrowException_whenParametersExceptionIsNotNullAndOutputCoordinateNotNull() {
        final RuntimeException someException = new RuntimeException("this is an exception"); 
        final CrsCoordinate outputCoordinateNotNull = this.outputCoordinate;
        assertNotNull(outputCoordinateNotNull); // just to assert what the name claims i.e. not null
        assertThrows(
            IllegalStateException.class,
            () -> CrsTransformationResult._createCrsTransformationResult(
                inputCoordinate,
                outputCoordinateNotNull,
                // when there is an exception as below then the above coordinate SHOULD BE null !
                someException,
                false,
                super.compositeAdapterForResultTest,
                super.listOfSubresultsForStatisticsTest,
                null
            ),
            "unvalid TransformResult object construction should throw exception when an exception parameter is combined with non-null as output coordinate"
        );
    }

    @Test
    void transformResultConstruction_shouldThrowException_whenParametersExceptionIsNotNullAndSuccessIsTrue() {
        final RuntimeException someException = new RuntimeException("this is an exception");
        final CrsCoordinate outputCoordinateNull = null;
        assertThrows(
            IllegalStateException.class,
            () -> CrsTransformationResult._createCrsTransformationResult(
                inputCoordinate,
                outputCoordinateNull,
                someException,
                // when there is an exception as above then the below success SHOULD BE false !
                true,
                super.compositeAdapterForResultTest,
                super.listOfSubresultsForStatisticsTest,
                null
            ),
            "unvalid TransformResult object construction should throw exception when an exception parameter is combined with success true"
        );
    }

    
    @Test
    void transformResult_shouldThrowException_whenTryingToGetCoordinateWhenSuccessIsFalse() {
        outputCoordinate = null;
        transformResultWithSuccessFalse = CrsTransformationResult._createCrsTransformationResult(
            inputCoordinate,
            outputCoordinate,
            null,
            false,
            super.compositeAdapterForResultTest,
            super.listOfSubresultsForStatisticsTest,
            null
        );
        Exception e = assertThrows(
            RuntimeException.class,
            () -> transformResultWithSuccessFalse.getOutputCoordinate(),
            "Should not try to get output coordinate unless the result was a success"
        );
        final String exceptionMessage = e.getMessage().toLowerCase().replaceAll("-", "");
        // the purpose of the above row is to make the test less sensitive to the exact message
        // e.g. will match text containing "Pre-condition" (including hyphen and uppercased first letter)
        // The exception message should be something like "Precondition violated ..."
        assertThat(exceptionMessage, containsString("precondition"));
    }

    @Test
    void transformResult_shouldReturnStatisticsObjectWithCorrectAverageAndMeanAndMaxDiffValues_whenCreatingResultWithListOfSubresults() {

        // Both the setup code and the verify/assertion code for this test method 
        // is placed in a base class because it is reused from another test class.
        // The keyword "super" is used below to make that more obvious.

        final CrsTransformationResultStatistic nullTransformationResultStatistic = null;
        
        final CrsTransformationResult transformResult = CrsTransformationResult._createCrsTransformationResult(
            super.inputCoordinateNotUsedInStatisticsTest,
            super.outputCoordinateNotUsedInStatisticsTest,
            null,
            true,
            super.compositeAdapterForResultTest,
            super.listOfSubresultsForStatisticsTest,
            
            nullTransformationResultStatistic
            // the above parameter with the statictics object is null 
            // i.e. it is not precalculated but will become created
            // (which this test method is testing further down below)                
        );
        final CrsTransformationResultStatistic crsTransformationResultStatistic = transformResult.getCrsTransformationResultStatistic();
        
        super.assertCrsTransformationResultStatistic(crsTransformationResultStatistic);
    }

}