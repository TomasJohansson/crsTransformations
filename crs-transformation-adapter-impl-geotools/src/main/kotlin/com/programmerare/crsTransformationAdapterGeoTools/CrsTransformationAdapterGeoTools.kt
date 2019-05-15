package com.programmerare.crsTransformationAdapterGeoTools

// build.gradle: implementation("org.geotools:gt-main:20.2")
import com.programmerare.crsTransformations.CrsTransformationAdapteeType
import com.programmerare.crsTransformations.coordinate.createFromYNorthingLatitudeAndXEastingLongitude
import com.programmerare.crsTransformations.CrsTransformationAdapterBaseLeaf
import com.programmerare.crsTransformations.CrsTransformationAdapter
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier
import org.locationtech.jts.geom.GeometryFactory // jts-core-...jar
import org.geotools.geometry.jts.JTSFactoryFinder // gt-main-...jar
import org.geotools.referencing.CRS
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.opengis.referencing.operation.MathTransform
import org.geotools.geometry.jts.JTS


// http://docs.geotools.org/
// https://github.com/geotools/geotools/blob/master/pom.xml

/**
 * Implementation of the interface CrsTransformationAdapter.
 * See the documentation of the interface. 
 * @see com.programmerare.crsTransformations.CrsTransformationAdapter
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-code-generation" project is licensed with MIT.
 * The code in the "crs-transformation-adapter-impl-geotools" project
 * is licensed with LGPL i.e. the same license as the adaptee library geotools.
 */
class CrsTransformationAdapterGeoTools : CrsTransformationAdapterBaseLeaf(), CrsTransformationAdapter {

    private val geometryFactory: GeometryFactory

    init {
        geometryFactory = JTSFactoryFinder.getGeometryFactory()
    }

    override protected fun transformHook(
        inputCoordinate: CrsCoordinate,
        crsIdentifierForOutputCoordinateSystem: CrsIdentifier
    ): CrsCoordinate {
        val sourceCRS: CoordinateReferenceSystem = CRS.decode(inputCoordinate.crsIdentifier.crsCode, true)
        val targetCRS: CoordinateReferenceSystem = CRS.decode(crsIdentifierForOutputCoordinateSystem.crsCode, true)
        val mathTransform: MathTransform = CRS.findMathTransform(sourceCRS, targetCRS)

        /*
        val sourceArray = doubleArrayOf(inputCoordinate.xEastingLongitude, inputCoordinate.yNorthingLatitude)
        val destinationArray = doubleArrayOf(0.0, 0.0)
        JTS.xform(transform, sourceArray, destinationArray)
        val lon = destinationArray[0]
        val lat = destinationArray[1]
        */
        // the above implementation is an alternative to the below implementation
        val inputPoint = geometryFactory.createPoint(org.locationtech.jts.geom.Coordinate(inputCoordinate.xEastingLongitude, inputCoordinate.yNorthingLatitude))
        val sourceGeometry = inputPoint
        val outputGeometry = JTS.transform(sourceGeometry, mathTransform)
        val outputCoordinate = outputGeometry.coordinate
        val lon = outputCoordinate.x
        val lat = outputCoordinate.y

        return createFromYNorthingLatitudeAndXEastingLongitude(yNorthingLatitude = lat, xEastingLongitude = lon, crsIdentifier = crsIdentifierForOutputCoordinateSystem)
    }

    // ----------------------------------------------------------

    override fun getAdapteeType() : CrsTransformationAdapteeType {
        return CrsTransformationAdapteeType.LEAF_GEOTOOLS_21_1
    }
    // The purpose of the method below is to use it in test code
    // for detecting upgrades to a new version (and then update the above method returned enum value)
    // Future failure will be a reminder to update the above enum value
    protected override fun getNameOfJarFileOrEmptyString(): String {
        return super.getNameOfJarFileFromProtectionDomain(JTS::class.java.protectionDomain)
    }
    // ----------------------------------------------------------
}