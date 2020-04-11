name := "scala"

version := "0.1"

scalaVersion := "2.13.1"

// resolver for geotools (transitive dependency from "crs-transformation-adapter-impl-geotools"): 
resolvers += "osgeo" at "https://repo.osgeo.org/repository/release/" // https://docs.geotools.org/latest/userguide/build/maven/repositories.html

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test"


// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-constants
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-constants" % "9.5.4"


// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-core
//libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-core" % "1.0.0"

// the above "core" library is not needed to include explicitly since it is a transitive dependency from the below adapter implementation libraries

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-proj4j
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-proj4j" % "1.0.0"

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-nga
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-nga" % "1.0.0"

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-orbisgis
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-orbisgis" % "1.0.0"

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-goober
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-goober" % "1.0.0"

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-geotools
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-geotools" % "1.0.0"
