require 'java'
Dir["../ivy_dependencies/lib/*.jar"].each { |jar| 
    require jar
}

# The JRuby code in this file is tested with JRuby 9.2.11.1 (for Windows 10 64 bit).
# Note that it is assumed that the needed jar files has been retrieved into the above directory "../ivy_dependencies/lib/".
# Those jar files (46 files and totally around 20 MB) can be retrieved from the internet with these command prompt commands from the root directory of this git repository:
# (assuming that Ant and Ivy are installed, and for further details, read comments in the file "ivy_dependencies/build.xml")
#   cd sample_code/ivy_dependencies
#   ant

# Then if you have installed JRuby try to execute this script like this (with command prompt commands from the root directory of this git repository):
#   cd sample_code/jruby
#   jruby CrsTransformation.rb

# "crs-transformation-constants-9.8.9.jar" is one of the jar files that are "required" above 
# in the above "each" iteration. That jar file contains the below imported class "EpsgNumber"
java_import "com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9.EpsgNumber"

java_import "com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory"
java_import "com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory"
java_import "com.programmerare.crsTransformations.CrsTransformationAdapterLeafFactory"
java_import "com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory"
java_import "com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight"
java_import "com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J"
java_import "com.programmerare.crsTransformationAdapterProj4jLocationtech.CrsTransformationAdapterProj4jLocationtech"
java_import "com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools"
java_import "com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA"
java_import "com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL"
java_import "com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS"

class CrsTransformation

    def main
        inputCoordinate = CrsCoordinateFactory.latLon(59.330231, 18.059196) #  Implicit/Default CrsIdentifier for latitude/longitude: EpsgNumber.WORLD__WGS_84__4326
        targetCrsIdentifier = CrsIdentifierFactory.createFromEpsgNumber(EpsgNumber::SWEDEN__SWEREF99_TM__3006)

        puts "Implicit/default EpsgNumber for latitude/longitude: #{inputCoordinate.crsIdentifier.epsgNumber}" # 4326
        puts "InputCoordinate: #{inputCoordinate}"
        puts "TargetCrsIdentifier: #{targetCrsIdentifier}"
        puts "Coordinate transformations from EPSG #{EpsgNumber::WORLD__WGS_84__4326} to EPSG #{EpsgNumber::SWEDEN__SWEREF99_TM__3006}"
    
        crsTransformationResults = transform(inputCoordinate, targetCrsIdentifier, getAllCrsTransformationAdapters)

        crsTransformationResults.each { |result|
            outputCoordinate = result.outputCoordinate
            crs = result.crsTransformationAdapterResultSource
            puts "#{crs.implementationType} #{crs.versionOfImplementationAdapteeLibrary}  : X / Y ===> #{outputCoordinate.x} / #{outputCoordinate.y}"
        }        

        puts "Short names of all Leaf implementations:"
        allLeafs = CrsTransformationAdapterLeafFactory.getInstancesOfAllKnownAvailableImplementations()
        allLeafs.each { |leaf|
            puts leaf.shortNameOfImplementation
        }

        # Below is the printed output from the above code in this main method: 
        #    Implicit/default EpsgNumber for latitude/longitude: 4326
        #    InputCoordinate: Coordinate(xEastingLongitude=18.059196, yNorthingLatitude=59.330231, crsIdentifier=CrsIdentifier(crsCode='EPSG:4326', isEpsgCode=true, epsgNumber=4326))
        #    TargetCrsIdentifier: CrsIdentifier(crsCode='EPSG:3006', isEpsgCode=true, epsgNumber=3006)
        #    Coordinate transformations from EPSG 4326 to EPSG 3006
        #    LEAF_PROJ4J 0.1.0  : X / Y ===> 674032.357326444 / 6580821.991123579
        #    LEAF_PROJ4J_LOCATIONTECH 1.1.1  : X / Y ===> 674032.357326444 / 6580821.991123579
        #    LEAF_NGA_GEOPACKAGE 3.5.0  : X / Y ===> 674032.357326444 / 6580821.991123579
        #    LEAF_ORBISGIS 1.5.2  : X / Y ===> 674032.3573261796 / 6580821.991121078
        #    LEAF_GOOBER 1.1  : X / Y ===> 674032.357 / 6580821.991
        #    LEAF_GEOTOOLS 23.0  : X / Y ===> 674032.3571771547 / 6580821.994371211
        #    COMPOSITE_MEDIAN 1.1.1  : X / Y ===> 674032.3573263118 / 6580821.991123579
        #    COMPOSITE_AVERAGE 1.1.1  : X / Y ===> 674032.357247111 / 6580821.991643838
        #    COMPOSITE_FIRST_SUCCESS 1.1.1  : X / Y ===> 674032.357 / 6580821.991
        #    COMPOSITE_WEIGHTED_AVERAGE 1.1.1  : X / Y ===> 674032.3572118095 / 6580821.991551861
        #    Short names of all Leaf implementations:
        #    GooberCTL
        #    GeoPackageNGA
        #    GeoTools
        #    OrbisgisCTS
        #    Proj4jLocationtech
        #    Proj4J
    end

    private
    
    def weightedAverageCrsTransformationAdapter
        crsTransformationAdapterWeights = [
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterProj4J.new, 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterProj4jLocationtech.new, 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterOrbisgisCTS.new, 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterGeoPackageNGA.new, 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterGeoTools.new, 1.0),
            CrsTransformationAdapterWeight.createFromInstance(CrsTransformationAdapterGooberCTL.new, 2.0)
        ]
        CrsTransformationAdapterCompositeFactory.createCrsTransformationWeightedAverage(
            crsTransformationAdapterWeights
        )
    end

    def getAllCrsTransformationAdapters # : List[CrsTransformationAdapter]
        return [
            CrsTransformationAdapterProj4J.new,
            CrsTransformationAdapterProj4jLocationtech.new,
            CrsTransformationAdapterGeoPackageNGA.new,
            CrsTransformationAdapterOrbisgisCTS.new,
            CrsTransformationAdapterGooberCTL.new,
            CrsTransformationAdapterGeoTools.new,
            CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian,
            CrsTransformationAdapterCompositeFactory.createCrsTransformationAverage,
            CrsTransformationAdapterCompositeFactory.createCrsTransformationFirstSuccess,
            weightedAverageCrsTransformationAdapter
        ]
    end

    def transform(
        inputCoordinate, # CrsCoordinate,
        targetCrsIdentifier, # CrsIdentifier,
        allCrsTransformationAdapters #  List<CrsTransformationAdapter>
    ) # return type: List<CrsTransformationResult>
        allCrsTransformationAdapters.map { |crsTransformationAdapter|
          crsTransformationAdapter.transform(inputCoordinate, targetCrsIdentifier)
        }
    end
end

if __FILE__ == $0 # "__FILE__ is the magic variable that contains the name of the current file." https://www.ruby-lang.org/en/documentation/quickstart/4/
    crsTransformation = CrsTransformation.new
    crsTransformation.main()
end
