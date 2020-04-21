package kotlin_sample

import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9.EpsgNumber
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.coordinate.latLon
import com.programmerare.crsTransformations.crsIdentifier.createFromEpsgNumber
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier
import com.programmerare.crsTransformations.CrsTransformationAdapter
import com.programmerare.crsTransformations.CrsTransformationResult
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight
import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J
import com.programmerare.crsTransformationAdapterProj4jLocationtech.CrsTransformationAdapterProj4jLocationtech;
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS
import com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA
import com.programmerare.crsTransformations.CrsTransformationAdapterLeafFactory
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight.Companion.createFromInstance

class CrsTransformation {

    // gradlew run
    fun main() {
        println("Implicit/default EpsgNumber for latitude/longitude: ${inputCoordinate.crsIdentifier.epsgNumber}") // 4326
        println("InputCoordinate: $inputCoordinate")
        println("TargetCrsIdentifier: $targetCrsIdentifier")
        println("Coordinate transformations from EPSG ${EpsgNumber.WORLD__WGS_84__4326} to EPSG ${EpsgNumber.SWEDEN__SWEREF99_TM__3006}")

        val crsTransformationResults = transform(inputCoordinate, targetCrsIdentifier, allCrsTransformationAdapters)

        crsTransformationResults.forEach { result ->
            val outputCoordinate = result.outputCoordinate
            val crs = result.crsTransformationAdapterResultSource
            val implementationType = crs.getImplementationType()
            val version = crs.getVersionOfImplementationAdapteeLibrary()
            println("$implementationType $version  : X / Y ===> ${outputCoordinate.getX()} / ${outputCoordinate.getY()}")
        }

        println("Short names of all Leaf implementations:")
        val allLeafs = CrsTransformationAdapterLeafFactory.getInstancesOfAllKnownAvailableImplementations()
        allLeafs.forEach { leaf ->
            println(leaf.getShortNameOfImplementation())
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

    private fun transform(
        inputCoordinate: CrsCoordinate,
        targetCrsIdentifier: CrsIdentifier,
        allCrsTransformationAdapters: List<CrsTransformationAdapter>
    ): List<CrsTransformationResult> {
        return allCrsTransformationAdapters.map { crsTransformationAdapter ->
            crsTransformationAdapter.transform(inputCoordinate, targetCrsIdentifier)
        }
    }
    
    private val inputCoordinate: CrsCoordinate by lazy {
        latLon(59.330231, 18.059196) //  Implicit/Default CrsIdentifier for latitude/longitude: EpsgNumber.WORLD__WGS_84__4326    
    }

    private val targetCrsIdentifier: CrsIdentifier by lazy {
        createFromEpsgNumber(EpsgNumber.SWEDEN__SWEREF99_TM__3006)    
    }

    private val weightedAverageCrsTransformationAdapter: CrsTransformationAdapter by lazy {
        val crsTransformationAdapterWeights = listOf<CrsTransformationAdapterWeight>(
            createFromInstance(CrsTransformationAdapterProj4J(), 1.0),
            createFromInstance(CrsTransformationAdapterProj4jLocationtech(), 1.0),
            createFromInstance(CrsTransformationAdapterOrbisgisCTS(), 1.0),
            createFromInstance(CrsTransformationAdapterGeoPackageNGA(), 1.0),
            createFromInstance(CrsTransformationAdapterGeoTools(), 1.0),
            createFromInstance(CrsTransformationAdapterGooberCTL(), 2.0)
        )
        CrsTransformationAdapterCompositeFactory.createCrsTransformationWeightedAverage(
            crsTransformationAdapterWeights
        )
    }

    private val allCrsTransformationAdapters: List<CrsTransformationAdapter> by lazy {
        listOf(
            CrsTransformationAdapterProj4J(),
            CrsTransformationAdapterProj4jLocationtech(),
            CrsTransformationAdapterGeoPackageNGA(),
            CrsTransformationAdapterOrbisgisCTS(),
            CrsTransformationAdapterGooberCTL(),
            CrsTransformationAdapterGeoTools(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationAverage(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess(),
            weightedAverageCrsTransformationAdapter
        )        
    }
}

// cd sample_code/kotlin
// gradlew run
fun main(args: Array<String>) {
    CrsTransformation().main()
}
