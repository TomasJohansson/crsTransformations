package com.programmerare.crsTransformationAdapterNgaGeoInt

import com.programmerare.crsTransformations.CrsTransformationAdapter
import com.programmerare.crsTransformations.CrsTransformationAdapterBaseLeaf
import com.programmerare.crsTransformations.CrsTransformationImplementationType
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier
import com.programmerare.crsTransformations.coordinate.createFromXEastingLongitudeAndYNorthingLatitude

// https://github.com/ngageoint/simple-features-proj-java/blob/master/src/main/java/mil/nga/sf/proj/GeometryTransform.java
import mil.nga.sf.proj.GeometryTransform

// https://github.com/ngageoint/simple-features-java/blob/master/src/main/java/mil/nga/sf/Point.java
import mil.nga.sf.Point

// https://github.com/ngageoint/projections-java/blob/master/src/main/java/mil/nga/proj/Projection.java
// https://github.com/ngageoint/projections-java/blob/master/src/main/java/mil/nga/proj/ProjectionFactory.java
import mil.nga.proj.Projection
import mil.nga.proj.ProjectionFactory

// https://github.com/ngageoint/simple-features-proj-java
// https://github.com/ngageoint/simple-features-java
// https://github.com/ngageoint/projections-java
// https://github.com/ngageoint/coordinate-reference-systems-java

// mil.nga.sf:sf-proj (ngageoint/simple-features-proj-java) has a dependency to mil.nga:sf (ngageoint/simple-features-java)
// and mil.nga:proj (ngageoint/projections-java) which has a dependency to mil.nga:crs (ngageoint/coordinate-reference-systems-java)
// The above mentioned dependencies can be seen in the maven pom files below:
// mil.nga.sf:sf-proj       https://github.com/ngageoint/simple-features-proj-java/blob/master/pom.xml
// mil.nga:sf               https://github.com/ngageoint/simple-features-java/blob/master/pom.xml
// mil.nga:proj             https://github.com/ngageoint/projections-java/blob/master/pom.xml
// mil.nga:crs              https://github.com/ngageoint/coordinate-reference-systems-java/blob/master/pom.xml

/**
 * Implementation of the interface CrsTransformationAdapter.
 * See the documentation of the interface.
 * @see com.programmerare.crsTransformations.CrsTransformationAdapter
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-code-generation" project is licensed with MIT.
 * The code in the "crs-transformation-adapter-impl-nga" project
 * is also licensed with MIT i.e. the same license as the adaptee library ngageoint/simple-features-proj-java.
 */
class CrsTransformationAdapterNgaGeoInt : CrsTransformationAdapterBaseLeaf(), CrsTransformationAdapter {

    override protected fun transformHook(
        inputCoordinate: CrsCoordinate,
        crsIdentifierForOutputCoordinateSystem: CrsIdentifier
    ): CrsCoordinate {
        val inputEPSGnumber = inputCoordinate.crsIdentifier.epsgNumber.toLong()
        val outputEPSGnumber = crsIdentifierForOutputCoordinateSystem.epsgNumber.toLong()

        val inputProjection: Projection = ProjectionFactory.getProjection(inputEPSGnumber)
        val outputProjection: Projection = ProjectionFactory.getProjection(outputEPSGnumber)
        val geometryTransform: GeometryTransform  = GeometryTransform.create(inputProjection, outputProjection)

        val inputPoint = Point(inputCoordinate.xEastingLongitude, inputCoordinate.yNorthingLatitude)
        val outputPoint = geometryTransform.transform(inputPoint)
        val outputCoordinate = createFromXEastingLongitudeAndYNorthingLatitude(outputPoint.getX(), outputPoint.getY(), crsIdentifierForOutputCoordinateSystem)
        return outputCoordinate
    }

    // ----------------------------------------------------------
    override fun getImplementationType() : CrsTransformationImplementationType {
        return CrsTransformationImplementationType.LEAF_NGA_GEOINT
    }
    
    /**
     * @since 1.1.1
     */    
    protected override fun getSomeClassFromTheJarFileOfTheImplementationLibrary(): Class<*> {
        return GeometryTransform::class.java
    }
    // ----------------------------------------------------------
}
