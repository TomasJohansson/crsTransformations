import os,glob,sys
lib_directory = "../ivy_dependencies/lib/"
for jar in glob.glob(os.path.join(lib_directory,'*.jar')):
    sys.path.append(jar)

# print sys.path # this includes '../ivy_dependencies/lib/gt-referencing-28.0.jar' which includes the class 'org.geotools.referencing.factory.epsg.CartesianAuthorityFactory' (mentioned further down because of problems with GeoTools)

# The Jython code in this file is tested with Jython 2.7.3 (for Windows 10 64 bit, using adapterVersion 2.0.1 in sample_code\ivy_dependencies\ivysettings.xml)
# (and has previously been tested with Jython 2.7.2 for Linux Ubuntu 20.04, using adapterVersion 2.0.0 in sample_code\ivy_dependencies\ivysettings.xml).
# Note that it is assumed that the needed jar files has been retrieved into the above directory "../ivy_dependencies/lib/".
# Those jar files (60 files and totally around 20 MB) can be retrieved from the internet with these command prompt commands from the root directory of this git repository:
# (assuming that Ant and Ivy are installed, and for further details, read comments in the file "ivy_dependencies/build.xml")
#   cd sample_code/ivy_dependencies
#   ant

# Then if you have installed Jython try to execute this script like this (with command prompt commands from the root directory of this git repository):
#   cd sample_code/jython
#   jython CrsTransformation.py

# "crs-transformation-constants-10.027.jar" is one of the jar files that are appended to the path above 
# in the above iteration. That jar file contains the below imported class "EpsgNumber"
try:
    from com.programmerare.crsConstants.constantsByAreaNameNumber.v10_027 import EpsgNumber
except ImportError:
    raise ImportError("The module could not be imported. Maybe you have not run 'ant' from the directory '../ivy_dependencies/' (with the jar-file for 'ivy' in the lib folder of ant)")
from com.programmerare.crsTransformations.coordinate import CrsCoordinateFactory
from com.programmerare.crsTransformations.crsIdentifier import CrsIdentifierFactory
from com.programmerare.crsTransformations import CrsTransformationAdapterLeafFactory
from com.programmerare.crsTransformations.compositeTransformations import CrsTransformationAdapterCompositeFactory
from com.programmerare.crsTransformations.compositeTransformations import CrsTransformationAdapterWeight
from com.programmerare.crsTransformationAdapterGooberCTL import CrsTransformationAdapterGooberCTL
from com.programmerare.crsTransformationAdapterGeoTools import CrsTransformationAdapterGeoTools
from com.programmerare.crsTransformationAdapterProj4J import CrsTransformationAdapterProj4J
from com.programmerare.crsTransformationAdapterProj4jLocationtech import CrsTransformationAdapterProj4jLocationtech
from com.programmerare.crsTransformationAdapterNgaGeoInt import CrsTransformationAdapterNgaGeoInt
from com.programmerare.crsTransformationAdapterOrbisgisCTS import CrsTransformationAdapterOrbisgisCTS

class CrsTransformation:
    def main(self):
        inputCoordinate = CrsCoordinateFactory.latLon(59.330231, 18.059196) #  Implicit/Default CrsIdentifier for latitude/longitude: EpsgNumber.WORLD__WGS_84__4326
        targetCrsIdentifier = CrsIdentifierFactory.createFromEpsgNumber(EpsgNumber.SWEDEN__SWEREF99_TM__3006)        

        print "Implicit/default EpsgNumber for latitude/longitude: %s" % (inputCoordinate.crsIdentifier.epsgNumber) # 4326
        print "InputCoordinate: %s" % (inputCoordinate)
        print "TargetCrsIdentifier: %s" % (targetCrsIdentifier)
        print "Coordinate transformations from EPSG %s to EPSG %s" % (EpsgNumber.WORLD__WGS_84__4326, EpsgNumber.SWEDEN__SWEREF99_TM__3006)

        crsTransformationResults = self.__transform(inputCoordinate, targetCrsIdentifier, self.__getAllCrsTransformationAdapters())

        for result in crsTransformationResults:
            # Note that GeoTools fails with the following message (for both Windows 10 and Linux Ubuntu 20.04):
            #   org.geotools.util.factory WARNING Can't load a service for category "CRSAuthorityFactory". Cause is "ServiceConfigurationError: org.opengis.referencing.crs.CRSAuthorityFactory: Provider org.geotools.referencing.factory.epsg.CartesianAuthorityFactory could not be instantiated".
            # but the above mentioned class 'CartesianAuthorityFactory' is located in the jar file 'gt-referencing-28.0.jar'
            # which is indeed included (see the 'print sys.path' further up in this file)
            # and it is also working from the jruby code (in the parallell directory "../jruby") which is using the same jar file in the directory "../ivy_dependencies/lib/"
            if result.isSuccess():
                outputCoordinate = result.outputCoordinate
                crs = result.crsTransformationAdapterResultSource
                implementationType = crs.implementationType
                version = crs.versionOfImplementationAdapteeLibrary
                print "%s %s  : X / Y ===> %s / %s" % (implementationType, version, outputCoordinate.x, outputCoordinate.y)
                # print "%s  : X / Y ===> %s / %s" % (crs.adapteeType, outputCoordinate.x, outputCoordinate.y) # the old output

        print "Short names of all Leaf implementations:"
        for leaf in CrsTransformationAdapterLeafFactory.getInstancesOfAllKnownAvailableImplementations():
            print leaf.shortNameOfImplementation

            # Below is the printed output from the above code in this main method:
            #       (at least the essential parts, but some rows were removed such as e.g. "hsqldb.db.HSQLDB6BD103EBD8.ENGINE INFO dataFileCache open start" 
            #        and also some of the outputs related to the above mentioned geotools problem)

            #   Implicit/default EpsgNumber for latitude/longitude: 4326
            #   InputCoordinate: Coordinate(xEastingLongitude=18.059196, yNorthingLatitude=59.330231, crsIdentifier=CrsIdentifier(crsCode='EPSG:4326', isEpsgCode=true, epsgNumber=4326))
            #   TargetCrsIdentifier: CrsIdentifier(crsCode='EPSG:3006', isEpsgCode=true, epsgNumber=3006)
            #   Coordinate transformations from EPSG 4326 to EPSG 3006
            #   org.geotools.util.factory WARNING Can't load a service for category "CRSAuthorityFactory". Cause is "ServiceConfigurationError: org.opengis.referencing.crs.CRSAuthorityFactory: Provider org.geotools.referencing.factory.epsg.CartesianAuthorityFactory could not be instantiated".
            #   LEAF_PROJ4J 0.1.0  : X / Y ===> 674032.357326 / 6580821.99112
            #   LEAF_PROJ4J_LOCATIONTECH 1.2.2  : X / Y ===> 674032.357326 / 6580821.99112
            #   LEAF_NGA_GEOINT 4.3.0  : X / Y ===> 674032.357326 / 6580821.99112
            #   LEAF_ORBISGIS 1.6.0  : X / Y ===> 674032.357326 / 6580821.99112
            #   LEAF_GOOBER 1.1  : X / Y ===> 674032.357 / 6580821.991
            #   COMPOSITE_MEDIAN 2.0.0  : X / Y ===> 674032.357326 / 6580821.99112
            #   COMPOSITE_AVERAGE 2.0.0  : X / Y ===> 674032.357261 / 6580821.9911
            #   COMPOSITE_FIRST_SUCCESS 2.0.0  : X / Y ===> 674032.357 / 6580821.991
            #   COMPOSITE_WEIGHTED_AVERAGE 2.0.0  : X / Y ===> 674032.357217 / 6580821.99108
            #   Short names of all Leaf implementations:
            #   GooberCTL
            #   NgaGeoInt
            #   GeoTools
            #   OrbisgisCTS
            #   Proj4jLocationtech
            #   Proj4J
    
    # "private" methods below with two underscores as prefix   https://www.geeksforgeeks.org/private-methods-in-python/

    def __weightedAverageCrsTransformationAdapter(self):
        crsTransformationAdapterWeights = [
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterProj4J(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterProj4jLocationtech(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterOrbisgisCTS(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterNgaGeoInt(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterGeoTools(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterGooberCTL(), 2.0)
        ]
        return CrsTransformationAdapterCompositeFactory.createCrsTransformationWeightedAverage(
            crsTransformationAdapterWeights
        )

    def __getAllCrsTransformationAdapters(self): # List<CrsTransformationAdapter>
        return [
            CrsTransformationAdapterProj4J(),
            CrsTransformationAdapterProj4jLocationtech(),
            CrsTransformationAdapterNgaGeoInt(),
            CrsTransformationAdapterOrbisgisCTS(),
            CrsTransformationAdapterGooberCTL(),            
            CrsTransformationAdapterGeoTools(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationAverage(),
            CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess(),
            self.__weightedAverageCrsTransformationAdapter()
        ]

    def __transform(
        self,
        inputCoordinate, # CrsCoordinate,
        targetCrsIdentifier, # CrsIdentifier,
        allCrsTransformationAdapters #  List<CrsTransformationAdapter>
    ): # return type: List<CrsTransformationResult>
        # In Python 2, the map() function retuns a list. In Python 3, however, the function returns a map object which is a generator object.

        # The current version of Jython is 2.7.2 (as of april 2020, https://www.jython.org/download)
        return map(lambda crsTransformationAdapter: crsTransformationAdapter.transform(inputCoordinate, targetCrsIdentifier), allCrsTransformationAdapters)


if __name__ == "__main__":
    crsTransformation = CrsTransformation()
    crsTransformation.main()
