package com.programmerare.crsTransformations

import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.coordinate.createFromXEastingLongitudeAndYNorthingLatitude
import com.programmerare.crsTransformations.utils.MedianValueUtility
import java.lang.RuntimeException

/**
 * Class providing conveniently available aggregated information from multiple results.
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-adapter-core" project is licensed with MIT.
 * Other subprojects may be released with other licenses e.g. LGPL or Apache License 2.0.
 * Please find more information in the license file at the root directory of each subproject
 * (e.g. the subprojects "crs-transformation-adapter-impl-geotools" , "crs-transformation-adapter-impl-proj4j" and so on)
 */
class CrsTransformationResultStatistic private constructor(
    private val results: List<CrsTransformationResult>
) {

    /**
     * @return true if there is at least one succesful result but otherwise false.
     */
    fun isStatisticsAvailable(): Boolean {
        return getNumberOfResults() > 0
    }

    /**
     * @return the number of succesful results
     */
    fun getNumberOfResults(): Int {
        return _sucessfulCoordinatesLazyLoaded.size
    }

    /**
     * Precondition: isStatisticsAvailable must return true
     * @return a coordinate with the average X/Longitude and the average Y/Latitude
     */
    fun getCoordinateAverage(): CrsCoordinate {
        throwExceptionIfPreconditionViolated()
        return _coordinateAverageLazyLoaded
    }

    /**
     * Precondition: isStatisticsAvailable must return true
     * @return a coordinate with the median X/Longitude and the median Y/Latitude
     */
    fun getCoordinateMedian(): CrsCoordinate {
        throwExceptionIfPreconditionViolated()
        return _coordinateMedianLazyLoaded
    }

    /**
     * @return the maximal difference in Y/Latitude values
     *      between the coordinate with the smallest and the largest Y/Latitude values.
     */
    fun getMaxDifferenceForYNorthingLatitude(): Double {
        return _maxDiffLatitudesLazyLoaded
    }
    
    /**
     * @return the maximal difference in X/Longitude values
     *      between the coordinate with the smallest and the largest X/Longitude values.
     */
    fun getMaxDifferenceForXEastingLongitude(): Double {
        return _maxDiffLongitudesLazyLoaded
    }
    
    // Above: public methods
    // ----------------------------------------------------------
    // Below: private methods/properties
    
    private fun throwExceptionIfPreconditionViolated() {
        if (!isStatisticsAvailable()) {
            throw RuntimeException("Precondition violated. No statistics available")
        }
    }
    
    private fun getMaxDiff(values: List<Double>): Double {
        if (values.size < 2) {
            return 0.0
        } else {
            val sortedValues = values.sorted()
            val diff = Math.abs(sortedValues.get(0) - sortedValues.get(sortedValues.size - 1))
            return diff
        }
    }
    
    private val _sucessfulCoordinatesLazyLoaded: List<CrsCoordinate> by lazy {
        results.filter { it.isSuccess }.map { it.outputCoordinate }
    }

    private val _longitudesLazyLoaded: List<Double> by lazy {
        _sucessfulCoordinatesLazyLoaded.map { it.xEastingLongitude }
    }

    private val _latitudesLazyLoaded: List<Double> by lazy {
        _sucessfulCoordinatesLazyLoaded.map { it.yNorthingLatitude }
    }

    private val _maxDiffLatitudesLazyLoaded: Double by lazy {
        getMaxDiff(_latitudesLazyLoaded)
    }

    private val _maxDiffLongitudesLazyLoaded: Double by lazy {
        getMaxDiff(_longitudesLazyLoaded)
    }

    private val _coordinateMedianLazyLoaded: CrsCoordinate by lazy {
        val lon = MedianValueUtility.getMedianValue(_longitudesLazyLoaded)
        val lat = MedianValueUtility.getMedianValue(_latitudesLazyLoaded)
        val coord = createFromXEastingLongitudeAndYNorthingLatitude(lon, lat, _sucessfulCoordinatesLazyLoaded.get(0).crsIdentifier)
        coord
    }

    private val _coordinateAverageLazyLoaded: CrsCoordinate by lazy {
        createFromXEastingLongitudeAndYNorthingLatitude(_longitudesLazyLoaded.average(), _latitudesLazyLoaded.average(), _sucessfulCoordinatesLazyLoaded.get(0).crsIdentifier)
    }

    internal companion object {
        /**
         * This method is not intended for public use from client code.
         */
        @JvmStatic
        fun _createCrsTransformationResultStatistic(
            results: List<CrsTransformationResult>
        ): CrsTransformationResultStatistic {
            return CrsTransformationResultStatistic(results)
        }
    }

}