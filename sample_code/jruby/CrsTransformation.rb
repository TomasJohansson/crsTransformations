require 'java'
Dir["../ivy_dependencies/lib/*.jar"].each { |jar| 
    require jar
    puts jar.to_s
}

# This code is tested with JRuby 9.2.11.1 (for Windows 10 64 bit)

# "crs-transformation-constants-9.5.4.jar" is one of the jar files that are "required" above 
# and it contains the below imported class "EpsgNumber"
java_import "com.programmerare.crsConstants.constantsByAreaNameNumber.v9_5_4.EpsgNumber"

epsgSource = EpsgNumber::WORLD__WGS_84__4326
epsgTarget = EpsgNumber::SWEDEN__SWEREF99_TM__3006
puts "TODO: tranformation from EPSG #{epsgSource} to EPSG #{epsgTarget}"