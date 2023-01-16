package com.programmerare.crsTransformations.compositeTransformations;

import com.programmerare.crsConstants.constantsByAreaNameNumber.v10_027.EpsgNumber;
import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.CrsTransformationAdapter;
import com.programmerare.crsTransformations.CrsTransformationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CompositeStrategyForFirstSuccessTest extends CompositeStrategyTestBase {

    private CrsTransformationAdapterComposite firstSuccessCompositeAdapterWithGooberLocationtechOrbisgis;
    @BeforeEach
    void beforeEach() {
        firstSuccessCompositeAdapterWithGooberLocationtechOrbisgis = CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess(
            Arrays.asList(
                adapterGooberCTL,
                adapterProj4jLocationtech,
                adapterOrbisgisCTS
            )
        );        
    } 
    
    @Test
    void transform_shouldReturn_Goober_Result_whenUsingFirstSuccessCompositeAdapter() {
        // the below used composite adapter aggregates the following three adapters
        // and (IMPORTANT) in this specific order:
        // adapterGooberCTL , adapterProj4jLocationtech , adapterOrbisgisCTS
        transform_shouldReturnFirstResult_whenUsingFirstSuccessCompositeAdapter_helper(
            firstSuccessCompositeAdapterWithGooberLocationtechOrbisgis,
            EpsgNumber.SWEDEN__SWEREF99_TM__3006, // epsgNumberForTransformTarget
            1, // expectedNumberOfResults
            adapterGooberCTL // expectedLeafAdapterWithTheSuccessfulResult
        );
    }

    @Test
    void transform_shouldReturn_Proj4jLocationtech_Result_whenUsingFirstSuccessCompositeAdapter() {
        // the below used composite adapter aggregates the following three adapters
        // and (IMPORTANT) in this specific order:
        // adapterGooberCTL , adapterProj4jLocationtech , adapterOrbisgisCTS        
        transform_shouldReturnFirstResult_whenUsingFirstSuccessCompositeAdapter_helper(
            firstSuccessCompositeAdapterWithGooberLocationtechOrbisgis,
            EpsgNumber.WORLD__85_S_TO_85_N__WGS_84__PSEUDO_MERCATOR__3857, // epsgNumberForTransformTarget
            // 'Goober' implementation only supports EPSG 4326 and EPSG 3006-3024 i.e. 
            // does NOT support EPSG 3857, but 'Proj4jLocationtech' supports
            // that transformation and therefore the expected number of results is 2
            // (and note that 'result' does NOT mean successful result, but how many 'results' are aggregated in the composite)
            2, // expectedNumberOfResults
            adapterProj4jLocationtech // expectedLeafAdapterWithTheSuccessfulResult
        );
    }

    @Test
    void transform_shouldReturn_No_Result_whenUsingFirstSuccessCompositeAdapter() {
        transform_shouldReturnFirstResult_whenUsingFirstSuccessCompositeAdapter_helper(
            firstSuccessCompositeAdapterWithGooberLocationtechOrbisgis,
            999999999, // epsgNumberForTransformTarget
            // the above non-existing EPSG is of course not supported by any implementation 
            3, // expectedNumberOfResults (all 3 results will be calculated but note that 'result' does NOT mean successful result)
            null // expectedLeafAdapterWithTheSuccessfulResult
        );
    }    
    private void transform_shouldReturnFirstResult_whenUsingFirstSuccessCompositeAdapter_helper(
        CrsTransformationAdapterComposite firstSuccessCompositeAdapter,
        int epsgNumberForTransformTarget,
        int expectedNumberOfResults,
        CrsTransformationAdapter expectedLeafAdapterWithTheSuccessfulResult
    ) {
        final CrsTransformationResult firstSuccessResult = firstSuccessCompositeAdapter.transform(wgs84coordinate, epsgNumberForTransformTarget);
        assertNotNull(firstSuccessResult);
        final List<CrsTransformationResult> children = firstSuccessResult.getTransformationResultChildren();
        assertEquals(expectedNumberOfResults, children.size());
        
        final boolean expectedSuccess = expectedLeafAdapterWithTheSuccessfulResult != null;
        assertEquals(
            expectedSuccess,
            firstSuccessResult.isSuccess()
        );
        if(!expectedSuccess) {
            return;
            // since the code further down below is not applicable when no successful result was expected
        }
        
        // the last should have the successful result (for this tested 'CompositeStrategyForFirstSuccess')
        final CrsTransformationResult leafResultWithSuccess = children.get(children.size() - 1);        
        
        assertEquals(
            expectedLeafAdapterWithTheSuccessfulResult.getImplementationType(),
            leafResultWithSuccess.getCrsTransformationAdapterResultSource().getImplementationType()
        );
        
        CrsCoordinate coordinateReturnedByCompositeAdapterFirstSuccess = firstSuccessResult.getOutputCoordinate();
        CrsCoordinate coordinateResultWhenUsingTheExpectedLeafAdapter = expectedLeafAdapterWithTheSuccessfulResult.transformToCoordinate(wgs84coordinate, epsgNumberForTransformTarget);
        assertEquals(
            coordinateResultWhenUsingTheExpectedLeafAdapter,
            coordinateReturnedByCompositeAdapterFirstSuccess
        );
    }

    @Test
    void transform_shouldReturnFirstResult_whenUsingFirstSuccessCompositeAdapter() {
        CrsTransformationAdapterComposite firstSuccessCompositeAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess(
            // note that geotools should be the first item in the below list defined in the baseclass,
            // and therefore geotools should be the implementation providing the result
            super.allAdapters
        );
        // The result of the composite should be equal to the result of GeoTools since it
        // is first in the list of parameters to the constructor and it should produce a result for
        // the input coordinates ... so therefore below the 'adapterGeoTools' is set to the adapter expected to provide the result

        transform_shouldReturnFirstResult_whenUsingFirstSuccessCompositeAdapter_helper(
            firstSuccessCompositeAdapter,
            EpsgNumber.SWEDEN__SWEREF99_TM__3006, // epsgNumberForTransformTarget
            1, // expectedNumberOfResults
            adapterGeoTools // expectedLeafAdapterWithTheSuccessfulResult
        );
    }
}
