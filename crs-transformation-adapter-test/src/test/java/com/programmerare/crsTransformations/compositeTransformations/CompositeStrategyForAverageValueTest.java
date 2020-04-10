package com.programmerare.crsTransformations.compositeTransformations;

import com.programmerare.crsTransformations.CrsTransformationAdapter;
import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier;
import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_5_4.EpsgNumber;
import com.programmerare.crsTransformations.CrsTransformationResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static com.programmerare.crsTransformations.CrsTransformationAdapterLeafFactoryTest.EXPECTED_NUMBER_OF_ADAPTER_LEAF_IMPLEMENTATIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompositeStrategyForAverageValueTest extends CompositeStrategyTestBase {

    private final static double delta = 0.000000001;

    @Test
    void transform_shouldReturnAverageResult_whenUsingAverageCompositeAdapter() {
        CrsCoordinate coordinateWithAverageLatitudeAndLongitude = calculateAverageCoordinate(super.allCoordinateResultsForTheDifferentImplementations);

        CrsTransformationAdapter averageCompositeAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationAverage(
            allAdapters
        );
        CrsTransformationResult averageResult = averageCompositeAdapter.transform(wgs84coordinate, EpsgNumber.SWEDEN__SWEREF99_TM__3006);
        assertNotNull(averageResult);
        assertTrue(averageResult.isSuccess());
        assertEquals(EXPECTED_NUMBER_OF_ADAPTER_LEAF_IMPLEMENTATIONS, super.allCoordinateResultsForTheDifferentImplementations.size());
        assertEquals(EXPECTED_NUMBER_OF_ADAPTER_LEAF_IMPLEMENTATIONS, averageResult.getTransformationResultChildren().size());

        CrsCoordinate coordinateReturnedByCompositeAdapter = averageResult.getOutputCoordinate();

        assertEquals(coordinateWithAverageLatitudeAndLongitude.getXEastingLongitude(), coordinateReturnedByCompositeAdapter.getXEastingLongitude(), delta);
        assertEquals(coordinateWithAverageLatitudeAndLongitude.getYNorthingLatitude(), coordinateReturnedByCompositeAdapter.getYNorthingLatitude(), delta);
        // assertEquals(coordinateWithAverageLatitudeAndLongitude, coordinateReturnedByCompositeAdapter);
        // Expected :Coordinate(xEastingLongitude=674032.3572074446, yNorthingLatitude=6580821.991903967, crsIdentifier=CrsIdentifier(crsCode=EPSG:3006, isEpsgCode=true, epsgNumber=3006))
        // Actual   :Coordinate(xEastingLongitude=674032.3572074447, yNorthingLatitude=6580821.991903967, crsIdentifier=CrsIdentifier(crsCode=EPSG:3006, isEpsgCode=true, epsgNumber=3006))
    }

    private double getAverage(List<CrsCoordinate> resultCoordinates, ToDoubleFunction<? super CrsCoordinate> mapperReturningDoubleValueForAverageCalculation) {
        return resultCoordinates.stream().mapToDouble(mapperReturningDoubleValueForAverageCalculation).average().getAsDouble();
    }

    private CrsCoordinate calculateAverageCoordinate(List<CrsCoordinate> resultCoordinates) {
        double averageLat = getAverage(resultCoordinates, c -> c.getYNorthingLatitude());
        double averageLon = getAverage(resultCoordinates, c -> c.getXEastingLongitude());
        Set<CrsIdentifier> set = resultCoordinates.stream().map(c -> c.getCrsIdentifier()).collect(Collectors.toSet());
        assertEquals(1, set.size(), "all coordinates should have the same CRS, since thet should all be the result of a transform to the same CRS");
        return CrsCoordinateFactory.createFromYNorthingLatitudeAndXEastingLongitude(averageLat, averageLon, resultCoordinates.get(0).getCrsIdentifier());
    }
}
