package com.programmerare.crsTransformationAdapterProj4J

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;
import com.programmerare.crsTransformations.CrsTransformationAdapter
import com.programmerare.crsTransformations.CrsTransformationAdapterBaseLeaf
import com.programmerare.crsTransformations.CrsTransformationImplementationType
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier
import com.programmerare.crsTransformations.coordinate.createFromXEastingLongitudeAndYNorthingLatitude

// " Proj4J/proj4j "
// https://github.com/Proj4J/proj4j

// The adaptee library "Proj4J/proj4j" has not been active in the last five years.
// As of april 2020, the last commit was in 2015.
// The library "locationtech/proj4j" seem to become the "replacement" of "Proj4J/proj4j".
// See also comments in the more recently added file 
// "crs-transformation-adapter-impl-proj4jlocationtech/src/main/kotlin/com/programmerare/crsTransformationAdapterProj4jLocationtech/CrsTransformationAdapterProj4jLocationtech.kt"

/**
 * Implementation of the interface CrsTransformationAdapter.
 * See the documentation of the interface.
 * @see com.programmerare.crsTransformations.CrsTransformationAdapter
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-code-generation" project is licensed with MIT.
 * The code in the "crs-transformation-adapter-impl-proj4j" project
 * is licensed with Apache License Version 2.0 i.e. the same license as the adaptee library proj4j.
 */
class CrsTransformationAdapterProj4J : CrsTransformationAdapterBaseLeaf(), CrsTransformationAdapter {

    private var coordinateTransformFactory: CoordinateTransformFactory = CoordinateTransformFactory()
    private var crsFactory: CRSFactory = CRSFactory()

    override protected fun transformHook(
            inputCoordinate: CrsCoordinate,
            crsIdentifierForOutputCoordinateSystem: CrsIdentifier
    ): CrsCoordinate {
        val sourceCrs = crsFactory.createFromName(inputCoordinate.crsIdentifier.crsCode)
        val targetCrs = crsFactory.createFromName(crsIdentifierForOutputCoordinateSystem.crsCode)
        val coordinateTransform = coordinateTransformFactory.createTransform(sourceCrs, targetCrs)
        val projCoordinateInput = ProjCoordinate(inputCoordinate.xEastingLongitude, inputCoordinate.yNorthingLatitude)
        val projCoordinateOutput = ProjCoordinate()
        coordinateTransform.transform(projCoordinateInput, projCoordinateOutput)
        return createFromXEastingLongitudeAndYNorthingLatitude(projCoordinateOutput.x, projCoordinateOutput.y, crsIdentifierForOutputCoordinateSystem)
    }

    // ----------------------------------------------------------
    override fun getImplementationType() : CrsTransformationImplementationType {
        return CrsTransformationImplementationType.LEAF_PROJ4J
    }


    /**
     * @since 1.1.1
     */    
    protected override fun getSomeClassFromTheJarFileOfTheImplementationLibrary(): Class<*> {
        return ProjCoordinate::class.java
    }    
    // ----------------------------------------------------------
}
