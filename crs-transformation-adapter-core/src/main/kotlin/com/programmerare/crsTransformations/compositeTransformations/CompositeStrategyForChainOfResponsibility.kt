package com.programmerare.crsTransformations.compositeTransformations

import com.programmerare.crsTransformations.*
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier

internal class CompositeStrategyForChainOfResponsibility private constructor(
    private val crsTransformationAdapters: List<CrsTransformationAdapter>
) : CompositeStrategyBase(crsTransformationAdapters), CompositeStrategy {

    override fun shouldContinueIterationOfAdaptersToInvoke(lastResultOrNullIfNoPrevious: CrsTransformationResult?): Boolean {
        return lastResultOrNullIfNoPrevious == null || !lastResultOrNullIfNoPrevious.isSuccess
    }

    override fun calculateAggregatedResult(
            allResults: List<CrsTransformationResult>,
            inputCoordinate: CrsCoordinate,
            crsIdentifierForOutputCoordinateSystem: CrsIdentifier,
            crsTransformationAdapterThatCreatedTheResult: CrsTransformationAdapter
    ): CrsTransformationResult {
        if(allResults.size == 1 && allResults.get(0).isSuccess) {
            // there should never be more than one result with the ChainOfResponsibility implementation
            // since the calculation is interrupted at the first succeful result
            return CrsTransformationResult(
                inputCoordinate,
                _outputCoordinate = allResults.get(0).outputCoordinate,
                exception = null,
                isSuccess = true,
                crsTransformationAdapterResultSource = crsTransformationAdapterThatCreatedTheResult,
                transformationResultChildren = allResults
            )
        }
        else {
            return CrsTransformationResult(
                inputCoordinate,
                _outputCoordinate = null,
                exception = null,
                isSuccess = false,
                crsTransformationAdapterResultSource = crsTransformationAdapterThatCreatedTheResult,
                transformationResultChildren = allResults
            )
        }
    }

    override fun getAdapteeType() : CrsTransformationAdapteeType {
        return CrsTransformationAdapteeType.COMPOSITE_CHAIN_OF_RESPONSIBILITY
    }

    internal companion object {

        /**
         * This method is not intended for public use, 
         * but instead the factory class should be used.
         * @see CrsTransformationAdapterCompositeFactory
         */
        @JvmStatic
        internal fun _createCompositeStrategyForChainOfResponsibility (
            list: List<CrsTransformationAdapter>
        ): CompositeStrategyForChainOfResponsibility {
            return CompositeStrategyForChainOfResponsibility(list) 
        }
    }
}