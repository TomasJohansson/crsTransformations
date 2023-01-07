package groovy_sample

// the package below is from the Java library "crs-transformation-constants"
import com.programmerare.crsConstants.constantsByAreaNameNumber.v10_027.EpsgNumber

// the other "com.programmerare" imports below are from Kotlin libraries ( artifactId's starting with "crs-transformation-adapter-" , https://mvnrepository.com/artifact/com.programmerare.crs-transformation )
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory
import com.programmerare.crsTransformations.CrsTransformationAdapterLeafFactory
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory
import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J
import com.programmerare.crsTransformationAdapterProj4jLocationtech.CrsTransformationAdapterProj4jLocationtech
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS
import com.programmerare.crsTransformationAdapterNgaGeoInt.CrsTransformationAdapterNgaGeoInt

class CrsTransformation {

    // cd sample_code/groovy
    // gradlew run    
    static void main(String[] args) {
        new CrsTransformation().mainInstanceMethod()
    }
    
    private void mainInstanceMethod() {
        println "Implicit/default EpsgNumber for latitude/longitude: ${inputCoordinate.crsIdentifier.epsgNumber}" // 4326
        println "InputCoordinate: ${inputCoordinate}"
        println "TargetCrsIdentifier: ${targetCrsIdentifier}"
        println "Coordinate transformations from EPSG ${EpsgNumber.WORLD__WGS_84__4326} to EPSG ${EpsgNumber.SWEDEN__SWEREF99_TM__3006}"

        def crsTransformationResults = transform inputCoordinate, targetCrsIdentifier, allCrsTransformationAdapters
        crsTransformationResults.each { 
            def crs = it.crsTransformationAdapterResultSource
            def outputCoordinate = it.outputCoordinate
            println "${crs.implementationType} ${crs.versionOfImplementationAdapteeLibrary}  : X / Y ===> ${outputCoordinate.x} / ${outputCoordinate.y}"
        }

        println "Short names of all Leaf implementations:"
        def allLeafs = CrsTransformationAdapterLeafFactory.getInstancesOfAllKnownAvailableImplementations()
        allLeafs.each {
            println it.shortNameOfImplementation
        }

        // Below is the printed output from the above code in this main method: 
        //    Implicit/default EpsgNumber for latitude/longitude: 4326
        //    InputCoordinate: Coordinate(xEastingLongitude=18.059196, yNorthingLatitude=59.330231, crsIdentifier=CrsIdentifier(crsCode='EPSG:4326', isEpsgCode=true, epsgNumber=4326))
        //    TargetCrsIdentifier: CrsIdentifier(crsCode='EPSG:3006', isEpsgCode=true, epsgNumber=3006)
        //    Coordinate transformations from EPSG 4326 to EPSG 3006
        //    LEAF_PROJ4J 0.1.0  : X / Y ===> 674032.357326444 / 6580821.991123579
        //    LEAF_PROJ4J_LOCATIONTECH 1.2.2  : X / Y ===> 674032.3573261689 / 6580821.991120384
        //    LEAF_NGA_GEOINT 4.3.0  : X / Y ===> 674032.3573261689 / 6580821.991120384
        //    LEAF_ORBISGIS 1.6.0  : X / Y ===> 674032.3573261796 / 6580821.991121078
        //    LEAF_GOOBER 1.1  : X / Y ===> 674032.357 / 6580821.991
        //    LEAF_GEOTOOLS 28.0  : X / Y ===> 674032.3571771549 / 6580821.994371211
        //    COMPOSITE_MEDIAN 2.0.0  : X / Y ===> 674032.3573261689 / 6580821.991120731
        //    COMPOSITE_AVERAGE 2.0.0  : X / Y ===> 674032.3572470193 / 6580821.991642772
        //    COMPOSITE_FIRST_SUCCESS 2.0.0  : X / Y ===> 674032.357 / 6580821.991
        //    COMPOSITE_WEIGHTED_AVERAGE 2.0.0  : X / Y ===> 674032.3572117308 / 6580821.991550948
        //    Short names of all Leaf implementations:
        //    GooberCTL
        //    NgaGeoInt
        //    GeoTools
        //    OrbisgisCTS
        //    Proj4jLocationtech
        //    Proj4J
    }
    
    def getTargetCrsIdentifier() {
        CrsIdentifierFactory.createFromEpsgNumber EpsgNumber.SWEDEN__SWEREF99_TM__3006
    }

    def getInputCoordinate() {
        CrsCoordinateFactory.latLon 59.330231, 18.059196
    }

    private def weightedAverageCrsTransformationAdapter() {
        def crsTransformationAdapterWeights = [
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterProj4J(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterProj4jLocationtech(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterOrbisgisCTS(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterNgaGeoInt(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterGeoTools(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterGooberCTL(), 2.0)
        ]
        CrsTransformationAdapterCompositeFactory.createCrsTransformationWeightedAverage(
            crsTransformationAdapterWeights
        )
    }    

    def getAllCrsTransformationAdapters() {
        [
            new CrsTransformationAdapterProj4J(),
            new CrsTransformationAdapterProj4jLocationtech(),
            new CrsTransformationAdapterNgaGeoInt(),
            new CrsTransformationAdapterOrbisgisCTS(),
            new CrsTransformationAdapterGooberCTL(),
            new CrsTransformationAdapterGeoTools(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationAverage(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess(),
            weightedAverageCrsTransformationAdapter()
        ]
    }

    def transform(
        inputCoordinate, // CrsCoordinate,
        targetCrsIdentifier, // CrsIdentifier,
        allCrsTransformationAdapters // List<CrsTransformationAdapter>
    ) { // return type: List<CrsTransformationResult>
        allCrsTransformationAdapters.collect { // "it" type: CrsTransformationAdapter
            it.transform(inputCoordinate, targetCrsIdentifier)
        }
    }
}
