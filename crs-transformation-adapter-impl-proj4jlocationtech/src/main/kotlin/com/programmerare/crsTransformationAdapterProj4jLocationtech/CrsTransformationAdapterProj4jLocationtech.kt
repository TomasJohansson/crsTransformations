package com.programmerare.crsTransformationAdapterProj4jLocationtech

import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate
import com.programmerare.crsTransformations.CrsTransformationAdapteeType
import com.programmerare.crsTransformations.CrsTransformationAdapter
import com.programmerare.crsTransformations.CrsTransformationAdapterBaseLeaf
import com.programmerare.crsTransformations.CrsTransformationImplementationType
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier
import com.programmerare.crsTransformations.coordinate.createFromXEastingLongitudeAndYNorthingLatitude

// https://github.com/locationtech/proj4j
// The above library seems to be based on the following library: 
// https://github.com/Proj4J/proj4j/
// As of april 2020, the latest commit in "Proj4J/proj4j" was in 2015, while "locationtech/proj4j" is active with commits from 2020.   

// In fact, when the implementation is this file was added, it was copied from the existing class "CrsTransformationAdapterProj4J"
// with exactly the same implementation of the method "transformHook".
// But the import statements had to be changed from e.g. "import org.osgeo.proj4j.CRSFactory" to "import org.locationtech.proj4j.CRSFactory"

// "What's the maintenance status?":
// https://github.com/locationtech/proj4j/issues/1

/**
 * Implementation of the interface CrsTransformationAdapter.
 * See the documentation of the interface.
 * @see com.programmerare.crsTransformations.CrsTransformationAdapter
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-code-generation" project is licensed with MIT.
 * The code in the "crs-transformation-adapter-impl-proj4jlocationtech" project
 * is licensed with Apache License Version 2.0 i.e. the same license as the adaptee library proj4j.
 */
class CrsTransformationAdapterProj4jLocationtech : CrsTransformationAdapterBaseLeaf(), CrsTransformationAdapter {

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
    override fun getAdapteeType() : CrsTransformationAdapteeType {
        return CrsTransformationAdapteeType.LEAF_PROJ4J_LOCATIONTECH_1_1_1
    }

    override fun getImplementationType() : CrsTransformationImplementationType {
        return CrsTransformationImplementationType.LEAF_PROJ4J_LOCATIONTECH
    }
    
    // The purpose of the method below is to use it in test code
    // for detecting upgrades to a new version (and then update the above method returned enum value)
    // Future failure will be a reminder to update the above enum value
    protected override fun getNameOfJarFileOrEmptyString(): String {
        return super.getNameOfJarFileFromProtectionDomain(ProjCoordinate::class.java.protectionDomain)
    }
    // ----------------------------------------------------------
}
