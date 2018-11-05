package com.programmerare.crsTransformations.compositeTransformations;

import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.CrsTransformationAdapter;
import com.programmerare.crsConstants.constantsByNumberNameArea.v9_5_4.EpsgNumber;
import com.programmerare.crsTransformations.CrsTransformationResult;
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory;
import com.programmerare.crsTransformations.utils.MedianValueUtility;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompositeStrategyForMedianValueTest extends CompositeStrategyTestBase {

    private final static double delta = 0.00001;

    @Test
    void transformWithAdapterCompositeMedianTest() {
        CrsCoordinate expectedCoordinateWithMedianLatitudeAndLongitude = calculateMedianCoordinate(super.allCoordinateResultsForTheDifferentImplementations);

        CrsTransformationAdapter medianCompositeAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian(
            allAdapters
        );
        CrsTransformationResult medianResult = medianCompositeAdapter.transform(wgs84coordinate, EpsgNumber._3006__SWEREF99_TM__SWEDEN);
        assertNotNull(medianResult);
        assertTrue(medianResult.isSuccess());
        assertEquals(super.allCoordinateResultsForTheDifferentImplementations.size(), medianResult.getTransformationResultChildren().size());

        CrsCoordinate coordinateReturnedByMedianAdapter = medianResult.getOutputCoordinate();

        // The same transformation as above has been done in the base class for the individual adapters
        assertEquals(expectedCoordinateWithMedianLatitudeAndLongitude.getXEastingLongitude(), coordinateReturnedByMedianAdapter.getXEastingLongitude(), delta);
        assertEquals(expectedCoordinateWithMedianLatitudeAndLongitude.getYNorthingLatitude(), coordinateReturnedByMedianAdapter.getYNorthingLatitude(), delta);
    }

    private CrsCoordinate calculateMedianCoordinate(List<CrsCoordinate> coordinateResultsForTheDifferentImplementations) {
        List<Double> longitudesSorted = coordinateResultsForTheDifferentImplementations.stream().map(x -> x.getXEastingLongitude()).collect(Collectors.toList());
        List<Double> latitudesSorted = coordinateResultsForTheDifferentImplementations.stream().map(x -> x.getYNorthingLatitude()).collect(Collectors.toList());
        double medianLongitude = MedianValueUtility.getMedianValue(longitudesSorted);
        double medianLatitude = MedianValueUtility.getMedianValue(latitudesSorted);
        return CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(medianLongitude, medianLatitude, EpsgNumber._3006__SWEREF99_TM__SWEDEN);
    }

}