package java_sample;

import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9.EpsgNumber;

import com.programmerare.crsTransformations.CrsTransformationImplementationType;
import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory;
import com.programmerare.crsTransformations.CrsTransformationAdapter;
import com.programmerare.crsTransformations.CrsTransformationAdapterLeafFactory;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight;
import com.programmerare.crsTransformations.CrsTransformationResult;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory;
import static com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight.createFromInstance;
import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J;
import com.programmerare.crsTransformationAdapterProj4jLocationtech.CrsTransformationAdapterProj4jLocationtech;
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools;
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL;
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS;
import com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// cd sample_code/java
// gradlew run
// mvn compile
// mvn exec:java
public class CrsTransformation {
    public static void main(String[] args) {
        new CrsTransformation().main();
    }
    
    private void main() {
        // The below method parameters are actually fields in this sample code class but they are still passed as parameter
        // just to more it more clear to here explain the three inputs for the below transform method: 
        //  * an input coordinate (including specifying the coordinate reference system through the EPSG code 4326 (WGS84 latitudes and longitudes)
        //  * the target coordinate system for the transformation i.e. transform to the CRS with EPSG code 3006 (i.e. the Swedish CSR named "SWEREF99 TM")
        //  * a list of all the 10 different transformers that will be used for creating 10 results 
        final List<CrsTransformationResult> crsTransformationResults = transform(
            inputCoordinate,
            targetCrsIdentifier,
            allCrsTransformationAdapters
        );

        System.out.println(String.format("Implicit/default EpsgNumber for latitude/longitude: %1$s", inputCoordinate.getCrsIdentifier().getEpsgNumber())); // 4326
        System.out.println(String.format("InputCoordinate: %1$s", inputCoordinate));
        System.out.println(String.format("TargetCrsIdentifier: %1$s", targetCrsIdentifier));
        System.out.println(String.format("Coordinate transformations from EPSG %1$s to EPSG %2$s", EpsgNumber.WORLD__WGS_84__4326, EpsgNumber.SWEDEN__SWEREF99_TM__3006));
            
        // iterate the ten results and print output to the screen
        for (CrsTransformationResult result: crsTransformationResults) {
            final CrsCoordinate outputCoordinate = result.getOutputCoordinate();
            final CrsTransformationAdapter crs = result.getCrsTransformationAdapterResultSource();
            final CrsTransformationImplementationType implementationType = crs.getImplementationType();
            final String version = crs.getVersionOfImplementationAdapteeLibrary();            

            // String formatter syntax: https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax
            System.out.println(String.format("%1$s %2$s  : X / Y ===> %3$s / %4$s",  implementationType, version, outputCoordinate.getX(), outputCoordinate.getY()));
        }

        System.out.println("Short names of all Leaf implementations:");
        List<CrsTransformationAdapter> allLeafs = CrsTransformationAdapterLeafFactory.getInstancesOfAllKnownAvailableImplementations();
        for (CrsTransformationAdapter leaf : allLeafs) {
            System.out.println(leaf.getShortNameOfImplementation());
        }
        // Below is the printed output from the above code in this main method:
        //    Implicit/default EpsgNumber for latitude/longitude: 4326
        //    InputCoordinate: Coordinate(xEastingLongitude=18.059196, yNorthingLatitude=59.330231, crsIdentifier=CrsIdentifier(crsCode='EPSG:4326', isEpsgCode=true, epsgNumber=4326))
        //    TargetCrsIdentifier: CrsIdentifier(crsCode='EPSG:3006', isEpsgCode=true, epsgNumber=3006)
        //    Coordinate transformations from EPSG 4326 to EPSG 3006
        //    LEAF_PROJ4J 0.1.0  : X / Y ===> 674032.357326444 / 6580821.991123579
        //    LEAF_PROJ4J_LOCATIONTECH 1.1.1  : X / Y ===> 674032.357326444 / 6580821.991123579
        //    LEAF_NGA_GEOPACKAGE 3.5.0  : X / Y ===> 674032.357326444 / 6580821.991123579
        //    LEAF_ORBISGIS 1.5.2  : X / Y ===> 674032.3573261796 / 6580821.991121078
        //    LEAF_GOOBER 1.1  : X / Y ===> 674032.357 / 6580821.991
        //    LEAF_GEOTOOLS 23.0  : X / Y ===> 674032.3571771547 / 6580821.994371211
        //    COMPOSITE_MEDIAN 1.1.1  : X / Y ===> 674032.3573263118 / 6580821.991123579
        //    COMPOSITE_AVERAGE 1.1.1  : X / Y ===> 674032.357247111 / 6580821.991643838
        //    COMPOSITE_FIRST_SUCCESS 1.1.1  : X / Y ===> 674032.357 / 6580821.991
        //    COMPOSITE_WEIGHTED_AVERAGE 1.1.1  : X / Y ===> 674032.3572118095 / 6580821.991551861
        //    Short names of all Leaf implementations:
        //    GooberCTL
        //    GeoPackageNGA
        //    GeoTools
        //    OrbisgisCTS
        //    Proj4jLocationtech
        //    Proj4J
    }

    private List<CrsTransformationResult> transform(
        final CrsCoordinate inputCoordinate,
        final CrsIdentifier targetCrsIdentifier,
        final List<CrsTransformationAdapter> allCrsTransformationAdapters
    ) {
        return allCrsTransformationAdapters.stream().map(
            crsTransformationAdapter -> 
            crsTransformationAdapter.transform(inputCoordinate, targetCrsIdentifier)
        ).collect(Collectors.toList());
    }

    private final CrsCoordinate inputCoordinate;
    private final CrsIdentifier targetCrsIdentifier;
    private final List<CrsTransformationAdapter> allCrsTransformationAdapters;
 
    // Constructor:
    private CrsTransformation() {
        inputCoordinate = CrsCoordinateFactory.latLon(59.330231, 18.059196); //  Implicit/Default CrsIdentifier for latitude/longitude: EpsgNumber.WORLD__WGS_84__4326
        targetCrsIdentifier = CrsIdentifierFactory.createFromEpsgNumber(EpsgNumber.SWEDEN__SWEREF99_TM__3006);

        final List<CrsTransformationAdapterWeight> crsTransformationAdapterWeights = Arrays.asList(
            createFromInstance(new CrsTransformationAdapterProj4J(), 1.0),
            createFromInstance(new CrsTransformationAdapterProj4jLocationtech(), 1.0),
            createFromInstance(new CrsTransformationAdapterOrbisgisCTS(), 1.0),
            createFromInstance(new CrsTransformationAdapterGeoPackageNGA(), 1.0),
            createFromInstance(new CrsTransformationAdapterGeoTools(), 1.0),
            createFromInstance(new CrsTransformationAdapterGooberCTL(), 2.0)
        );
        final CrsTransformationAdapter weightedAverageCrsTransformationAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationWeightedAverage(
            crsTransformationAdapterWeights
        );

        allCrsTransformationAdapters = Arrays.asList(
            new CrsTransformationAdapterProj4J(),
            new CrsTransformationAdapterProj4jLocationtech(),
            new CrsTransformationAdapterGeoPackageNGA(),
            new CrsTransformationAdapterOrbisgisCTS(),
            new CrsTransformationAdapterGooberCTL(),
            new CrsTransformationAdapterGeoTools(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationAverage(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess(),
            weightedAverageCrsTransformationAdapter
        );
    }
}
