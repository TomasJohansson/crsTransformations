package com.programmerare.crsTransformations.crsIdentifier;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrsIdentifierTest {

    private String getCrsCodeIncludingUppercasedEpsgPrefix(int epsgNumber) {
        return "EPSG:" + epsgNumber;
    }
    
    @Test
    void crsIdentifier_shouldReturnWhitespaceTrimmedCrsCodeAndNotBeConsideredAsEpsg_whenCreatedFromNonEpsgString() {
        final CrsIdentifier crsIdentifier = CrsIdentifierFactory.createFromCrsCode("  abc  ");
        assertEquals("abc", crsIdentifier.getCrsCode());
        assertEquals(false, crsIdentifier.isEpsgCode());
    }

    @Test
    void crsIdentifier_shouldReturnEpsgNumberAndEpsgPrefixedCrsCodeAndBeConsideredAsEpsg_whenCreatedFromEpsgNumber() {
        final int inputEpsgNumber = 3006;
        // No validation that the number is actually an existing EPSG but any positive integer
        // is assumed to be a EPSG number
        final CrsIdentifier crsIdentifier = CrsIdentifierFactory.createFromEpsgNumber(inputEpsgNumber);
        assertEquals(
            inputEpsgNumber, // expected
            crsIdentifier.getEpsgNumber()
        );
        assertEquals(getCrsCodeIncludingUppercasedEpsgPrefix(inputEpsgNumber), crsIdentifier.getCrsCode());
        assertEquals(true, crsIdentifier.isEpsgCode());
    }

    @Test
    void crsIdentifier_shouldReturnEpsgNumberAndUppercasedEpsgPrefixedWhitespaceTrimmedCrsCodeAndBeConsideredAsEpsg_whenCreatedFromLowecasedEpsgCodeWithSurroundingWhitespace() {
        final int inputEpsgNumber = 4326;
        final String inputCrsCode = "  epsg:" + inputEpsgNumber + "  "; 
        final CrsIdentifier crsIdentifier = CrsIdentifierFactory.createFromCrsCode(inputCrsCode);
        // the input should become trimmed and return string with uppercased "EPSG:" prefix
        assertEquals(
            getCrsCodeIncludingUppercasedEpsgPrefix(inputEpsgNumber), 
            crsIdentifier.getCrsCode()
        );
        assertEquals(true, crsIdentifier.isEpsgCode());
        assertEquals(inputEpsgNumber, crsIdentifier.getEpsgNumber());
    }

    @Test
    void crsIdentifierFactory_shouldThrowException_whenCrsCodeInputIsNull() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> CrsIdentifierFactory.createFromCrsCode(null), // should fail
            "Must not be null"
        );
        assertExceptionMessageWhenArgumentWasNullOrEmptyString(exception, "non-null");
    }

    @Test
    void crsIdentifierFactory_shouldThrowException_whenCrsCodeInputIsOnlyWhitespace() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CrsIdentifierFactory.createFromCrsCode("   "), // should fail
            "Must not be empty string"
        );
        assertExceptionMessageWhenArgumentWasNullOrEmptyString(exception, "non-empty");
    }

    @Test
    void crsIdentifierFactory_shouldThrowException_whenCrsCodeIsEpsgWithNegativeNumber() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CrsIdentifierFactory.createFromCrsCode(getCrsCodeIncludingUppercasedEpsgPrefix(-123)), // should fail
                "EPSG must not be negative"
        );
        assertExceptionMessageWhenArgumentWasNullOrEmptyString(exception, "non-positive");
    }


    @Test
    void crsIdentifierFactory_shouldThrowException_epsgNumberIsNegative() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CrsIdentifierFactory.createFromEpsgNumber(-1), // should fail
            "EPSG must not be negative"
        );
        assertExceptionMessageWhenArgumentWasNullOrEmptyString(exception, "non-positive");
    }
    
    @Test
    void crsIdentifiers_shouldBeEqual_whenCreatedFromEpsgNumberAndCorrespondingCrsCode() {
        CrsIdentifier fromEpsgNumber    = CrsIdentifierFactory.createFromEpsgNumber(3006);
        CrsIdentifier fromCrsCode       = CrsIdentifierFactory.createFromCrsCode("  epsg:3006   ");
        assertEquals(fromEpsgNumber, fromCrsCode);
        assertEquals(fromEpsgNumber.hashCode(), fromCrsCode.hashCode());
    }

    /**
     * @param exception
     * @param expectedStringToBeContainedInExceptionMessage e.g. "non-null" or "non-empty"
     */
    private void assertExceptionMessageWhenArgumentWasNullOrEmptyString(
        RuntimeException exception, // NullPointerException or IllegalArgumentException
        String expectedStringToBeContainedInExceptionMessage
    ) {
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        // the exception message is currently something like this: "Parameter specified as non-null is null: method com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier$Companion.createFromCrsCode, parameter crsCode"
        // (potentially fragile to test the message strings but it does not really change often, and in such a rare scenario, then easy to fix)
        assertThat(exception.getMessage(), containsString(expectedStringToBeContainedInExceptionMessage));
    }

    @Test
    void createFromEpsgNumber_shouldThrowException_whenEpsgNumberIsNull() {
        Integer epsgNumber = null;
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CrsIdentifierFactory.createFromEpsgNumber(epsgNumber)
        );
        // Previously the above method resulted in a "NullPointerException"
        // (created by Kotlin when trying to invoke)
        // but now the validation instead creates an IllegalArgumentException 
        assertNotNull(exception);
        final String exceptionMessage = exception.getMessage();
        // fragile hardcoded string below but will not change often and if/when then it will be easy to fix when it fails
        assertThat(exceptionMessage, containsString("EPSG number must not be null"));        
    }

    @Test
    void createFromCrsCode_shouldThrowException_whenCrsCodeIsNull() {
        String crsCode = null;
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> CrsIdentifierFactory.createFromCrsCode(crsCode)
        );
        // "java.lang.IllegalArgumentException: Parameter specified as non-null is null: method com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory.createFromCrsCode, parameter crsCode"
    }
    
}