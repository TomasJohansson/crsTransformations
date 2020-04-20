package com.programmerare.crsTransformations

import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier
import com.programmerare.crsTransformations.crsIdentifier.createFromCrsCode
import com.programmerare.crsTransformations.crsIdentifier.createFromEpsgNumber
import java.lang.IllegalArgumentException
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
        // println("" + this.getAdapteeType() + " transformToCoordinate " + crsCoordinate)
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

    override fun getAdapteeType() : CrsTransformationAdapteeType {
        // Should be overridden by subclasses
        return CrsTransformationAdapteeType.UNSPECIFIED
    }

    override fun getImplementationType() : CrsTransformationImplementationType {
        // Should be overridden by subclasses
        return CrsTransformationImplementationType.UNSPECIFIED
    }

    // The below method is protected which means that: Be careful to remove it !
    // External code might still be using it, i.e. removal could cause incompatibilities 
    // if mixing core libraries and implementation libraries with different versions
    // if it is removed.    
    @Deprecated("Deprecated from version 1.1.1. Replacement: 'getSomeClassFromTheJarFileOfTheImplementationLibrary' and lazy loaded '_version'")
    /**
     * This helper method is protected since it is NOT intended for
     * client code but only for test code purposes.
     * 
     * It should be overridden by subclasses.
     * @return empty string is returned as the default value
     *      which should also be returned byt the composites (i.e. they should not override).
     *      
     *      The 'leaf' adapter implementations should return the
     *      name of the jar file (potentially including a path)
     *      for the used adaptee library.
     *      
     *      The reason is that the jar files (retrieved through Maven)
     *      includes the version name and can be asserted in test code
     *      to help remembering that the value of an enum specifying
     *      the 'adaptee' (and version) should be updated after an adaptee upgrade.
     * @see CrsTransformationAdapteeType
     */
    protected open fun getNameOfJarFileOrEmptyString(): String {
        return ""
    }

    // The below method is protected which means that: Be careful to remove it !
    // External code might still be using it, i.e. removal could cause incompatibilities 
    // if mixing core libraries and implementation libraries with different versions
    // if it is removed.
    @Deprecated("Deprecated from version 1.1.1. Replacement: 'getSomeClassFromTheJarFileOfTheImplementationLibrary' and lazy loaded '_version'")
    /**
     * Protected helper method intended to be used from subclasses
     * when implementing the method that should return the name
     * of a jar file belonging to an adaptee library.
     */
    protected final fun getNameOfJarFileFromProtectionDomain(
        protectionDomainCreatedFromSomeClassInTheThidPartAdapteeLibrary: ProtectionDomain
    ): String {
        // Doing the code in small steps below to figure out where the problem is when it does not work from Jython
        // (though it does work from Java, Kotlin, Scala, Groovy and JRuby)
        debug("getNameOfJarFileFromProtectionDomain protectionDomain: " + protectionDomainCreatedFromSomeClassInTheThidPartAdapteeLibrary) // "... protectionDomain: ProtectionDomain  (null <no signer certificates>)"
        val codeSource = protectionDomainCreatedFromSomeClassInTheThidPartAdapteeLibrary.codeSource
        debug("getNameOfJarFileFromProtectionDomain codeSource: " + codeSource) // "... codeSource: (null <no signer certificates>)"
        val location = codeSource.location
        debug("getNameOfJarFileFromProtectionDomain location: " + location) // "... location: null"
        val externalForm = location.toExternalForm()
        debug("getNameOfJarFileFromProtectionDomain externalForm " + externalForm)
        return externalForm
    }

    protected fun debug(s: String) {
        //logger.debug(s) 
        //logger.info(s)
    }    
}
