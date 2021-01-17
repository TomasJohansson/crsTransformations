package com.programmerare.crsCodeGeneration.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class JavaPackageToCSharpeNamespaceConverterTest {
    @Test
    fun getAsNameOfCSharpeNameSpace() {
        assertEquals(
            "Programmerare.CrsConstants.ConstantsByAreaNameNumber.v9_8_9",
            JavaPackageToCSharpeNamespaceConverter.getAsNameOfCSharpeNameSpace(
                "com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9"
            )
        )
    }

    @Test
    fun getAsNameOfDartModule() {
        assertEquals(
            "crs_constants.v9_9_1",
            JavaPackageToCSharpeNamespaceConverter.getAsNameOfDartModule(
            "com.programmerare.crsConstants.constantsByAreaNameNumber.v9_9_1"
            )
        )
    }
}
