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

// @Deprecated("The type CrsTransformationAdapteeType was only used for getting information about which implementation was used, e.g. an enum with a name revealing the version number. Now instead use the methods getShortNameOfImplementation (existing since before) and the new method CrsTransformationAdapter.getVersionOfImplementationAdapteeLibrary()")
class CrsTransformationAdapteeTypeTest {

    // The version below should be the same as the version defined like this in "build.gradle":
    // ext.crsTransformationVersion = '1.0.1'
    private final static String VersionOfCrsTransformationLibrary = "1.1.0";
    
    @Test
    void orbisgisAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        this.verifyExpectedAdapteeVersion(
            new CrsTransformationAdapterOrbisgisCTS(),
            "1.5.2"
        );
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterOrbisgisCTS(),
            "cts-1.5.2.jar",
            CrsTransformationAdapteeType.LEAF_ORBISGIS_1_5_2,
            "1.5.2"
        );
    }
    
    @Test
    void geotoolsAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        this.verifyExpectedAdapteeVersion(
                new CrsTransformationAdapterGeoTools(),
                "21.2"
        );        
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterGeoTools(),
            "gt-main-21.2.jar",
            CrsTransformationAdapteeType.LEAF_GEOTOOLS_21_2,
            "21.2"
        );
    }

    @Test
    void geopackageNgaAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        this.verifyExpectedAdapteeVersion(
            new CrsTransformationAdapterGeoPackageNGA(),
            "3.5.0"
        );
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterGeoPackageNGA(),
            "geopackage-core-3.5.0.jar",
            CrsTransformationAdapteeType.LEAF_NGA_GEOPACKAGE_3_5_0,
            "3.5.0"
        );
    }
    
    @Test
    void proj4jAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        this.verifyExpectedAdapteeVersion(
            new CrsTransformationAdapterProj4J(),
            "0.1.0"
        );        
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterProj4J(),
            "proj4j-0.1.0.jar",
            CrsTransformationAdapteeType.LEAF_PROJ4J_0_1_0,
            "0.1.0"
        );
    }

    @Test
    void proj4jLocationtechAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        this.verifyExpectedAdapteeVersion(
            new CrsTransformationAdapterProj4jLocationtech(),
            "1.1.1"
        );
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterProj4jLocationtech(),
            "proj4j-1.1.1.jar",
            CrsTransformationAdapteeType.LEAF_PROJ4J_LOCATIONTECH_1_1_1,
            "1.1.1"
        );
    }

    @Test
    void gooberAdapter_shouldMatchExpectedEnumAndJarfileNameWithVersion() {
        this.verifyExpectedAdapteeVersion(
            new CrsTransformationAdapterGooberCTL(),
            "1.1"
        );        
        verifyExpectedEnumAndJarfileVersion(
            new CrsTransformationAdapterGooberCTL(),
            "coordinate-transformation-library-1.1.jar",
            CrsTransformationAdapteeType.LEAF_GOOBER_1_1,
            "1.1"
        );
    }

    private void verifyVersionOfCrsTransformationLibraryForComposite(
        CrsTransformationAdapterBase crsTransformationAdapter,
        CrsTransformationAdapteeType expectedAdaptee
    ) {
        verifyExpectedEnumAndJarfileVersion(
            crsTransformationAdapter,
            "",
            expectedAdaptee,
            VersionOfCrsTransformationLibrary
        );
    }
    
    private void verifyExpectedEnumAndJarfileVersion(
        CrsTransformationAdapterBase crsTransformationAdapter,
        String emptyStringOrExpectedNameOfJarFile,
        CrsTransformationAdapteeType expectedEnumWithMatchingNameInlcudingVersionNumber,
        String version
    ) {
        assertEquals(
            expectedEnumWithMatchingNameInlcudingVersionNumber, 
            crsTransformationAdapter.getAdapteeType()
        );
        this.verifyExpectedAdapteeVersion(
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
    
    @Test
    void testCompositeAverage() {
        verifyVersionOfCrsTransformationLibraryForComposite(
            CrsTransformationAdapterCompositeFactory.createCrsTransformationAverage(),
            CrsTransformationAdapteeType.COMPOSITE_AVERAGE
        );
    }

    @Test
    void testCompositeMedian() {
        verifyVersionOfCrsTransformationLibraryForComposite(
            CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian(),
            CrsTransformationAdapteeType.COMPOSITE_MEDIAN
        );
    }

    @Test
    void testCompositeFirstSuccess() {
        verifyVersionOfCrsTransformationLibraryForComposite(
            CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess(),
            CrsTransformationAdapteeType.COMPOSITE_FIRST_SUCCESS
        );
    }

    @Test
    void testCompositeWeightedAverage() {
        verifyVersionOfCrsTransformationLibraryForComposite(
            CrsTransformationAdapterCompositeFactory.createCrsTransformationWeightedAverage(
                Arrays.asList(CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterGeoPackageNGA(), 1))
            ),
            CrsTransformationAdapteeType.COMPOSITE_WEIGHTED_AVERAGE
        );
    }

}
