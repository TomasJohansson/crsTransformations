package groovy_sample

import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_5_4.EpsgNumber

import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS
import com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA

import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory

class CrsTransformation {

    static void main(String[] args) {
        def crsCoordinate = CrsCoordinateFactory.latLon 59.330231, 18.059196
        def targetCrs = CrsIdentifierFactory.createFromEpsgNumber(EpsgNumber.SWEDEN__SWEREF99_TM__3006)
        def crsNGA = new CrsTransformationAdapterGeoPackageNGA()
        def crsGoober = new CrsTransformationAdapterGooberCTL()
        def crsOrbisgis = new CrsTransformationAdapterOrbisgisCTS()
        def crsGeo = new CrsTransformationAdapterGeoTools()
        def crsProj4J = new CrsTransformationAdapterProj4J()
        println("crs: ${crsGoober.adapteeType}")
    }
}
