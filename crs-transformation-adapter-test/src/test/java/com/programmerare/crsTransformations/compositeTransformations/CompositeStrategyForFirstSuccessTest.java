package com.programmerare.crsTransformations.compositeTransformations;

import com.programmerare.crsConstants.constantsByAreaNameNumber.v10_027.EpsgNumber;
import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.CrsTransformationAdapter;
import com.programmerare.crsTransformations.CrsTransformationResult;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompositeStrategyForFirstSuccessTest extends CompositeStrategyTestBase {

    @Test
    void transform_shouldReturnFirstResult_whenUsingFirstSuccessCompositeAdapter() {
        CrsTransformationAdapterComposite firstSuccessCompositeAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess(
            // note that geotools should be the first item in the below list defined in the baseclass,
            // and therefore geotools should be the implementation providing the result
            super.allAdapters
        );
        // The result of the composite should be equal to the result of GeoTools since it
        // is first in the list of parameters to the constructor and it should produce a result for
        // the input coordinates ... so therefore below it is set to the adapter expected to provide the result

        transform_shouldReturnFirstResult_whenUsingFirstSuccessCompositeAdapter_helper(
            firstSuccessCompositeAdapter,
            EpsgNumber.SWEDEN__SWEREF99_TM__3006, // epsgNumberForTransformTarget
            1, // expectedNumberOfResults
            adapterGeoTools // expectedLeafAdapterForTheResult
        );
    }

    private void transform_shouldReturnFirstResult_whenUsingFirstSuccessCompositeAdapter_helper(
        CrsTransformationAdapterComposite firstSuccessCompositeAdapter,
        int epsgNumberForTransformTarget,
        int expectedNumberOfResults,
        CrsTransformationAdapter expectedLeafAdapterWithTheResult
    ) {
        final CrsTransformationResult firstSuccessResult = firstSuccessCompositeAdapter.transform(wgs84coordinate, epsgNumberForTransformTarget);
        assertNotNull(firstSuccessResult);
        assertTrue(firstSuccessResult.isSuccess());
        assertEquals(expectedNumberOfResults, firstSuccessResult.getTransformationResultChildren().size());

        final List<CrsTransformationResult> children = firstSuccessResult.getTransformationResultChildren();
        // the last should have the result (for this tested 'CompositeStrategyForFirstSuccess')
        final CrsTransformationResult leafResult = children.get(children.size() - 1);
            
        assertEquals(
            expectedLeafAdapterWithTheResult.getImplementationType(),
            leafResult.getCrsTransformationAdapterResultSource().getImplementationType()
        );
        
        CrsCoordinate coordinateReturnedByCompositeAdapterFirstSuccess = firstSuccessResult.getOutputCoordinate();
        CrsCoordinate coordinateResultWhenUsingTheExpectedLeafAdapter = expectedLeafAdapterWithTheResult.transformToCoordinate(wgs84coordinate, epsgNumberForTransformTarget);
        assertEquals(
            coordinateResultWhenUsingTheExpectedLeafAdapter,
            coordinateReturnedByCompositeAdapterFirstSuccess
        );
    }
    
}
