package groovy_sample

// the package below is from the Java library "crs-transformation-constants"
import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9.EpsgNumber

// the other "com.programmerare" imports below are from Kotlin libraries ( artifactId's starting with "crs-transformation-adapter-" , https://mvnrepository.com/artifact/com.programmerare.crs-transformation )
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory
import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS
import com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA

class CrsTransformation {

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
            def nameOfCrsTransformationAdapterImplementation = it.crsTransformationAdapterResultSource.adapteeType
            def outputCoordinate = it.outputCoordinate
            println "$nameOfCrsTransformationAdapterImplementation : X / Y ===> ${outputCoordinate.x} / ${outputCoordinate.y}"
        }

        // Below is the printed output from the above code in this main method: 
        //    Implicit/default EpsgNumber for latitude/longitude: 4326
        //    InputCoordinate: Coordinate(xEastingLongitude=18.059196, yNorthingLatitude=59.330231, crsIdentifier=CrsIdentifier(crsCode='EPSG:4326', isEpsgCode=true, epsgNumber=4326))
        //    TargetCrsIdentifier: CrsIdentifier(crsCode='EPSG:3006', isEpsgCode=true, epsgNumber=3006)
        //    Coordinate transformations from EPSG 4326 to EPSG 3006
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
    
    def getTargetCrsIdentifier() {
        CrsIdentifierFactory.createFromEpsgNumber EpsgNumber.SWEDEN__SWEREF99_TM__3006
    }

    def getInputCoordinate() {
        CrsCoordinateFactory.latLon 59.330231, 18.059196
    }

    private def weightedAverageCrsTransformationAdapter() {
        def crsTransformationAdapterWeights = [
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterProj4J(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterOrbisgisCTS(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(new CrsTransformationAdapterGeoPackageNGA(), 1.0),
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
            new CrsTransformationAdapterGeoPackageNGA(),
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
