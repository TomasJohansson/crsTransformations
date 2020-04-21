name := "scala"

version := "0.1"

scalaVersion := "2.13.1"

val adapterVersion = "1.1.1"

// resolver for geotools (transitive dependency from "crs-transformation-adapter-impl-geotools"): 
resolvers += "osgeo" at "https://repo.osgeo.org/repository/release/" // https://docs.geotools.org/latest/userguide/build/maven/repositories.html

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test"

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-constants
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-constants" % "9.8.9"

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-core
//libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-core" % adapterVersion

// the above "core" library is not needed to include explicitly since it is a transitive dependency from the below adapter implementation libraries

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-proj4j
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-proj4j" % adapterVersion

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-proj4jlocationtech
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-proj4jlocationtech" % adapterVersion

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-nga
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-nga" % adapterVersion

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-orbisgis
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-orbisgis" % adapterVersion

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-goober
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-goober" % adapterVersion

// https://mvnrepository.com/artifact/com.programmerare.crs-transformation/crs-transformation-adapter-impl-geotools
libraryDependencies += "com.programmerare.crs-transformation" % "crs-transformation-adapter-impl-geotools" % adapterVersion

