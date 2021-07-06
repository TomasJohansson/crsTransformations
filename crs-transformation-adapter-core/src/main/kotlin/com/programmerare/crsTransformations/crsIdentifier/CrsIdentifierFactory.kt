/**
 * From Java code this will look like a class 'CrsIdentifierFactory'
 * with public static factory methods.
 * The Java class name: com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory  
 * 
 * From Kotlin code the methods are available as package level functions
 * and each function can be imported as if it would be a class, for example like this:  
 *  import com.programmerare.crsTransformations.crsIdentifier.createFromEpsgNumber
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-adapter-core" project is licensed with MIT.
 * Other subprojects may be released with other licenses e.g. LGPL or Apache License 2.0.
 * Please find more information in the license file at the root directory of each subproject
 * (e.g. the subprojects "crs-transformation-adapter-impl-geotools" , "crs-transformation-adapter-impl-proj4j" and so on)
 */
@file:JvmName("CrsIdentifierFactory")
package com.programmerare.crsTransformations.crsIdentifier

// The reason for having CrsIdentifier and this CrsIdentifierFactory
// in a package of its own is to avoid "polluting" the base
// package from lots of package level function defined in this file
// when using Kotlin code.
// (when using Java we do not see that problem but rather a class
//   CrsIdentifierFactory with all these function as static method in that class)

private const val EPSG_PREFIX_UPPERCASED = "EPSG:"
private const val LENGTH_OF_EPSG_PREFIX = EPSG_PREFIX_UPPERCASED.length

// The crsCode string will become trimmed, and if it is
// "epsg" (or e.g. something like "ePsG") then it will be uppercased i.e. "EPSG"

/**
 * @param crsCode a string which should begin with "EPSG:4326" 
 *  (although it is case insensitive and it is also acceptable 
 *   with leading white spaces and e.g. " ePsG:4326" but 
 *   it will then be internally canonicalized to "EPSG:4326")
 *   An exception is thrown if an EPSG number is zero or negative,
 *   or if the input string is null or only whitespace.
 */
fun createFromCrsCode(crsCode: String): CrsIdentifier {
    // these two default values will be used, unless the crsCode parameter is an EPSG string
    var epsgNumber = 0
    var isEpsgCode = false

    if(crsCode.isNullOrBlank()) {
        throw java.lang.IllegalArgumentException("CRS code must be non-empty")
    }
    var crsIdentifierCode = crsCode.trim() // but does not uppercase here (unless EPSG below)

    if(crsIdentifierCode.uppercase().startsWith(EPSG_PREFIX_UPPERCASED)) {
        val nonEpsgPartOfString = crsIdentifierCode.substring(LENGTH_OF_EPSG_PREFIX);
        val epsgNumberOrNull = nonEpsgPartOfString.toIntOrNull()
        if(epsgNumberOrNull != null) {
            epsgNumber = epsgNumberOrNull
            getValidEpsgNumberOrThrowIllegalArgumentExceptionMessageIfNotValid(epsgNumber)
            isEpsgCode = true
            crsIdentifierCode = crsIdentifierCode.uppercase()
        }
    }
    return CrsIdentifier._internalCrsFactory(crsIdentifierCode, isEpsgCode, epsgNumber)
}

/**
 * Creates a CrsIdentifier from a positive integer.
 * The only validation constraints are that the integer must be positive (and not null).
 * An exception is thrown if the input number is null or zero or negative.
 * The reason to allow null in the Kotlin method signature is to provide java clients
 * with better error messages and "IllegalArgumentException" instead of "NullPointerException" 
 * @param epsgNumber an EPSG number, 
 *      for example 4326 for the frequently used coordinate reference system WGS84. 
 */
fun createFromEpsgNumber(epsgNumber: Int?): CrsIdentifier {
    val validatedEpsgNumber = getValidEpsgNumberOrThrowIllegalArgumentExceptionMessageIfNotValid(epsgNumber)
    return CrsIdentifier._internalCrsFactory(
        crsCode = EPSG_PREFIX_UPPERCASED + validatedEpsgNumber,
        isEpsgCode = true,
        epsgNumber = validatedEpsgNumber
    )
}

private fun getValidEpsgNumberOrThrowIllegalArgumentExceptionMessageIfNotValid(epsgNumber: Int?): Int {
    if(epsgNumber == null) {
        throw IllegalArgumentException("EPSG number must not be null")    
    }
    val epsgNumberNotNull: Int = epsgNumber
    
    if(epsgNumberNotNull <= 0) {
        throw IllegalArgumentException("EPSG number must not be non-positive but was: " + epsgNumberNotNull)
    }
    return epsgNumberNotNull
}