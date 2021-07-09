package com.programmerare.crsTransformations;

import com.programmerare.crsTransformationAdapterNgaGeoInt.CrsTransformationAdapterNgaGeoInt;
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools;
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL;
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS;
import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J;
import com.programmerare.crsTransformationAdapterProj4jLocationtech.CrsTransformationAdapterProj4jLocationtech;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CrsTransformationAdapteeImplementationTypeAndVersionTest
{
    // The version below should be the same as the version defined like this in "build.gradle":
    // ext.crsTransformationVersion = '1.1.1'
    // upgrade from 1.1.1 ==> 2.0.0 (breaking change: removed the enum CrsTransformationAdapteeType, deprecated since 1.1.0)  
    private final static String VersionOfCrsTransformationLibrary = "2.0.0";
    
    // Note that the names of CrsTransformationImplementationType and the deprecated CrsTransformationAdapteeType
    // are almost the same (e.g. "LEAF_ORBISGIS" vs "LEAF_ORBISGIS_1_5_2"), but for the Leaf's the 
    // version number is included in the deprecated enum.
    // And as long as the old enums are used, i.e. only deprecated and not removed, they should keep being tested.
    
    @Test
    void orbisgisAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterOrbisgisCTS(),
            CrsTransformationImplementationType.LEAF_ORBISGIS,
            "1.5.2" // "cts-1.5.2.jar"
        );
    }
    
    @Test
    void geotoolsAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterGeoTools(),
            CrsTransformationImplementationType.LEAF_GEOTOOLS,
            "25.1" // "gt-main-25.1.jar"
        );
    }

    @Test
    void geopackageNgaAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterNgaGeoInt(),
            CrsTransformationImplementationType.LEAF_NGA_GEOPACKAGE,
            "5.0.0" // "geopackage-core-5.0.0.jar"
        );
    }
    
    @Test
    void proj4jAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterProj4J(),
            CrsTransformationImplementationType.LEAF_PROJ4J,
            "0.1.0" // "proj4j-0.1.0.jar"
        );
    }

    @Test
    void proj4jLocationtechAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterProj4jLocationtech(),
            CrsTransformationImplementationType.LEAF_PROJ4J_LOCATIONTECH,
            "1.1.3" // "proj4j-1.1.3.jar"
        );
    }

    @Test
    void gooberAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterGooberCTL(),
            CrsTransformationImplementationType.LEAF_GOOBER,
            "1.1" // "coordinate-transformation-library-1.1.jar"
        );
    }


    // regarding the below "duplications" of e.g. "COMPOSITE_AVERAGE" see comment above before all the test methods in this class

    @Test
    void testCompositeAverage() {
        verifyVersionOfCrsTransformationLibraryForComposite(
            CrsTransformationAdapterCompositeFactory.createCrsTransformationAverage(),
            CrsTransformationImplementationType.COMPOSITE_AVERAGE
        );
    }

    @Test
    void testCompositeMedian() {
        verifyVersionOfCrsTransformationLibraryForComposite(
            CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian(),
            CrsTransformationImplementationType.COMPOSITE_MEDIAN
        );
    }

    @Test
    void testCompositeFirstSuccess() {
        verifyVersionOfCrsTransformationLibraryForComposite(
            CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess(),
            CrsTransformationImplementationType.COMPOSITE_FIRST_SUCCESS
        );
    }

    @Test
    void testCompositeWeightedAverage() {
        verifyVersionOfCrsTransformationLibraryForComposite(
            CrsTransformationAdapterCompositeFactory.createCrsTransformationWeightedAverage(
                Arrays.asList(CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterNgaGeoInt(), 1))
            ),
            CrsTransformationImplementationType.COMPOSITE_WEIGHTED_AVERAGE
        );
    }
    
    private void verifyVersionOfCrsTransformationLibraryForComposite(
        CrsTransformationAdapterBase crsTransformationAdapter,
        CrsTransformationImplementationType expectedCrsTransformationImplementationType 
    ) {
        verifyExpectedEnumAndJarfileVersion(
            crsTransformationAdapter,
            expectedCrsTransformationImplementationType,
            VersionOfCrsTransformationLibrary
        );
    }
    
    private void verifyExpectedEnumAndJarfileVersion(
        CrsTransformationAdapterBase crsTransformationAdapter,
        CrsTransformationImplementationType expectedCrsTransformationImplementationType,
        String version
    ) {
        assertEquals(
            expectedCrsTransformationImplementationType,
            crsTransformationAdapter.getImplementationType()
        );        
        verifyExpectedAdapteeVersion(
            crsTransformationAdapter,
            version
        );
    }

    private void verifyExpectedAdapteeVersion(
        CrsTransformationAdapterBase crsTransformationAdapter,
        String expectedVersion
    ) {
        assertEquals(
            expectedVersion,
            crsTransformationAdapter.getVersionOfImplementationAdapteeLibrary()
        );
    }

}
