package com.programmerare.crsTransformations

/**
 *
 * Enumeration type returned from a method in the adapter interface.
 *
 * The main purpose is to make it easier to see from where a result originated
 * when iterating the 'leaf' adapter results in a 'composite' object.
 *
 * The names of the leafs in the enumeration does NOT include information
 * about the version number for the adaptee library it represents.
 * (but the interface 'CrsTransformationAdapter' provides a method that returns the version) 
 *
 * @author Tomas Johansson ( http://programmerare.com )
 * The code in the "crs-transformation-adapter-core" project is licensed with MIT.
 * Other subprojects may be released with other licenses e.g. LGPL or Apache License 2.0.
 * Please find more information in the license file at the root directory of each subproject
 * (e.g. the subprojects "crs-transformation-adapter-impl-geotools" , "crs-transformation-adapter-impl-proj4j" and so on)
 */
enum class CrsTransformationImplementationType {
    
    LEAF_GEOTOOLS,
    
    LEAF_GOOBER,
    
    LEAF_ORBISGIS,

    LEAF_NGA_GEOINT,
    
    LEAF_PROJ4J,
    
    LEAF_PROJ4J_LOCATIONTECH,

    // The above "leafs" are representing the real "adaptees"
    // and the below composite "adapters" are not true adapters

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
