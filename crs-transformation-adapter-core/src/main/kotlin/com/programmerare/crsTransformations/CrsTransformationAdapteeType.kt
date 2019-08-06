package com.programmerare.crsTransformations

/**
 * 
 * Enumeration type returned from a method in the adapter interface.
 * 
 * The purpose is to make it easier to see from where a result originated
 * when iterating the 'leaf' adapter results in a 'composite' object.
 * 
 * The names of the leafs in the enumeration includes information
 * about the version number for the adaptee library it represents.
 * 
 * The purpose of this enum type is NOT to use it anywhere for specifying which implementation you want to use,
 * e.g. it is NOT possible to choose an older version of a library such as GeoTools
 * by using the enum value LEAF_GEOTOOLS_20_0 instead of LEAF_GEOTOOLS_20_2.
 * The latest version (with the highest version number in the enum name) will be used,
 * and the older enum is only kept for avoiding to cause compiling problems if someone
 * would be using it in for example if/switch statements for checking which version is used.
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-adapter-core" project is licensed with MIT.
 * Other subprojects may be released with other licenses e.g. LGPL or Apache License 2.0.
 * Please find more information in the license file at the root directory of each subproject
 * (e.g. the subprojects "crs-transformation-adapter-impl-geotools" , "crs-transformation-adapter-impl-proj4j" and so on)
 */
@Deprecated("The type CrsTransformationAdapteeType was only used for getting information about which implementation was used, e.g. an enum with a name revealing the version number. Now instead use the methods getShortNameOfImplementation (existing since before) and the new method CrsTransformationAdapter.getVersionOfImplementationAdapteeLibrary()")
enum class CrsTransformationAdapteeType {

    // TODO add the tags @since with version number for the constant values in this class
    
    // After an upgrade, test code should help to remind about updating the enum values.
    // For exampl, there is test code that retrives the name of the jar file for 
    // a class within geotools library, and the testcode verifies for example 
    // the file name "gt-api-20.0.jar" but if the file name would instead be  
    // for example "gt-api-20.1.jar" then the test would fail to help reminding 
    // that a new enum should be added

    /**
     * DEPRECATED version for the adaptee library !
     * ( this constant was published in release 1.0.0 )
     */
    @Deprecated("Use 'LEAF_GEOTOOLS_21_0' instead since the currently used GeoTools library is version 21.0")
    LEAF_GEOTOOLS_20_0,

//    /**
//     * DEPRECATED version for the adaptee library !
//     */
//    @Deprecated("Use 'LEAF_GEOTOOLS_21_0' instead since the currently used GeoTools library is version 21.0")
//    LEAF_GEOTOOLS_20_2,

//    /**
//     * DEPRECATED version for the adaptee library !
//     */
//    @Deprecated("Use 'LEAF_GEOTOOLS_21_0' instead since the currently used GeoTools library is version 21.0")    
//    LEAF_GEOTOOLS_20_3,

    @Deprecated("Use 'LEAF_GEOTOOLS_21_2' instead since the currently used GeoTools library is version 21.2")
    LEAF_GEOTOOLS_21_0, // "old"

    @Deprecated("Use 'LEAF_GEOTOOLS_21_2' instead since the currently used GeoTools library is version 21.2")
    LEAF_GEOTOOLS_21_1,    
    
    /**
     * Maven version for the adaptee library:
     * "org.geotools:gt-main:21.2"
     */
    LEAF_GEOTOOLS_21_2,
    
    /**
     * Maven version for the adaptee library:
     * "com.github.goober:coordinate-transformation-library:1.1"
     */
    LEAF_GOOBER_1_1,

    /**
     * DEPRECATED version for the adaptee library !
     * ( this constant was published in release 1.0.0 )
     */
    @Deprecated("Use 'LEAF_ORBISGIS_1_5_2' instead since the currently used OrbisGIS/CTS library is version 1.5.2")
    LEAF_ORBISGIS_1_5_1,

    /**
     * Maven version for the adaptee library:
     * "org.orbisgis:cts:1.5.2"
     */
    LEAF_ORBISGIS_1_5_2,

    /**
     * DEPRECATED version for the adaptee library !
     * ( this constant was published in release 1.0.0 )
     */
    @Deprecated("Use 'LEAF_NGA_GEOPACKAGE_3_2_0' instead since the currently used 'mil.nga.geopackage' library is version 3.2.0")
    LEAF_NGA_GEOPACKAGE_3_1_0,

    /**
     * Maven version for the adaptee library:
     * "mil.nga.geopackage:geopackage-core:3.2.0"
     */    
    LEAF_NGA_GEOPACKAGE_3_2_0,

    /**
     * Maven version for the adaptee library:  
     * "org.osgeo:proj4j:0.1.0"
     */
    LEAF_PROJ4J_0_1_0,

    // The above "leafs" are the real "adaptees"
    // and the below composite "adapters" are not true adapters

    // Maybe a version number for this crs-transformation library (e.g. suffix _1_0_0)
    // should be used as suffix for the below enum values ...
    // though questionable if that would be meaningful, while it can be more useful
    // for troubleshooting to make it easier to figure out exactly which
    // version of a leaf adaptee is causing a certain transformation

    /**
     * Represents a composite which returns a result with
     * longitude and latitude being the median of the 'leafs'.
     */
    COMPOSITE_MEDIAN,

    /**
     * Represents a composite which returns a result with
     * longitude and latitude being the average of the 'leafs'.
     */
    COMPOSITE_AVERAGE,

    /**
     * Represents a composite which returns a result with
     * longitude and latitude being a weighted average of the 'leafs'.
     * 
     * The implementation will try to use results from all 'leaf' adapters
     * by calculating the resulting coordinate using weights
     * which must have been provided to the composite object when it was constructed.
     */
    COMPOSITE_WEIGHTED_AVERAGE,


    /**
     * Represents a composite which returns a result with
     * longitude and latitude being the first
     * succesful result for a 'leaf'.
     * 
     * When a result from a 'leaf' is considered as (seem to be) succesful
     * then no more leafs will be used for the transformation.
     * 
     * In other words, the number of results will always
     * be zero or one, unlike the median and average (or weighted) composites
     * which can have many results from multiple 'leafs' (adapter/adaptee implementations).
     */
    COMPOSITE_FIRST_SUCCESS,


    /**
     * A default value for leafs in a base class but this value
     * should normally not occur since it should be overridden in
     * leaf implementations.
     */
    UNSPECIFIED_LEAF,

    /**
     * A default value for composites in a base class but this value
     * should normally not occur since it should be overridden in
     * composite implementations.
     */
    UNSPECIFIED_COMPOSITE,

    /**
     * A default value for adapters in a base class but this value
     * should normally not occur since it should be overridden in
     * subclass implementations.
     */
    UNSPECIFIED;
}