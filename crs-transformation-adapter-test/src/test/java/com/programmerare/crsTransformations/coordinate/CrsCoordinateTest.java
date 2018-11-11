package com.programmerare.crsTransformations.coordinate;

import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_5_4.EpsgNumber;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CrsCoordinateTest {
    private final static String EpsgPrefix = "EPSG:";
    
    private final double deltaTolerance = 0.00001;
    private final double xLongitude = 12.34;
    private final double yLatitude = 56.67;
    private final int epsgNumber = EpsgNumber.SWEDEN__SWEREF99_TM__3006;
    private final String epsgCode = EpsgPrefix + epsgNumber;// EpsgCode._3006__SWEREF99_TM__SWEDEN;

    @Test
    void coordinateProperties_shouldHaveValuesEqualtToFactoryMethodsParameters() {
        CrsCoordinate coordinate = CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(xLongitude, yLatitude, epsgNumber);
        assertEquals(xLongitude, coordinate.getXEastingLongitude(), deltaTolerance);
        assertEquals(yLatitude, coordinate.getYNorthingLatitude(), deltaTolerance);
        assertEquals(epsgNumber, coordinate.getCrsIdentifier().getEpsgNumber());
    }

    @Test
    void coordinates_shouldBeEqual_whenUsingIntegerEpsgNumberAndDifferentFactoryMethodsWithParametersInDifferentOrder() {
        CrsCoordinate coordinate1 = CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(xLongitude, yLatitude, epsgNumber);
        CrsCoordinate coordinate2 = CrsCoordinateFactory.createFromYNorthingLatitudeAndXEastingLongitude(yLatitude, xLongitude, epsgNumber);
        assertEqualCoordinates(coordinate1, coordinate2);
    }

    @Test
    void coordinates_shouldBeEqual_whenUsingStringEpsgCodeAndDifferentFactoryMethodsWithParametersInDifferentOrder() {
        String crsCode = "EPSG:3006";
        CrsCoordinate coordinate1 = CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(xLongitude, yLatitude, crsCode);
        CrsCoordinate coordinate2 = CrsCoordinateFactory.createFromYNorthingLatitudeAndXEastingLongitude(yLatitude, xLongitude, crsCode);
        assertEqualCoordinates(coordinate1, coordinate2);
    }

    @Test
    void coordinates_shouldBeEqual_whenUsingCrsIdentifierAndDifferentFactoryMethodsWithParametersInDifferentOrder() {
        CrsIdentifier crsIdentifier = CrsIdentifierFactory.createFromEpsgNumber(3006);
        CrsCoordinate coordinate1 = CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(xLongitude, yLatitude, crsIdentifier);
        CrsCoordinate coordinate2 = CrsCoordinateFactory.createFromYNorthingLatitudeAndXEastingLongitude(yLatitude, xLongitude, crsIdentifier);
        assertEqualCoordinates(coordinate1, coordinate2);
    }

    @Test
    void coordinate_shouldBeCreatedWithWGS84asDefaultCrs_whenNotSpecifyingCrs() {
        CrsCoordinate coordinate;
        
        coordinate = CrsCoordinateFactory.createFromLongitudeLatitude(xLongitude, yLatitude);
        assertEquals(
            EpsgNumber.WORLD__WGS_84__4326,
            coordinate.getCrsIdentifier().getEpsgNumber()
        );

        // The tets below is the same as above except that the factory method use the reversed order for lat/lon parameters
        coordinate = CrsCoordinateFactory.createFromLatitudeLongitude(yLatitude, xLongitude);
        assertEquals(
            EpsgNumber.WORLD__WGS_84__4326,
            coordinate.getCrsIdentifier().getEpsgNumber()
        );        
    }

    private void assertEqualCoordinates(CrsCoordinate coordinate1, CrsCoordinate coordinate2) {
        assertEquals(coordinate1.getXEastingLongitude(), coordinate2.getXEastingLongitude(), deltaTolerance);
        assertEquals(coordinate1.getYNorthingLatitude(), coordinate2.getYNorthingLatitude(), deltaTolerance);
        assertEquals(coordinate1.getCrsIdentifier(), coordinate2.getCrsIdentifier()); // data class
        assertEquals(coordinate1.getCrsIdentifier().getEpsgNumber(), coordinate2.getCrsIdentifier().getEpsgNumber());

        // TODO: find out how the autogenerated implementations of equals and hashCode
        // are implemented in a Kotlin data class, e.g. Coordinate,
        // when properties are typed as double.
        // Maybe using Double.compare and Double.doubleToLongBits and following recommendations
        // of "Effective Java" book (Joshua Bloch)
        assertEquals(coordinate1.hashCode(), coordinate2.hashCode());
        assertEquals(coordinate1, coordinate2);
        // Note that the above assertion is not reliable since double fields are used.
        // Example below from an assertion failure in class CrsTransformationAdapterAverageTest:
        // assertEquals(coordinateWithAverageLatitudeAndLongitude, coordinateReturnedByCompositeAdapter);
        // Expected :Coordinate(xEastingLongitude=674032.3572074446, yNorthingLatitude=6580821.991903967, crsIdentifier=CrsIdentifier(crsCode=EPSG:3006, isEpsgCode=true, epsgNumber=3006))
        // Actual   :Coordinate(xEastingLongitude=674032.3572074447, yNorthingLatitude=6580821.991903967, crsIdentifier=CrsIdentifier(crsCode=EPSG:3006, isEpsgCode=true, epsgNumber=3006))
    }

    @Test // six decimals are commonly used for latitude and longitude values 
    void coordinateWithSixDecimals_shouldBeEqualToCoordinateConstructedWithTheSameValues_whenTheOnlyDifferenceIsSomeAdditionalZeroes() {
        CrsCoordinate c1 = CrsCoordinateFactory.createFromLatitudeLongitude(59.123456, 18.123456000);
        CrsCoordinate c2 = CrsCoordinateFactory.createFromLatitudeLongitude(59.123456000, 18.123456);
        assertEquals(
            c1, c2
        );
        assertEquals(
            c1.hashCode(), c2.hashCode()
        );
    }

    @Test
    void coordinateWithNineDecimals_shouldBeEqualToCoordinateConstructedWithTheSameValues_whenTheOnlyDifferenceIsSomeAdditionalZeroes() {
        CrsCoordinate c1 = CrsCoordinateFactory.createFromLatitudeLongitude(59.123456789, 18.123456789000);
        CrsCoordinate c2 = CrsCoordinateFactory.createFromLatitudeLongitude(59.123456789000, 18.123456789);
        assertEquals(
            c1, c2
        );
        assertEquals(
            c1.hashCode(), c2.hashCode()
        );
    }

    @Test
    void coordinates_shouldNotBeEqual_whenDifferenceAtTwelfthDecimalOfLatitude() {
        // very small latitude difference:
        assertNotEquals(
            CrsCoordinateFactory.createFromLatitudeLongitude(59.123456789000, 18.123456789),
            CrsCoordinateFactory.createFromLatitudeLongitude(59.123456789001, 18.123456789)
        );
    }

    @Test
    void coordinates_shouldNotBeEqual_whenDifferenceAtTwelfthDecimalOfLongitude() {
        // very small longitude difference:
        assertNotEquals(
            CrsCoordinateFactory.createFromLatitudeLongitude(59.123456789, 18.123456789000),
            CrsCoordinateFactory.createFromLatitudeLongitude(59.123456789, 18.123456789001)
        );
    }

    @Test
    void coordinates_shouldBeEqual_whenCreatedWithTheSameValuesButDifferentFactoryMethods() {
        final CrsIdentifier crsIdentifier = CrsIdentifierFactory.createFromEpsgNumber(epsgNumber);
        final String epsgCode = EpsgPrefix + epsgNumber;
        
        final CrsCoordinate expectedCoordinate = CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(xLongitude, yLatitude, epsgNumber);
        // all should be equal to each other, so one of them was chosen above 
        // as the "expected" and then the others are compared with it in the below assertions

        // -----------------------------------------------------------------------
        // the last parameter (epsgNumber) is an integer in the first below assertions:
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.lonLat(xLongitude, yLatitude, epsgNumber)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.xy(xLongitude, yLatitude, epsgNumber)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.eastingNorthing(xLongitude, yLatitude, epsgNumber)  
        );

        // the below four assertions are using x/y values in the opposite order 
        // compared to the above three assertions
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.createFromYNorthingLatitudeAndXEastingLongitude(yLatitude, xLongitude, epsgNumber)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.latLon(yLatitude, xLongitude, epsgNumber)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.yx(yLatitude, xLongitude, epsgNumber)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.northingEasting(yLatitude, xLongitude, epsgNumber)
        );

        // -----------------------------------------------------------------------
        // epsg code (string parameter) is the last parameter below
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(xLongitude, yLatitude, epsgCode)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.lonLat(xLongitude, yLatitude, epsgCode)   
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.xy(xLongitude, yLatitude, epsgCode)   
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.eastingNorthing(xLongitude, yLatitude, epsgCode)  
        );

        // the below four assertions are using x/y values in the opposite order 
        // compared to the above four assertions

        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.createFromYNorthingLatitudeAndXEastingLongitude(yLatitude, xLongitude, epsgCode)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.latLon(yLatitude, xLongitude, epsgCode)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.yx(yLatitude, xLongitude, epsgCode)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.northingEasting(yLatitude, xLongitude, epsgCode)
        );        

        // -----------------------------------------------------------------------
        // crsIdentifier obkect is the last parameter below
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(xLongitude, yLatitude, crsIdentifier)   
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.lonLat(xLongitude, yLatitude, crsIdentifier)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.xy(xLongitude, yLatitude, crsIdentifier)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.eastingNorthing(xLongitude, yLatitude, crsIdentifier)
        );

        // the below four assertions are using x/y values in the opposite order 
        // compared to the above four assertions

        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.createFromYNorthingLatitudeAndXEastingLongitude(yLatitude, xLongitude, crsIdentifier)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.latLon(yLatitude, xLongitude, crsIdentifier)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.yx(yLatitude, xLongitude, crsIdentifier)
        );
        assertEqualCoordinates(expectedCoordinate,
            CrsCoordinateFactory.northingEasting(yLatitude, xLongitude, crsIdentifier)
        );        
    }

    @Test
    void coordinate_shouldHaveEquivalentXEastingLongitudeProperties() {
        CrsCoordinate c = CrsCoordinateFactory.createFromYNorthingLatitudeAndXEastingLongitude(yLatitude, xLongitude, epsgCode);
        assertEquals(c.getXEastingLongitude(), c.getX());
        assertEquals(c.getXEastingLongitude(), c.getEasting());
        assertEquals(c.getXEastingLongitude(), c.getLongitude());
    }

    @Test
    void coordinate_shouldHaveEquivalentgetYNorthingLatitudeProperties() {
        CrsCoordinate c = CrsCoordinateFactory.createFromYNorthingLatitudeAndXEastingLongitude(yLatitude, xLongitude, epsgCode);
        assertEquals(c.getYNorthingLatitude(), c.getY());
        assertEquals(c.getYNorthingLatitude(), c.getNorthing());
        assertEquals(c.getYNorthingLatitude(), c.getLatitude());
//        new CrsCoordinate(1.1,2.1,CrsIdentifierFactory.createFromEpsgNumber(123));
    }

    // throw IllegalArgumentException("Neither of the two coordinate parameters must be null i.e. neither 'X / Easting / Longitude' nor 'Y / Northing / Latitude'")
    // fragile hardcoded string below but will not change often and if/when then it will be easy to fix when it fails
    private final static String EXPECTED_PART_OF_EXCEPTION_MESSAGE_WHEN_COORDINATE_VALUES_IS_NULL = "Neither of the two coordinate parameters must be null";
    
    @Test
    void createFromXEastingLongitudeAndYNorthingLatitude_shouldThrowException_whenXisNull() {
        Double x = null;
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(
                x,
                20.0,
                4326
            )
        );
        assertThat(exception.getMessage(), containsString(EXPECTED_PART_OF_EXCEPTION_MESSAGE_WHEN_COORDINATE_VALUES_IS_NULL));                
    }

    @Test
    void createFromXEastingLongitudeAndYNorthingLatitude_shouldThrowException_whenYisNull() {
        Double y = null;
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(
                60.0,
                y,
                4326
            )
        );
        assertThat(exception.getMessage(), containsString(EXPECTED_PART_OF_EXCEPTION_MESSAGE_WHEN_COORDINATE_VALUES_IS_NULL));
    }

    @Test
    void createFromXEastingLongitudeAndYNorthingLatitude_shouldThrowException_whenCrsIdentifierIsNull() {
        CrsIdentifier crsIdentifier = null;
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(
                60.0,
                20.0,
                crsIdentifier
            )
        );
        // "java.lang.IllegalArgumentException: Parameter specified as non-null is null: method com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude, parameter crsIdentifier"
        assertExceptionMessageForIllegalArgumentException(exception, "crsIdentifier");
    }

    @Test
    void createFromXEastingLongitudeAndYNorthingLatitude_shouldThrowException_whenEpsgNumberIsNull() {
        Integer epsgNumber = null;
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(
                60.0,
                20.0,
                epsgNumber
            )
        );
        // fragile hardcoded string below but will not change often and if/when then it will be easy to fix when it fails
        assertThat(exception.getMessage(), containsString("EPSG number must not be null"));
    }

    @Test
    void createFromXEastingLongitudeAndYNorthingLatitude_shouldThrowException_whenCrsCodeIsNull() {
        String crsCode = null;
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(
                60.0,
                20.0,
                crsCode
            )
        );
        // "java.lang.IllegalArgumentException: Parameter specified as non-null is null: method com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude, parameter crsCode"        
        assertExceptionMessageForIllegalArgumentException(exception, "crsCode");
    }

    private void assertExceptionMessageForIllegalArgumentException(IllegalArgumentException exception, String suffixWithNameOfParameter) {
        assertNotNull(exception);
        final String actualEceptionMessage = exception.getMessage();
        // fragile hardcoded strings below but will not change often and if/when then it will be easy to fix when it fails
        
        // actualEceptionMessage for example: "java.lang.IllegalArgumentException: Parameter specified as non-null is null: method com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude, parameter crsCode"        
        final String expectedEceptionMessagePart1 = "Parameter specified as non-null is null:";
        final String expectedEceptionMessagePart2 = "parameter " + suffixWithNameOfParameter;
        assertThat(actualEceptionMessage, containsString(expectedEceptionMessagePart1));
        assertThat(actualEceptionMessage, containsString(expectedEceptionMessagePart2));
    }
}