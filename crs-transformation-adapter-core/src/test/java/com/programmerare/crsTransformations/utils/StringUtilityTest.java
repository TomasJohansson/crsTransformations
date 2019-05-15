package com.programmerare.crsTransformations.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilityTest {
    @Test
    void getLastNumericalValueFromString() {
        assertNumericalValue("3.2.0", "geopackage-core-3.2.0.jar");
        assertNumericalValue("1.5.2", "cts-1.5.2.jar");
        assertNumericalValue("1.1", "coordinate-transformation-library-1.1.jar");
        assertNumericalValue("0.1.0", "proj4j-0.1.0.jar");
        assertNumericalValue("21.1", "gt-main-21.1.jar");
            
        assertNumericalValue("2", "2");
        assertNumericalValue("34.56", "34.56");
        
        assertNumericalValue("2", "a2b");
        assertNumericalValue("3.4", "a3.4b");
        assertNumericalValue("5.6", "a5.6.b");
        assertNumericalValue("7.8", "a.7.8.b");

        assertNumericalValue("2", "2b");
        assertNumericalValue("3.4", "3.4b");
        assertNumericalValue("5.6", "5.6.b");
        assertNumericalValue("7.8", ".7.8.b");

        assertNumericalValue("2", "a2");
        assertNumericalValue("3.4", "a3.4");
        assertNumericalValue("5.6", "a5.6.");
        assertNumericalValue("7.8", "a.7.8.");

        assertNumericalValue("4.5.6", "abc.1.2.3.def.4.5.6.hjk");
        assertNumericalValue("444.555.666", "abc.111.222.333.def.444.555.666.hjk");
        assertNumericalValue("8.92", "ab-cd.12.34.ef-gh.5.67.ij-kl.8.92.mn-op");
        assertNumericalValue("8.92", "ab-cd-12.34-ef-gh-5.67-ij-kl-8.92-mn-op");

        assertNumericalValue("?", "");
        assertNumericalValue("?", " ");
        assertNumericalValue("?", "\t");
        assertNumericalValue("?", "a");
        assertNumericalValue("?", "abc");
        assertNumericalValue("?", null);
    }

    private void assertNumericalValue(String expected, String inputString) {
        String numericValue = StringUtility.getLastNumericalValueFromString(inputString);
        assertEquals(expected, numericValue);        
    }
}
