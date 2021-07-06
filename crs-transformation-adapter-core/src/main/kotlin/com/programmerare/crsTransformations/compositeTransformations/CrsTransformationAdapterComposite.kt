package com.programmerare.crsTransformations.compositeTransformations

import com.programmerare.crsTransformations.*
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier
import java.lang.RuntimeException

/**
 * Base class for the 'composite' adapters.
 * 
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-adapter-core" project is licensed with MIT.
 * Other subprojects may be released with other licenses e.g. LGPL or Apache License 2.0.
 * Please find more information in the license file at the root directory of each subproject
 * (e.g. the subprojects "crs-transformation-adapter-impl-geotools" , "crs-transformation-adapter-impl-proj4j" and so on)
 * @see CrsTransformationAdapterBase
 * @see CompositeStrategy
*/
final class CrsTransformationAdapterComposite private constructor(

    /**
     * Interface for calculating the resulting coordinate in different ways, 
     * e.g. one stratefy implementation calculates the median and another the average.
     */        
    private val compositeStrategy: CompositeStrategy

) : CrsTransformationAdapterBase(), CrsTransformationAdapter {

    /**
     *  For a Composite implementation the version number is the version of this library
     *  itself i.e. "com.programmerare.crs-transformation:crs-transformation-adapter-core:1.1.1"
     * @return "1.1.1"
     * @see CrsTransformationAdapter.getVersionOfImplementationAdapteeLibrary
     */
    override fun getVersionOfImplementationAdapteeLibrary(): String {
        // the semantic is not really correct here, since it is not actually 
        // an adapter to a third party adaptee library but instead
        // the composites can simply refer to the library itself
        return "2.0.0" // the same as the library itself as specified in build.gradle like below: 
        // ext.crsTransformationVersion = '2.0.0' + rootProject.snapshotSuffixOrEmpty 
    }

    override final protected fun transformHook(inputCoordinate: CrsCoordinate, crsIdentifierForOutputCoordinateSystem: CrsIdentifier): CrsCoordinate {
        val transformResult = transform(inputCoordinate, crsIdentifierForOutputCoordinateSystem)
        if(transformResult.isSuccess) {
            return transformResult.outputCoordinate
        }
        else {
            throw RuntimeException("Transformation failed")
        }
    }

    override final fun transform(inputCoordinate: CrsCoordinate, crsIdentifierForOutputCoordinateSystem: CrsIdentifier): CrsTransformationResult {
        val allCrsTransformationAdapters = compositeStrategy._getAllTransformationAdaptersInTheOrderTheyShouldBeInvoked()
        val list = mutableListOf<CrsTransformationResult>()
        var lastResultOrNullIfNoPrevious: CrsTransformationResult? = null
        for (crsTransformationAdapter: CrsTransformationAdapter in allCrsTransformationAdapters) {
            if(!compositeStrategy._shouldContinueIterationOfAdaptersToInvoke(lastResultOrNullIfNoPrevious)) {
                break
            }
            val res = crsTransformationAdapter.transform(inputCoordinate, crsIdentifierForOutputCoordinateSystem)
            list.add(res)
            lastResultOrNullIfNoPrevious = res
        }
        return compositeStrategy._calculateAggregatedResult(list, inputCoordinate, crsIdentifierForOutputCoordinateSystem, this)
    }

    override final fun getTransformationAdapterChildren(): List<CrsTransformationAdapter> {
        return compositeStrategy._getAllTransformationAdaptersInTheOrderTheyShouldBeInvoked()
    }

    override final fun isComposite(): Boolean {
        return true
    }

    override fun getImplementationType() : CrsTransformationImplementationType {
        return compositeStrategy._getImplementationType()
    }

    internal companion object {
        /**
         * This method is not intended for public use,
         * but instead the factory class should be used.
         * @see CrsTransformationAdapterCompositeFactory
         */
        @JvmStatic
        internal fun _createCrsTransformationAdapterComposite(
            compositeStrategy: CompositeStrategy
        ): CrsTransformationAdapterComposite {
            return CrsTransformationAdapterComposite(compositeStrategy)
        }
    }
    
}
