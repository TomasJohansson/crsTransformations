package java_sample;

import org.testng.annotations.Test;
import org.testng.Assert;

// the package below is from the Java library "crs-transformation-constants"
import com.programmerare.crsConstants.constantsByAreaNameNumber.v10_027.EpsgNumber;

import com.programmerare.crsTransformations.CrsTransformationAdapter;
import com.programmerare.crsTransformations.CrsTransformationResult;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory;
import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory;


public class CrsTransformationTest {
    // If you want to see Java tests then there are many JUnit tests of the Kotlin code in the module "crs-transformation-adapter-test" 
    // ( https://github.com/TomasJohansson/crsTransformations/tree/master/crs-transformation-adapter-test/src/test/java )
    
    @Test
    public void coordinateTransformationTest() {
        final int epsgWgs84  = EpsgNumber.WORLD__WGS_84__4326;
        final int epsgSweRef = EpsgNumber.SWEDEN__SWEREF99_TM__3006;

        CrsCoordinate centralStockholmWgs84 = CrsCoordinateFactory.latLon(59.330231, 18.059196, epsgWgs84);

        CrsTransformationAdapter crsTransformationAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian();
        // If the Gradle/Maven configuration includes all six adapter implementations, then the 
        // above created 'Composite' implementation will below use all six 'leaf' implementations 
        // and when transforming below, will return a resulting coordinate with a median longitude and a median latitude
        CrsTransformationResult centralStockholmResultSweRef = crsTransformationAdapter.transform(centralStockholmWgs84, epsgSweRef);
        Assert.assertTrue(centralStockholmResultSweRef.isSuccess());
        CrsCoordinate resultCoordinate = centralStockholmResultSweRef.getOutputCoordinate(); // median result
        Assert.assertNotNull(resultCoordinate);
        
        // https://javadoc.io/static/org.testng/testng/7.5/org/testng/Assert.html#assertEquals(double%5B%5D,double%5B%5D,double)
        // public static void assertEquals​(double[] actual, double[] expected, double delta)
        final double delta = 0.001;
        Assert.assertEquals(resultCoordinate.getX(), 674032.357, delta);
        Assert.assertEquals(resultCoordinate.getY(), 6580821.991, delta);
    }
}