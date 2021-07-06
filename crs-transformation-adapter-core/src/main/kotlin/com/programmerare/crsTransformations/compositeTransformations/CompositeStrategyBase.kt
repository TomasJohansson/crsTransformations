package com.programmerare.crsTransformations.compositeTransformations

import com.programmerare.crsTransformations.*
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import java.lang.RuntimeException

/**
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-adapter-core" project is licensed with MIT.
 * Other subprojects may be released with other licenses e.g. LGPL or Apache License 2.0.
 * Please find more information in the license file at the root directory of each subproject
 * (e.g. the subprojects "crs-transformation-adapter-impl-geotools" , "crs-transformation-adapter-impl-proj4j" and so on)
 */
internal abstract class CompositeStrategyBase protected constructor
    (private val crsTransformationAdapters: List<CrsTransformationAdapter>)
    : CompositeStrategy {

    init {
        if(crsTransformationAdapters.size < 1) {
            throw RuntimeException("'Composite' adapter can not be created with an empty list of 'leaf' adapters") 
        }
    }
    
    override final fun _getAllTransformationAdaptersInTheOrderTheyShouldBeInvoked(): List<CrsTransformationAdapter> {
        return crsTransformationAdapters
    }

    /**
     * This base class method is reusable from both subclasses
     * that calculates the median or average which is provided with the last
     * function parameter of the method
     */
    protected fun calculateAggregatedResultBase(
            allResults: List<CrsTransformationResult>,
            inputCoordinate: CrsCoordinate,
            crsTransformationAdapterThatCreatedTheResult: CrsTransformationAdapter,
            crsTransformationResultStatistic: CrsTransformationResultStatistic,
            medianOrAverage: (CrsTransformationResultStatistic) -> CrsCoordinate
    ): CrsTransformationResult {
        if(crsTransformationResultStatistic.isStatisticsAvailable()) {
            //  val coordRes = crsTransformationResultStatistic.getCoordinateMedian() // THE ONLY DIFFERENCE in the above mentioned two classes
            //  val coordRes = crsTransformationResultStatistic.getCoordinateAverage()  // THE ONLY DIFFERENCE in the above mentioned two classes
            val coordRes: CrsCoordinate = medianOrAverage(crsTransformationResultStatistic) // this line replaced the above two lines in different subclasses when doing refactoring
            return CrsTransformationResult._createCrsTransformationResult(
                inputCoordinate,
                outputCoordinate = coordRes,
                exception = null,
                isSuccess = true,
                crsTransformationAdapterResultSource = crsTransformationAdapterThatCreatedTheResult,
                transformationResultChildren = allResults,
                nullableCrsTransformationResultStatistic = crsTransformationResultStatistic
            )
        }
        else {
            return CrsTransformationResult._createCrsTransformationResult(
                inputCoordinate,
                outputCoordinate = null,
                exception = null,
                isSuccess = false,
                crsTransformationAdapterResultSource = crsTransformationAdapterThatCreatedTheResult,
                transformationResultChildren = allResults,
                nullableCrsTransformationResultStatistic = crsTransformationResultStatistic
            )
        }
    }

    override fun _getImplementationType() : CrsTransformationImplementationType {
        return CrsTransformationImplementationType.UNSPECIFIED_COMPOSITE
    }    
}
