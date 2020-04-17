// https://github.com/TomasJohansson/crsTransformations
// This content of this file is published at the above webpage
package smallJavaExample;

import com.programmerare.crsTransformations.CrsTransformationAdapter;
import com.programmerare.crsTransformations.CrsTransformationResult;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory;
import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory;

public class SmallJavaExample {
    public static void main(String[] args) {
        final int epsgWgs84  = 4326;
        final int epsgSweRef = 3006;
        // alternative to the above hardcoding: use the library "crs-transformation-constants"
        // and constants EpsgNumber.WORLD__WGS_84__4326 and EpsgNumber.SWEDEN__SWEREF99_TM__3006
        // from the class com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9.EpsgNumber

        CrsCoordinate centralStockholmWgs84 = CrsCoordinateFactory.latLon(59.330231, 18.059196, epsgWgs84);

        CrsTransformationAdapter crsTransformationAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian();
        // If the Gradle/Maven configuration includes all six adapter implementations, then the 
        // above created 'Composite' implementation will below use all six 'leaf' implementations 
        // and return a coordinate with a median longitude and a median latitude
        CrsTransformationResult centralStockholmResultSweRef = crsTransformationAdapter.transform(centralStockholmWgs84, epsgSweRef);

        if(centralStockholmResultSweRef.isSuccess()) {
            System.out.println(centralStockholmResultSweRef.getOutputCoordinate());
            // Console output from the above code row: 
            // Coordinate(xEastingLongitude=674032.3573263118, yNorthingLatitude=6580821.991123579, crsIdentifier=CrsIdentifier(crsCode='EPSG:3006', isEpsgCode=true, epsgNumber=3006))
        }
    }
}
