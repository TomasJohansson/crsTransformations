package com.programmerare.crsTransformationAdapterGooberCTL

import com.github.goober.coordinatetransformation.Position
import com.github.goober.coordinatetransformation.positions.RT90Position.RT90Projection
import com.github.goober.coordinatetransformation.positions.RT90Position
import com.github.goober.coordinatetransformation.positions.SWEREF99Position
import com.github.goober.coordinatetransformation.positions.SWEREF99Position.SWEREFProjection
import com.github.goober.coordinatetransformation.positions.WGS84Position
import com.programmerare.crsTransformations.CrsTransformationAdapteeType
import com.programmerare.crsTransformations.CrsTransformationAdapter
import com.programmerare.crsTransformations.CrsTransformationAdapterBaseLeaf
import com.programmerare.crsTransformations.CrsTransformationImplementationType
import com.programmerare.crsTransformations.coordinate.CrsCoordinate
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier
import com.programmerare.crsTransformations.coordinate.createFromYNorthingLatitudeAndXEastingLongitude
import java.util.*

// " goober/coordinate-transformation-library "
// https://github.com/goober/coordinate-transformation-library

/**
 * Implementation of the interface CrsTransformationAdapter.
 * See the documentation of the interface.
 * @see com.programmerare.crsTransformations.CrsTransformationAdapter
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-code-generation" project is licensed with MIT.
 * The code in the "crs-transformation-adapter-impl-goober" project
 * is licensed with Apache License 2.0 i.e. the same license as the adaptee library goober/coordinate-transformation-library.
 */
class CrsTransformationAdapterGooberCTL : CrsTransformationAdapterBaseLeaf(), CrsTransformationAdapter {
    
    override protected fun transformHook(
        inputCoordinate: CrsCoordinate,
        crsIdentifierForOutputCoordinateSystem: CrsIdentifier
    ): CrsCoordinate {
        throwIllegalArgumentExceptionIfUnvalidCoordinateOrCrs(inputCoordinate, crsIdentifierForOutputCoordinateSystem)
        val epsgNumberForInputCoordinateSystem = inputCoordinate.crsIdentifier.epsgNumber
        var positionToReturn: Position? = null

        // shorter names below for readibility purpose (lots os usages further down)
        val input = epsgNumberForInputCoordinateSystem
        val output = crsIdentifierForOutputCoordinateSystem.epsgNumber
        // "Int" is the data type for the above input and output
        // and below in the if statements they are used with extension functions
        // for semantic reasons i.e. readability.
        if(input.isRT90() && output.isWgs84()) { // procedural alternative: "if(isRT90(input) && isWgs84(output))"
            val rt90Position = RT90Position(inputCoordinate.yNorthingLatitude, inputCoordinate.xEastingLongitude, rt90Projections[input])
            positionToReturn = rt90Position.toWGS84()

        } else if(input.isWgs84() && output.isRT90()) {
            val wgs84Position = WGS84Position(inputCoordinate.yNorthingLatitude, inputCoordinate.xEastingLongitude)
            positionToReturn = RT90Position(wgs84Position, rt90Projections[output])

        } else if(input.isSweref99() && output.isWgs84()) {
            val sweREF99Position = SWEREF99Position(inputCoordinate.yNorthingLatitude, inputCoordinate.xEastingLongitude, sweREFProjections[input])
            positionToReturn = sweREF99Position.toWGS84()

        } else if(input.isWgs84() && output.isSweref99()) {
            val wgs84Position = WGS84Position(inputCoordinate.yNorthingLatitude, inputCoordinate.xEastingLongitude)
            positionToReturn = SWEREF99Position(wgs84Position, sweREFProjections[output])

        }

        if (positionToReturn != null) {
            return createFromYNorthingLatitudeAndXEastingLongitude(yNorthingLatitude = positionToReturn.latitude, xEastingLongitude = positionToReturn.longitude, crsIdentifier = crsIdentifierForOutputCoordinateSystem)
        } else if (
            // not direct support for transforming directly between SWEREF99 and RT90
            // but can do it by first transforming to WGS84 and then to the other
            (input.isSweref99() && output.isRT90())
            ||
            (input.isRT90() && output.isSweref99())
            || // transform between different Sweref systems
            (input.isSweref99() && output.isSweref99())
            || // transform between different RT90 systems
            (input.isRT90() && output.isRT90())
        ) {
            // first transform to WGS84
            val wgs84Coordinate = transformToCoordinate(inputCoordinate, WGS84)
            // then transform from WGS84
            return transformToCoordinate(wgs84Coordinate, crsIdentifierForOutputCoordinateSystem)
        } else {
            throw IllegalArgumentException("Unsupported transformation from $epsgNumberForInputCoordinateSystem to ${crsIdentifierForOutputCoordinateSystem.crsCode}")
        }
    }

    private fun throwIllegalArgumentException(crsIdentifier: CrsIdentifier) {
        throw IllegalArgumentException("Unsupported CRS: ${crsIdentifier.crsCode}")
    }

    private fun Int.isWgs84(): Boolean {
        return this == WGS84
    }

    private fun Int.isSweref99(): Boolean {
        return sweREFProjections.containsKey(this)
    }

    private fun Int.isRT90(): Boolean {
        return rt90Projections.containsKey(this)
    }

    private companion object {
        private val WGS84 = 4326

        private val rt90Projections = HashMap<Int, RT90Position.RT90Projection>()
        private val sweREFProjections = HashMap<Int, SWEREFProjection>()

        init {
            // Below some EPSG numbers are hardcoded.
            // If those numbers would be used in more than one place in this file or the module,
            // then constants would definitely have been preferred,
            // but adding 20 constants for only one usage is not very motivated.
            // Another option would have been to reuse the already existing constants in the module "crs-transformation-constants" i.e. constants like this:
            // com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9.EpsgNumber.SWEDEN__SWEREF99_TM__3006
            // However, that would introduce a dependency to a module with LOTS OF constants just to use this few values below.
            
            // http://spatialreference.org/ref/?search=rt90
            rt90Projections.put(3019, RT90Projection.rt90_7_5_gon_v)    // EPSG:3019: RT90 7.5 gon V		https://epsg.io/3019
            rt90Projections.put(3020, RT90Projection.rt90_5_0_gon_v)    // EPSG:3020: RT90 5 gon V			https://epsg.io/3020
            rt90Projections.put(3021, RT90Projection.rt90_2_5_gon_v)    // EPSG:3021: RT90 2.5 gon V		https://epsg.io/3021
            rt90Projections.put(3022, RT90Projection.rt90_0_0_gon_v)    // EPSG:3022: RT90 0 gon			https://epsg.io/3022
            rt90Projections.put(3023, RT90Projection.rt90_2_5_gon_o)    // EPSG:3023: RT90 2.5 gon O		https://epsg.io/3023
            rt90Projections.put(3024, RT90Projection.rt90_5_0_gon_o)    // EPSG:3024: RT90 5 gon O			https://epsg.io/3024

            // http://spatialreference.org/ref/?search=sweref
            sweREFProjections.put(3006, SWEREFProjection.sweref_99_tm)       // EPSG:3006: SWEREF99 TM		https://epsg.io/3006
            sweREFProjections.put(3007, SWEREFProjection.sweref_99_12_00)    // EPSG:3007: SWEREF99 12 00	https://epsg.io/3007
            sweREFProjections.put(3008, SWEREFProjection.sweref_99_13_30)    // EPSG:3008: SWEREF99 13 30	https://epsg.io/3008
            sweREFProjections.put(3009, SWEREFProjection.sweref_99_15_00)    // EPSG:3009: SWEREF99 15 00	https://epsg.io/3009
            sweREFProjections.put(3010, SWEREFProjection.sweref_99_16_30)    // EPSG:3010: SWEREF99 16 30	https://epsg.io/3010
            sweREFProjections.put(3011, SWEREFProjection.sweref_99_18_00)    // EPSG:3011: SWEREF99 18 00	https://epsg.io/3011
            sweREFProjections.put(3012, SWEREFProjection.sweref_99_14_15)    // EPSG:3012: SWEREF99 14 15	https://epsg.io/3012
            sweREFProjections.put(3013, SWEREFProjection.sweref_99_15_45)    // EPSG:3013: SWEREF99 15 45	https://epsg.io/3013
            sweREFProjections.put(3014, SWEREFProjection.sweref_99_17_15)    // EPSG:3014: SWEREF99 17 15	https://epsg.io/3014
            sweREFProjections.put(3015, SWEREFProjection.sweref_99_18_45)    // EPSG:3015: SWEREF99 18 45	https://epsg.io/3015
            sweREFProjections.put(3016, SWEREFProjection.sweref_99_20_15)    // EPSG:3016: SWEREF99 20 15	https://epsg.io/3016
            sweREFProjections.put(3017, SWEREFProjection.sweref_99_21_45)    // EPSG:3017: SWEREF99 21 45	https://epsg.io/3017
            sweREFProjections.put(3018, SWEREFProjection.sweref_99_23_15)    // EPSG:3018: SWEREF99 23 15	https://epsg.io/3018
        }
    }


    // ----------------------------------------------------------
    override fun getAdapteeType() : CrsTransformationAdapteeType {
        return CrsTransformationAdapteeType.LEAF_GOOBER_1_1
    }

    override fun getImplementationType() : CrsTransformationImplementationType {
        return CrsTransformationImplementationType.LEAF_GOOBER
    }
    
    // The purpose of the method below is to use it in test code
    // for detecting upgrades to a new version (and then update the above method returned enum value)
    // Future failure will be a reminder to update the above enum value
    protected override fun getNameOfJarFileOrEmptyString(): String {
        // Doing the code in small steps below to figure out where the problem is when it does not work from Jython
        // (though it does work from Java, Kotlin, Scala, Groovy and JRuby)
        val clazz = WGS84Position::class
        super.debug("goober clazz: " + clazz) // "goober clazz: class com.github.goober.coordinatetransformation.positions.WGS84Position (Kotlin reflection is not available)"
        val javaClazz = clazz.java
        debug("goober javaClazz: " + javaClazz) // "goober javaClazz: class com.github.goober.coordinatetransformation.positions.WGS84Position"
        val protectionDomain = javaClazz.protectionDomain
        debug("goober protectionDomain: " + protectionDomain) // "goober protectionDomain: ProtectionDomain  (null <no signer certificates>)"
        return super.getNameOfJarFileFromProtectionDomain(protectionDomain)
    }

    // ----------------------------------------------------------

    private fun throwIllegalArgumentExceptionIfUnvalidCoordinateOrCrs(
        inputCoordinate: CrsCoordinate,
        crsIdentifierForOutputCoordinateSystem: CrsIdentifier
    ) {
        if(!inputCoordinate.crsIdentifier.isEpsgCode) {
            throwIllegalArgumentException(inputCoordinate.crsIdentifier)
        }
        if(!crsIdentifierForOutputCoordinateSystem.isEpsgCode) {
            throwIllegalArgumentException(crsIdentifierForOutputCoordinateSystem)
        }
        // TODO: maybe more validation, for example validate coordinates to be reasonable i.e. within Sweden since this is an implementation with only coordinate systems used in Sweden  
    }    
}
