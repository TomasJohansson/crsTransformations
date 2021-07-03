package com.programmerare.crsTransformations;

import static org.hamcrest.CoreMatchers.endsWith;
import com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA;
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
            "cts-1.5.2.jar",
            CrsTransformationImplementationType.LEAF_ORBISGIS,
            "1.5.2"
        );
    }
    
    @Test
    void geotoolsAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterGeoTools(),
            "gt-main-23.2.jar",
            CrsTransformationImplementationType.LEAF_GEOTOOLS,
            "23.2"
        );
    }

    @Test
    void geopackageNgaAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterGeoPackageNGA(),
            "geopackage-core-5.0.0.jar",
            CrsTransformationImplementationType.LEAF_NGA_GEOPACKAGE,
            "5.0.0"
        );
    }
    
    @Test
    void proj4jAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterProj4J(),
            "proj4j-0.1.0.jar",
            CrsTransformationImplementationType.LEAF_PROJ4J,
            "0.1.0"
        );
    }

    @Test
    void proj4jLocationtechAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterProj4jLocationtech(),
            "proj4j-1.1.3.jar",
            CrsTransformationImplementationType.LEAF_PROJ4J_LOCATIONTECH,
            "1.1.3"
        );
    }

    @Test
    void gooberAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterGooberCTL(),
            "coordinate-transformation-library-1.1.jar",
            CrsTransformationImplementationType.LEAF_GOOBER,
            "1.1"
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
                Arrays.asList(CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterGeoPackageNGA(), 1))
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
            "",
            expectedCrsTransformationImplementationType,
            VersionOfCrsTransformationLibrary
        );
    }
    
    private void verifyExpectedEnumAndJarfileVersion(
        CrsTransformationAdapterBase crsTransformationAdapter,
        String emptyStringOrExpectedNameOfJarFile,
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
        
        String fileNameIncludingPath = crsTransformationAdapter.getNameOfJarFileOrEmptyString();
        if(!emptyStringOrExpectedNameOfJarFile.equals("")) {
            assertThat(
                "Likely failure reason: You have upgraded a version. If so, then upgrade both the enum value and the filename",
                fileNameIncludingPath, endsWith(emptyStringOrExpectedNameOfJarFile)
            );
        }
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
