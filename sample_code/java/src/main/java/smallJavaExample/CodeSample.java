// https://github.com/TomasJohansson/crsTransformations
// Some of the content of this file is published at the above webpage
// and the purpose of this file below is to verify for sure that is compiles 
// correctly before it is pasted into the above page

package smallJavaExample;

import static com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory.*;
import com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA;
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools;
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL;
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS;
import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J;
import com.programmerare.crsTransformationAdapterProj4jLocationtech.CrsTransformationAdapterProj4jLocationtech;
import com.programmerare.crsTransformations.CrsTransformationAdapter;
import com.programmerare.crsTransformations.CrsTransformationResult;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight;
import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory;

import java.util.Arrays;

public class CodeSample
{
    void sample1() {
        // The interface with ten implementations as illustrated below
        CrsTransformationAdapter crsTransformationAdapter;
        // The interface is defined in the library "crs-transformation-adapter-core" with this full name:
        // com.programmerare.crsTransformations.CrsTransformationAdapter        

        // The six 'Leaf' implementations:

        // Library "crs-transformation-adapter-impl-proj4j", class:
        // com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J;
        crsTransformationAdapter = new CrsTransformationAdapterProj4J();

        // Library "crs-transformation-adapter-impl-proj4jlocationtech", class:
        // com.programmerare.crsTransformationAdapterProj4jLocationtech.CrsTransformationAdapterProj4jLocationtech;
        crsTransformationAdapter = new CrsTransformationAdapterProj4jLocationtech();
        
        // Library "crs-transformation-adapter-impl-orbisgis", class:
        // com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS;        
        crsTransformationAdapter = new CrsTransformationAdapterOrbisgisCTS();

        // Library "crs-transformation-adapter-impl-nga", class:
        // com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA;
        crsTransformationAdapter = new CrsTransformationAdapterGeoPackageNGA();

        // Library "crs-transformation-adapter-impl-geotools", class:
        // com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools;        
        crsTransformationAdapter = new CrsTransformationAdapterGeoTools();

        // Library "crs-transformation-adapter-impl-goober", class:
        // com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL;        
        crsTransformationAdapter = new CrsTransformationAdapterGooberCTL();
        // - - - - - - - - - - - -

        // The four 'Composite' implementations below are all located in the library
        // "crs-transformation-adapter-core" and the factory class is:
        // com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory

        crsTransformationAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian();

        crsTransformationAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationAverage();

        crsTransformationAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess();

        // All of the above three factory methods without any parameter will try to use as many of the six 'leaf' 
        // implementations as are available at the class path (e.g. are included as dependencies with Gradle or Maven).
        // All above three factory methods are also overloaded with methods taking 
        // a parameter 'List<CrsTransformationAdapter>' if you prefer to explicit define which 'leafs' to use.

        // The fourth 'Composite' below does not have any overloaded method without parameter 
        // but if you want to use a result created as a weighted average then the weights need 
        // to be specified per leaf implementation as in the example below.

        crsTransformationAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationWeightedAverage(Arrays.asList(
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterProj4J(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterProj4jLocationtech(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterOrbisgisCTS(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterGeoPackageNGA(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterGeoTools(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterGooberCTL(), 2.0)
        ));
        // The weight values above illustrates a situation where you (for some reason) want to consider 
        // the transformation result from 'goober' as being 'two times better' than the others.
    }

    void sample2() {
        final int epsgNumber = 4326;
        final String crsCode = "EPSG:" + epsgNumber;
        CrsIdentifier crsIdentifier; // package com.programmerare.crsTransformations.crsIdentifier
        crsIdentifier = CrsIdentifierFactory.createFromEpsgNumber(epsgNumber);
        // Alternative:
        crsIdentifier = CrsIdentifierFactory.createFromCrsCode(crsCode);

        final double latitude = 59.330231;
        final double longitude = 18.059196;

        CrsCoordinate crsCoordinate; // package com.programmerare.crsTransformations.coordinate
        // All the below methods are alternatives for creating the same coordinate 
        // with the above latitude/longitude and coordinate reference system.
        // No class or object is used for the methods below because of the following static import:
        // import static com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory.*;
        crsCoordinate = latLon(latitude, longitude, epsgNumber);
        crsCoordinate = latLon(latitude, longitude, crsCode);
        crsCoordinate = latLon(latitude, longitude, crsIdentifier);

        crsCoordinate = lonLat(longitude, latitude, epsgNumber);
        crsCoordinate = lonLat(longitude, latitude, crsCode);
        crsCoordinate = lonLat(longitude, latitude, crsIdentifier);

        crsCoordinate = yx(latitude, longitude, epsgNumber);
        crsCoordinate = yx(latitude, longitude, crsCode);
        crsCoordinate = yx(latitude, longitude, crsIdentifier);

        crsCoordinate = xy(longitude, latitude, epsgNumber);
        crsCoordinate = xy(longitude, latitude, crsCode);
        crsCoordinate = xy(longitude, latitude, crsIdentifier);

        crsCoordinate = northingEasting(latitude, longitude, epsgNumber);
        crsCoordinate = northingEasting(latitude, longitude, crsCode);
        crsCoordinate = northingEasting(latitude, longitude, crsIdentifier);

        crsCoordinate = eastingNorthing(longitude, latitude, epsgNumber);
        crsCoordinate = eastingNorthing(longitude, latitude, crsCode);
        crsCoordinate = eastingNorthing(longitude, latitude, crsIdentifier);

        crsCoordinate = createFromYNorthingLatitudeAndXEastingLongitude(latitude, longitude, epsgNumber);
        crsCoordinate = createFromYNorthingLatitudeAndXEastingLongitude(latitude, longitude, crsCode);
        crsCoordinate = createFromYNorthingLatitudeAndXEastingLongitude(latitude, longitude, crsIdentifier);

        crsCoordinate = createFromXEastingLongitudeAndYNorthingLatitude(longitude, latitude, epsgNumber);
        crsCoordinate = createFromXEastingLongitudeAndYNorthingLatitude(longitude, latitude, crsCode);
        crsCoordinate = createFromXEastingLongitudeAndYNorthingLatitude(longitude, latitude, crsIdentifier);


        CrsIdentifier targetCrs = CrsIdentifierFactory.createFromEpsgNumber(3006);
        
        // Below is one (of ten) implementation of CrsTransformationAdapter created (see further up at this page for the others)  
        CrsTransformationAdapter crsTransformationAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian();
        
        CrsTransformationResult crsTransformationResult = crsTransformationAdapter.transform(crsCoordinate, targetCrs);
        // see also more example code further down in this webpage
    }
}
