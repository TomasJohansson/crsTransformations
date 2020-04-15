package java_sample;

import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9.EpsgNumber;
import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory;
import com.programmerare.crsTransformations.CrsTransformationAdapter;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight;
import com.programmerare.crsTransformations.CrsTransformationResult;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory;
import static com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight.createFromInstance;
import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J;
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools;
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL;
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS;
import com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CrsTransformation {
    public static void main(String[] args) {
        new CrsTransformation().main();
    }
    
    private void main() {
        // The below method parameters are actually fields in this sample code class but they are still passed as parameter
        // just to more it more clear to here explain the three inputs for the below transform method: 
        //  * an input coordinate (including specifying the coordinate reference system through the EPSG code 4326 (WGS84 latitudes and longitudes)
        //  * the target coordinate system for the transformation i.e. transform to the CRS with EPSG code 3006 (i.e. the Swedish CSR named "SWEREF99 TM")
        //  * a list of all the 9 different transformers that will be used for creating 9 results 
        final List<CrsTransformationResult> crsTransformationResults = transform(
            inputCoordinate,
            targetCrsIdentifier,
            allCrsTransformationAdapters
        );

        // iterate the nine results and print output to the screen
        for (CrsTransformationResult result: crsTransformationResults) {
            final String nameOfCrsTransformationAdapterImplementation = result.getCrsTransformationAdapterResultSource().getAdapteeType().toString();
            final CrsCoordinate outputCoordinate = result.getOutputCoordinate();
            
            // String formatter syntax: https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax
            System.out.println(String.format("%1$s : X / Y ===> %2$s / %3$s",  nameOfCrsTransformationAdapterImplementation, outputCoordinate.getX(), outputCoordinate.getY()));
        }
        // Below is the printed output from the above code in this main method:
        //    LEAF_PROJ4J_0_1_0 : X / Y ===> 674032.357326444 / 6580821.991123579
        //    LEAF_NGA_GEOPACKAGE_3_1_0 : X / Y ===> 674032.357326444 / 6580821.991123579
        //    LEAF_ORBISGIS_1_5_1 : X / Y ===> 674032.3573261796 / 6580821.991121078
        //    LEAF_GOOBER_1_1 : X / Y ===> 674032.357 / 6580821.991
        //    LEAF_GEOTOOLS_20_0 : X / Y ===> 674032.3571771547 / 6580821.994371211
        //    COMPOSITE_MEDIAN : X / Y ===> 674032.3573261796 / 6580821.991123579
        //    COMPOSITE_AVERAGE : X / Y ===> 674032.3572312444 / 6580821.991747889
        //    COMPOSITE_FIRST_SUCCESS : X / Y ===> 674032.357 / 6580821.991
        //    COMPOSITE_WEIGHTED_AVERAGE : X / Y ===> 674032.3571927036 / 6580821.991623241
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
