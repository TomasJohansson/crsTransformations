package com.programmerare.crsTransformationAdapterGeoPackageNGA

import com.programmerare.crsTransformations.CrsTransformationAdapter
import com.programmerare.crsTransformations.CrsTransformationAdapterBaseLeaf
import com.programmerare.crsTransformations.CrsTransformationImplementationType
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier
import com.programmerare.crsTransformations.coordinate.createFromXEastingLongitudeAndYNorthingLatitude
import mil.nga.geopackage.contents.ContentsDao
import mil.nga.sf.Point
import mil.nga.sf.proj.ProjectionFactory

// " ngageoint/geopackage-java "
// https://github.com/ngageoint/geopackage-java
// https://github.com/ngageoint/geopackage-core-java
// http://ngageoint.github.io/geopackage-java/

/**
 * Implementation of the interface CrsTransformationAdapter.
 * See the documentation of the interface.
 * @see com.programmerare.crsTransformations.CrsTransformationAdapter
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-code-generation" project is licensed with MIT.
 * The code in the "crs-transformation-adapter-impl-nga" project
 * is also licensed with MIT i.e. the same license as the adaptee library ngageoint/geopackage.
 */
class CrsTransformationAdapterGeoPackageNGA : CrsTransformationAdapterBaseLeaf(), CrsTransformationAdapter {

    override protected fun transformHook(
        inputCoordinate: CrsCoordinate,
        crsIdentifierForOutputCoordinateSystem: CrsIdentifier
    ): CrsCoordinate {
        val inputEPSGnumber = inputCoordinate.crsIdentifier.epsgNumber.toLong()
        val outputEPSGnumber = crsIdentifierForOutputCoordinateSystem.epsgNumber.toLong()
        val projection = ProjectionFactory.getProjection(inputEPSGnumber) // "EPSG:4326" wgs84
        val projectionTransform = projection.getTransformation(outputEPSGnumber)  // sweref 99
        val inputPoint = Point(inputCoordinate.xEastingLongitude, inputCoordinate.yNorthingLatitude)
        val outputPoint = projectionTransform.transform(inputPoint)
        val outputCoordinate = createFromXEastingLongitudeAndYNorthingLatitude(outputPoint.getX(), outputPoint.getY(), crsIdentifierForOutputCoordinateSystem)
        return outputCoordinate
    }

    // ----------------------------------------------------------
    override fun getImplementationType() : CrsTransformationImplementationType {
        return CrsTransformationImplementationType.LEAF_NGA_GEOPACKAGE
    }
    
    // The purpose of the method below is to use it in test code
    // for detecting upgrades to a new version (and then update the above method returned enum value)
    // Future failure will be a reminder to update the above enum value
    protected override fun getNameOfJarFileOrEmptyString(): String {
        return super.getNameOfJarFileFromProtectionDomain(ContentsDao::class.java.protectionDomain)
    }

    /**
     * @since 1.1.1
     */    
    protected override fun getSomeClassFromTheJarFileOfTheImplementationLibrary(): Class<*> {
        return ContentsDao::class.java
    }
    // ----------------------------------------------------------
}
