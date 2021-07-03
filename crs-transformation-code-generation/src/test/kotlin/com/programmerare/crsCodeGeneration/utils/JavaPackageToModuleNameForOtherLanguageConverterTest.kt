package com.programmerare.crsCodeGeneration.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class JavaPackageToModuleNameForOtherLanguageConverterTest {
    @Test
    fun getAsNameOfCSharpeNameSpace() {
        assertEquals(
            "Programmerare.CrsConstants.ConstantsByAreaNameNumber.v9_8_9",
            JavaPackageToModuleNameForOtherLanguageConverter.getAsNameOfCSharpeNameSpace(
                "com.programmerare.crsConstants.constantsByAreaNameNumber.v10_027"
            )
        )
    }

    @Test
    fun getAsNameOfDartModule() {
        assertEquals(
            "crs_constants.v9_9_1",
            JavaPackageToModuleNameForOtherLanguageConverter.getAsNameOfDartModule(
            "com.programmerare.crsConstants.constantsByAreaNameNumber.v9_9_1"
            )
        )
    }
}
