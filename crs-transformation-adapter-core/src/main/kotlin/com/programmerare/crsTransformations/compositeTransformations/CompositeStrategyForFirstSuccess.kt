package com.programmerare.crsTransformations.compositeTransformations

import com.programmerare.crsTransformations.*
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier

/**
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-adapter-core" project is licensed with MIT.
 * Other subprojects may be released with other licenses e.g. LGPL or Apache License 2.0.
 * Please find more information in the license file at the root directory of each subproject
 * (e.g. the subprojects "crs-transformation-adapter-impl-geotools" , "crs-transformation-adapter-impl-proj4j" and so on)
 */
internal class CompositeStrategyForFirstSuccess private constructor(
    private val crsTransformationAdapters: List<CrsTransformationAdapter>
) : CompositeStrategyBase(crsTransformationAdapters), CompositeStrategy {

    override fun _shouldContinueIterationOfAdaptersToInvoke(lastResultOrNullIfNoPrevious: CrsTransformationResult?): Boolean {
        return lastResultOrNullIfNoPrevious == null || !lastResultOrNullIfNoPrevious.isSuccess
    }

    override fun _calculateAggregatedResult(
            allResults: List<CrsTransformationResult>,
            inputCoordinate: CrsCoordinate,
            crsIdentifierForOutputCoordinateSystem: CrsIdentifier,
            crsTransformationAdapterThatCreatedTheResult: CrsTransformationAdapter
    ): CrsTransformationResult {
        val indexForSuccess = allResults.size - 1;
        if(indexForSuccess >= 0 && allResults.get(indexForSuccess).isSuccess) {
            // the last result (if any result at all, of course) 
            // should always be success, so in theory the second part in the above "if statement" i.e. the 'isSuccess'
            // should not be necessary (but it still there just to make it easier to find bugs, if it for some reason would not be success)
            // since the calculation is interrupted when a successful result is found (i.e. the last result, when the iteration was interrupted, should be a success) 
            return CrsTransformationResult._createCrsTransformationResult(
                inputCoordinate,
                outputCoordinate = allResults.get(indexForSuccess).outputCoordinate,
                exception = null,
                isSuccess = true,
                crsTransformationAdapterResultSource = crsTransformationAdapterThatCreatedTheResult,
                transformationResultChildren = allResults
            )
        }
        else {
            return CrsTransformationResult._createCrsTransformationResult(
                inputCoordinate,
                outputCoordinate = null,
                exception = null,
                isSuccess = false,
                crsTransformationAdapterResultSource = crsTransformationAdapterThatCreatedTheResult,
                transformationResultChildren = allResults
            )
        }
    }

    override fun _getImplementationType() : CrsTransformationImplementationType {
        return CrsTransformationImplementationType.COMPOSITE_FIRST_SUCCESS
    }    

    internal companion object {

        /**
         * This method is not intended for public use, 
         * but instead the factory class should be used.
         * @see CrsTransformationAdapterCompositeFactory
         */
        @JvmStatic
        internal fun _createCompositeStrategyForFirstSuccess (
            list: List<CrsTransformationAdapter>
        ): CompositeStrategyForFirstSuccess {
            return CompositeStrategyForFirstSuccess(list) 
        }
    }
}
