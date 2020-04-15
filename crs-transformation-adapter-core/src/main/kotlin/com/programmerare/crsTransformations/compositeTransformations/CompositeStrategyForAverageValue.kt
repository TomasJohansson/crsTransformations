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
internal class CompositeStrategyForAverageValue private constructor(
    private val crsTransformationAdapters: List<CrsTransformationAdapter>
) : CompositeStrategyBase(crsTransformationAdapters), CompositeStrategy {

    override fun _shouldContinueIterationOfAdaptersToInvoke(lastResultOrNullIfNoPrevious: CrsTransformationResult?): Boolean {
        return true
    }

    override fun _calculateAggregatedResult(
            allResults: List<CrsTransformationResult>,
            inputCoordinate: CrsCoordinate,
            crsIdentifierForOutputCoordinateSystem: CrsIdentifier,
            crsTransformationAdapterThatCreatedTheResult: CrsTransformationAdapter
    ): CrsTransformationResult {
        val resultsStatistic = CrsTransformationResultStatistic._createCrsTransformationResultStatistic(allResults)
        return this.calculateAggregatedResultBase(
            allResults,
            inputCoordinate,
            crsTransformationAdapterThatCreatedTheResult,
            resultsStatistic,
            { r: CrsTransformationResultStatistic -> r.getCoordinateAverage() }
        )
   }

    override fun _getAdapteeType() : CrsTransformationAdapteeType {
        return CrsTransformationAdapteeType.COMPOSITE_AVERAGE
    }
    override fun _getImplementationType() : CrsTransformationImplementationType {
        return CrsTransformationImplementationType.COMPOSITE_AVERAGE
    }

    internal companion object {
        /**
         * This method is not intended for public use,
         * but instead the factory class should be used.
         * @see CrsTransformationAdapterCompositeFactory
         */
        @JvmStatic
        internal fun _createCompositeStrategyForAverageValue (
            list: List<CrsTransformationAdapter>
        ): CompositeStrategyForAverageValue {
            return CompositeStrategyForAverageValue(list)
        }
    }

}
