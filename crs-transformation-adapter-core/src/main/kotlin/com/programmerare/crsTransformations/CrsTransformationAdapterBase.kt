package com.programmerare.crsTransformations

import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier
import com.programmerare.crsTransformations.crsIdentifier.createFromCrsCode
import com.programmerare.crsTransformations.crsIdentifier.createFromEpsgNumber
import java.security.ProtectionDomain
import org.slf4j.LoggerFactory

/**
 * The base class of the adapter interface implementing most of the 
 * transform methods as final i.e. not overridden by subclasses.  
 * 
 * @see CrsTransformationAdapter
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-adapter-core" project is licensed with MIT.
 * Other subprojects may be released with other licenses e.g. LGPL or Apache License 2.0.
 * Please find more information in the license file at the root directory of each subproject
 * (e.g. the subprojects "crs-transformation-adapter-impl-geotools" , "crs-transformation-adapter-impl-proj4j" and so on)
 */
abstract class CrsTransformationAdapterBase : CrsTransformationAdapter {
    private val logger = LoggerFactory.getLogger(CrsTransformationAdapterBase::class.java)
    
    /**
     * Transforms a coordinate to another coordinate reference system.  
     * 
     * This is a "hook" method (as it is named in the design pattern Template Method)   
     * which must be implemented by subclasses.
     */
    abstract protected fun transformHook(
        inputCoordinate: CrsCoordinate,
        crsIdentifierForOutputCoordinateSystem: CrsIdentifier
    ): CrsCoordinate


    // -------------------------------------------------
    // The three below methods returning a coordinate object
    // are all final (i.e. not overridden) and invokes
    // a so called "hook" method(named so in the Template Method pattern)
    // which is an abstract method that must be implemented in a subclass.

    override final fun transformToCoordinate(
        inputCoordinate: CrsCoordinate,
        crsCodeForOutputCoordinateSystem: String
    ): CrsCoordinate {
        // this Template Method is invoking the below overloaded hook method in subclasses
        return transformHook(
            inputCoordinate,
            createFromCrsCode(crsCodeForOutputCoordinateSystem)
        )
    }

    override final fun transformToCoordinate(
        inputCoordinate: CrsCoordinate,
        epsgNumberForOutputCoordinateSystem: Int
    ): CrsCoordinate {
        return transformToCoordinate(
            inputCoordinate,
            createFromEpsgNumber(epsgNumberForOutputCoordinateSystem)
        )
    }

    override final fun transformToCoordinate(
        inputCoordinate: CrsCoordinate,
        crsIdentifierForOutputCoordinateSystem: CrsIdentifier
    ): CrsCoordinate {
        val crsCoordinate = transformHook(
            inputCoordinate,
            crsIdentifierForOutputCoordinateSystem
        )
        return crsCoordinate        
    }
    // -------------------------------------------------

    // -------------------------------------------------
    // Two of the three methods (define in the interface)
    // returning a transformation result object are implemented
    // here in the base class as final methods which only invokes the third method.

    override final fun transform(
        inputCoordinate: CrsCoordinate,
        epsgNumberForOutputCoordinateSystem: Int
    ): CrsTransformationResult {
        return transform(
            inputCoordinate,
            crsIdentifierForOutputCoordinateSystem = createFromEpsgNumber(epsgNumberForOutputCoordinateSystem)
        )
    }

    override final fun transform(
        inputCoordinate: CrsCoordinate,
        crsCodeForOutputCoordinateSystem: String
    ): CrsTransformationResult {
        return transform(
            inputCoordinate,
            crsIdentifierForOutputCoordinateSystem = createFromCrsCode(crsCodeForOutputCoordinateSystem)
        )
    }
    // -------------------------------------------------

   override final fun getLongNameOfImplementation(): String {
        return this.javaClass.name
    }

    private val classNamePrefix = "CrsTransformationAdapter"
    // if the above string would change because of class renamings
    // then it will be detected by a failing test

    override final fun getShortNameOfImplementation(): String {
        val className = this.javaClass.simpleName
        if(className.startsWith(classNamePrefix) && !className.equals(classNamePrefix)) {
            return className.substring(classNamePrefix.length)
        }
        else {
            return className
        }
    }

    override fun getImplementationType() : CrsTransformationImplementationType {
        // Should be overridden by subclasses
        return CrsTransformationImplementationType.UNSPECIFIED
    }

//    protected fun debug(s: String) {
//        //logger.debug(s)
//        //logger.info(s)
//    }
}
