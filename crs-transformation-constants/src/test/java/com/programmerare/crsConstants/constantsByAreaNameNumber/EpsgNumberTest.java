package com.programmerare.crsConstants.constantsByAreaNameNumber;

import static org.junit.jupiter.api.Assertions.assertEquals;
// published versions:
// 9.5.4
// 9.8.9
// https://search.maven.org/artifact/com.programmerare.crs-transformation/crs-transformation-constants

//import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_5_4.EpsgNumber; // https://search.maven.org/artifact/com.programmerare.crs-transformation/crs-transformation-constants/9.5.4/jar
//import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_6.EpsgNumber;
//import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_6_3.EpsgNumber;
//import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_7.EpsgNumber;
//import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9.EpsgNumber; // https://search.maven.org/artifact/com.programmerare.crs-transformation/crs-transformation-constants/9.8.9/jar
//import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_9_1.EpsgNumber;
//import com.programmerare.crsConstants.constantsByAreaNameNumber.v10_011.EpsgNumber;
import com.programmerare.crsConstants.constantsByAreaNameNumber.v10_027.EpsgNumber;

// the below tested constants has so far had the same names for the generated constants for all the above versions

import org.junit.jupiter.api.Test;

class EpsgNumberTest {

    @Test
    void epsg_wgs84() {
        assertEquals(EpsgNumber.WORLD__WGS_84__4326, 4326);
    }

    @Test // SWEREF99 ==> EPSG 3006-3018
    void epsg_sweref99() { 
        assertEquals(EpsgNumber.SWEDEN__SWEREF99_TM__3006, 3006);
        assertEquals(EpsgNumber.SWEDEN__12_00__SWEREF99_12_00__3007, 3007);
        assertEquals(EpsgNumber.SWEDEN__13_30__SWEREF99_13_30__3008, 3008);
        assertEquals(EpsgNumber.SWEDEN__15_00__SWEREF99_15_00__3009, 3009);
        assertEquals(EpsgNumber.SWEDEN__16_30__SWEREF99_16_30__3010, 3010);
        assertEquals(EpsgNumber.SWEDEN__18_00__SWEREF99_18_00__3011, 3011);
        assertEquals(EpsgNumber.SWEDEN__14_15__SWEREF99_14_15__3012, 3012);
        assertEquals(EpsgNumber.SWEDEN__15_45__SWEREF99_15_45__3013, 3013);
        assertEquals(EpsgNumber.SWEDEN__17_15__SWEREF99_17_15__3014, 3014);
        assertEquals(EpsgNumber.SWEDEN__18_45__SWEREF99_18_45__3015, 3015);
        assertEquals(EpsgNumber.SWEDEN__20_15__SWEREF99_20_15__3016, 3016);
        assertEquals(EpsgNumber.SWEDEN__21_45__SWEREF99_21_45__3017, 3017);
        assertEquals(EpsgNumber.SWEDEN__23_15__SWEREF99_23_15__3018, 3018);
    }

    @Test // RT90 ==> EPSG 3019-3024
    void epsg_rt90() {
        assertEquals(EpsgNumber.SWEDEN__7_5_GON_W__RT90_7_5_GON_V__3019, 3019);
        assertEquals(EpsgNumber.SWEDEN__5_GON_W__RT90_5_GON_V__3020, 3020);
        assertEquals(EpsgNumber.SWEDEN__2_5_GON_W__RT90_2_5_GON_V__3021, 3021);
        assertEquals(EpsgNumber.SWEDEN__0_GON__RT90_0_GON__3022, 3022);
        assertEquals(EpsgNumber.SWEDEN__2_5_GON_E__RT90_2_5_GON_O__3023, 3023);
        assertEquals(EpsgNumber.SWEDEN__5_GON_E__RT90_5_GON_O__3024, 3024);
    }
}
