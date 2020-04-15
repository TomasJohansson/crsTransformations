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
java_import "com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory"
java_import "com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterWeight"
java_import "com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J"
java_import "com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools"
java_import "com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA"
java_import "com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL"
java_import "com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS"

class CrsTransformation

    def main
        inputCoordinate = CrsCoordinateFactory::latLon(59.330231, 18.059196) #  Implicit/Default CrsIdentifier for latitude/longitude: EpsgNumber.WORLD__WGS_84__4326
        targetCrsIdentifier = CrsIdentifierFactory::createFromEpsgNumber(EpsgNumber::SWEDEN__SWEREF99_TM__3006)
    
        crsTransformationResults = transform(inputCoordinate, targetCrsIdentifier, getAllCrsTransformationAdapters)

        crsTransformationResults.each { |result|
            nameOfCrsTransformationAdapterImplementation = result.crsTransformationAdapterResultSource.adapteeType
            outputCoordinate = result.outputCoordinate
            puts "#{nameOfCrsTransformationAdapterImplementation} : X / Y ===> #{outputCoordinate.x} / #{outputCoordinate.y}"
        }        

        # Below is the printed output from the above code in this main method: 
        #    LEAF_PROJ4J_0_1_0 : X / Y ===> 674032.357326444 / 6580821.991123579
        #    LEAF_NGA_GEOPACKAGE_3_1_0 : X / Y ===> 674032.357326444 / 6580821.991123579
        #    LEAF_ORBISGIS_1_5_1 : X / Y ===> 674032.3573261796 / 6580821.991121078
        #    LEAF_GOOBER_1_1 : X / Y ===> 674032.357 / 6580821.991
        #    LEAF_GEOTOOLS_20_0 : X / Y ===> 674032.3571771547 / 6580821.994371211
        #    COMPOSITE_MEDIAN : X / Y ===> 674032.3573261796 / 6580821.991123579
        #    COMPOSITE_AVERAGE : X / Y ===> 674032.3572312444 / 6580821.991747889
        #    COMPOSITE_FIRST_SUCCESS : X / Y ===> 674032.357 / 6580821.991
        #    COMPOSITE_WEIGHTED_AVERAGE : X / Y ===> 674032.3571927036 / 6580821.991623241        
    end

    private
    
    def weightedAverageCrsTransformationAdapter
        crsTransformationAdapterWeights = [
            CrsTransformationAdapterWeight::createFromInstance(CrsTransformationAdapterProj4J.new, 1.0),
            CrsTransformationAdapterWeight::createFromInstance(CrsTransformationAdapterOrbisgisCTS.new, 1.0),
            CrsTransformationAdapterWeight::createFromInstance(CrsTransformationAdapterGeoPackageNGA.new, 1.0),
            CrsTransformationAdapterWeight::createFromInstance(CrsTransformationAdapterGeoTools.new, 1.0),
            CrsTransformationAdapterWeight::createFromInstance(CrsTransformationAdapterGooberCTL.new, 2.0)
        ]
        CrsTransformationAdapterCompositeFactory::createCrsTransformationWeightedAverage(
            crsTransformationAdapterWeights
        )
    end

    def getAllCrsTransformationAdapters # : List[CrsTransformationAdapter]
        return [
            CrsTransformationAdapterProj4J.new,
            CrsTransformationAdapterGeoPackageNGA.new,
            CrsTransformationAdapterOrbisgisCTS.new,
            CrsTransformationAdapterGooberCTL.new,
            CrsTransformationAdapterGeoTools.new,
            CrsTransformationAdapterCompositeFactory::createCrsTransformationMedian,
            CrsTransformationAdapterCompositeFactory::createCrsTransformationAverage,
            CrsTransformationAdapterCompositeFactory::createCrsTransformationFirstSuccess,
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
