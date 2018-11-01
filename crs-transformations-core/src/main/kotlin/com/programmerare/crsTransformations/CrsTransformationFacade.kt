package com.programmerare.crsTransformations

/**
 * The methods 'transformToCoordinate' can throw exception when transformation fails.
 * The methods 'transform' should always return an object,
 * but then you should check failures with 'TransformResult.isSuccess'.
 *
 */
interface CrsTransformationFacade {

    fun transformToCoordinate(
        inputCoordinate: Coordinate,
        epsgNumberForOutputCoordinateSystem: Int
    ): Coordinate

    fun transformToCoordinate(
        inputCoordinate: Coordinate,
        crsCodeForOutputCoordinateSystem: String
    ): Coordinate

    fun transformToCoordinate(
        inputCoordinate: Coordinate,
        crsIdentifierForOutputCoordinateSystem: CrsIdentifier
    ): Coordinate


    fun transform(
        inputCoordinate: Coordinate,
        epsgNumberForOutputCoordinateSystem: Int
    ): TransformResult

    fun transform(
        inputCoordinate: Coordinate,
        crsCodeForOutputCoordinateSystem: String
    ): TransformResult

    fun transform(
        inputCoordinate: Coordinate,
        crsIdentifierForOutputCoordinateSystem: CrsIdentifier
    ): TransformResult

    /**
     * Should normally simply return the class name,
     * but when implementing test doubles (e.g. Mockito stub)
     * it should be implemented by defining different names
     * to simulate that different classes (implementations)
     * should have different weights.
     */
    fun getNameOfImplementation(): String
}