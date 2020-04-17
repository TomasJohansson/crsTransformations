package sample

// the package below is from the Java library "crs-transformation-constants"
import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9.EpsgNumber

// the other "com.programmerare" packages below are from Kotlin libraries ( artifactId's starting with "crs-transformation-adapter-" , https://mvnrepository.com/artifact/com.programmerare.crs-transformation )
import com.programmerare.crsTransformations.coordinate.{CrsCoordinate, CrsCoordinateFactory}
import com.programmerare.crsTransformations.crsIdentifier.{CrsIdentifier, CrsIdentifierFactory}
import com.programmerare.crsTransformations.{CrsTransformationAdapter, CrsTransformationResult, CrsTransformationAdapterLeafFactory}
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight.createFromInstance
import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J
import com.programmerare.crsTransformationAdapterProj4jLocationtech.CrsTransformationAdapterProj4jLocationtech
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS
import com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA

import scala.jdk.CollectionConverters._ // https://docs.scala-lang.org/overviews/collections-2.13/conversions-between-java-and-scala-collections.html

object CrsTransformation {

  // cd sample_code/scala
  // sbt run
  def main(args: Array[String]): Unit = {
    println(s"Implicit/default EpsgNumber for latitude/longitude: ${inputCoordinate.getCrsIdentifier.getEpsgNumber}") // 4326
    println(s"InputCoordinate: ${inputCoordinate}")
    println(s"TargetCrsIdentifier: ${targetCrsIdentifier}")
    println(s"Coordinate transformations from EPSG ${EpsgNumber.WORLD__WGS_84__4326} to EPSG ${EpsgNumber.SWEDEN__SWEREF99_TM__3006}")

    val crsTransformationResults = transform(inputCoordinate, targetCrsIdentifier, getAllCrsTransformationAdapters())
    crsTransformationResults.foreach { result =>
      val outputCoordinate = result.getOutputCoordinate
      val crs = result.getCrsTransformationAdapterResultSource    
      val implementationType = crs.getImplementationType
      val version = crs.getVersionOfImplementationAdapteeLibrary
      println(s"$implementationType $version : X / Y ===> ${outputCoordinate.getX} / ${outputCoordinate.getY}") 
    }

    println("Short names of all Leaf implementations:")
    val allLeafs = CrsTransformationAdapterLeafFactory.getInstancesOfAllKnownAvailableImplementations().asScala
    allLeafs.foreach { leaf =>
      println(leaf.getShortNameOfImplementation)
    }
    
    // Below is the printed output from the above code in this main method: 
    //    Implicit/default EpsgNumber for latitude/longitude: 4326
    //    InputCoordinate: Coordinate(xEastingLongitude=18.059196, yNorthingLatitude=59.330231, crsIdentifier=CrsIdentifier(crsCode='EPSG:4326', isEpsgCode=true, epsgNumber=4326))
    //    TargetCrsIdentifier: CrsIdentifier(crsCode='EPSG:3006', isEpsgCode=true, epsgNumber=3006)
    //    Coordinate transformations from EPSG 4326 to EPSG 3006
    //    LEAF_PROJ4J 0.1.0 : X / Y ===> 674032.357326444 / 6580821.991123579
    //    LEAF_PROJ4J_LOCATIONTECH 1.1.1 : X / Y ===> 674032.357326444 / 6580821.991123579
    //    LEAF_NGA_GEOPACKAGE 3.5.0 : X / Y ===> 674032.357326444 / 6580821.991123579
    //    LEAF_ORBISGIS 1.5.2 : X / Y ===> 674032.3573261796 / 6580821.991121078
    //    LEAF_GOOBER 1.1 : X / Y ===> 674032.357 / 6580821.991
    //    LEAF_GEOTOOLS 23.0 : X / Y ===> 674032.3571771547 / 6580821.994371211
    //    COMPOSITE_MEDIAN 1.1.0 : X / Y ===> 674032.3573263118 / 6580821.991123579
    //    COMPOSITE_AVERAGE 1.1.0 : X / Y ===> 674032.357247111 / 6580821.991643838
    //    COMPOSITE_FIRST_SUCCESS 1.1.0 : X / Y ===> 674032.357 / 6580821.991
    //    COMPOSITE_WEIGHTED_AVERAGE 1.1.0 : X / Y ===> 674032.3571927036 / 6580821.991623241
    //    Short names of all Leaf implementations:
    //    GooberCTL
    //    GeoPackageNGA
    //    GeoTools
    //    OrbisgisCTS
    //    Proj4jLocationtech
    //    Proj4J
  }
  
  private lazy val inputCoordinate = CrsCoordinateFactory.latLon(59.330231, 18.059196) //  Implicit/Default CrsIdentifier for latitude/longitude: EpsgNumber.WORLD__WGS_84__4326
  private lazy val targetCrsIdentifier = CrsIdentifierFactory.createFromEpsgNumber(EpsgNumber.SWEDEN__SWEREF99_TM__3006)
  private lazy val weightedAverageCrsTransformationAdapter = {
    val crsTransformationAdapterWeights = List(
      createFromInstance(new CrsTransformationAdapterProj4J, 1.0),
      createFromInstance(new CrsTransformationAdapterOrbisgisCTS, 1.0),
      createFromInstance(new CrsTransformationAdapterGeoPackageNGA, 1.0),
      createFromInstance(new CrsTransformationAdapterGeoTools, 1.0),
      createFromInstance(new CrsTransformationAdapterGooberCTL, 2.0)
    )
    CrsTransformationAdapterCompositeFactory.createCrsTransformationWeightedAverage(
      crsTransformationAdapterWeights.asJava // "asJava" is available thanks to:  import scala.jdk.CollectionConverters._
    )
  }

  private lazy val allCrsTransformationAdapters = List(
    new CrsTransformationAdapterProj4J,
    new CrsTransformationAdapterProj4jLocationtech,
    new CrsTransformationAdapterGeoPackageNGA,
    new CrsTransformationAdapterOrbisgisCTS,
    new CrsTransformationAdapterGooberCTL,
    new CrsTransformationAdapterGeoTools,
    CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian(),
    CrsTransformationAdapterCompositeFactory.createCrsTransformationAverage(),
    CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess(),
    weightedAverageCrsTransformationAdapter
  )

  def getTargetCrsIdentifier(): CrsIdentifier = {
    targetCrsIdentifier
  }

  def getInputCoordinate(): CrsCoordinate = {
    inputCoordinate
  }

  def getAllCrsTransformationAdapters(): List[CrsTransformationAdapter] = {
    allCrsTransformationAdapters
  }

  def transform(
    inputCoordinate: CrsCoordinate,
    targetCrsIdentifier: CrsIdentifier,
    allCrsTransformationAdapters: List[CrsTransformationAdapter]
  ): List[CrsTransformationResult] = {
    allCrsTransformationAdapters.map { crsTransformationAdapter =>
      crsTransformationAdapter.transform(inputCoordinate, targetCrsIdentifier)
    }
  }
}
