import os,glob,sys
lib_directory = "../ivy_dependencies/lib/"
for jar in glob.glob(os.path.join(lib_directory,'*.jar')):
    sys.path.append(jar)

# The Jython code in this file is tested with Jython 2.7.2 (for Windows 10 64bit).
# Note that it is assumed that the needed jar files has been retrieved into the above directory "../ivy_dependencies/lib/".
# Those jar files (46 files and totally around 20 MB) can be retrieved from the internet with these command prompt commands from the root directory of this git repository:
# (assuming that Ant and Ivy are installed, and for further details, read comments in the file "ivy_dependencies/build.xml")
#   cd sample_code/ivy_dependencies
#   ant

# Then if you have installed Jython try to execute this script like this (with command prompt commands from the root directory of this git repository):
#   cd sample_code/jython
#   jython CrsTransformation.py

# "crs-transformation-constants-9.8.9.jar" is one of the jar files that are appended to the path above 
# in the above iteration. That jar file contains the below imported class "EpsgNumber"
from com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9 import EpsgNumber

from com.programmerare.crsTransformations.coordinate import CrsCoordinateFactory
from com.programmerare.crsTransformations.crsIdentifier import CrsIdentifierFactory
from com.programmerare.crsTransformations.compositeTransformations import CrsTransformationAdapterCompositeFactory
from com.programmerare.crsTransformations.compositeTransformations import CrsTransformationAdapterWeight
from com.programmerare.crsTransformationAdapterGooberCTL import CrsTransformationAdapterGooberCTL
from com.programmerare.crsTransformationAdapterGeoTools import CrsTransformationAdapterGeoTools
from com.programmerare.crsTransformationAdapterProj4J import CrsTransformationAdapterProj4J
from com.programmerare.crsTransformationAdapterGeoPackageNGA import CrsTransformationAdapterGeoPackageNGA
from com.programmerare.crsTransformationAdapterOrbisgisCTS import CrsTransformationAdapterOrbisgisCTS

class CrsTransformation:
    def main(self):
        inputCoordinate = CrsCoordinateFactory.latLon(59.330231, 18.059196) #  Implicit/Default CrsIdentifier for latitude/longitude: EpsgNumber.WORLD__WGS_84__4326
        targetCrsIdentifier = CrsIdentifierFactory.createFromEpsgNumber(EpsgNumber.SWEDEN__SWEREF99_TM__3006)        

        crsTransformationResults = self.__transform(inputCoordinate, targetCrsIdentifier, self.__getAllCrsTransformationAdapters())
        for result in crsTransformationResults:
            nameOfCrsTransformationAdapterImplementation = result.crsTransformationAdapterResultSource.adapteeType
            outputCoordinate = result.outputCoordinate
            print "%s : X / Y ===> %s / %s" % (nameOfCrsTransformationAdapterImplementation, outputCoordinate.x, outputCoordinate.y)

        # Below is the printed output from the above code in this main method:
        #   LEAF_PROJ4J_0_1_0 : X / Y ===> 674032.357326 / 6580821.99112
        #   LEAF_NGA_GEOPACKAGE_3_1_0 : X / Y ===> 674032.357326 / 6580821.99112
        #   LEAF_ORBISGIS_1_5_1 : X / Y ===> 674032.357326 / 6580821.99112
        #   LEAF_GOOBER_1_1 : X / Y ===> 674032.357 / 6580821.991
        #   LEAF_GEOTOOLS_20_0 : X / Y ===> 674032.357177 / 6580821.99437
        #   COMPOSITE_MEDIAN : X / Y ===> 674032.357326 / 6580821.99112
        #   COMPOSITE_AVERAGE : X / Y ===> 674032.357231 / 6580821.99175
        #   COMPOSITE_FIRST_SUCCESS : X / Y ===> 674032.357 / 6580821.991
        #   COMPOSITE_WEIGHTED_AVERAGE : X / Y ===> 674032.357193 / 6580821.99162



    # "private" methods below with two underscores as prefix   https://www.geeksforgeeks.org/private-methods-in-python/

    def __weightedAverageCrsTransformationAdapter(self):
        crsTransformationAdapterWeights = [
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterProj4J(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterOrbisgisCTS(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterGeoPackageNGA(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterGeoTools(), 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterGooberCTL(), 2.0)
        ]
        return CrsTransformationAdapterCompositeFactory.createCrsTransformationWeightedAverage(
            crsTransformationAdapterWeights
        )

    def __getAllCrsTransformationAdapters(self): # List<CrsTransformationAdapter>
        return [
            CrsTransformationAdapterProj4J(),
            CrsTransformationAdapterGeoPackageNGA(),
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
