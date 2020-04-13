import os,glob,sys
lib_directory = "../ivy_dependencies/lib/"
for jar in glob.glob(os.path.join(lib_directory,'*.jar')):
    sys.path.append(jar)
    print jar

# This code is tested with Jython 2.7.2 (for Windows 10 64 bit)

# "crs-transformation-constants-9.5.4.jar" is one of the jar files appended to the path above 
# and it contains the below imported class "EpsgNumber"
from com.programmerare.crsConstants.constantsByAreaNameNumber.v9_5_4 import EpsgNumber

print "TODO: tranformation from EPSG %s to EPSG %s" % (EpsgNumber.WORLD__WGS_84__4326, EpsgNumber.SWEDEN__SWEREF99_TM__3006)