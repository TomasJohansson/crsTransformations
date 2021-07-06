package com.programmerare.crsCodeGeneration.constantsGenerator

import com.programmerare.crsCodeGeneration.CodeGeneratorBase
import com.programmerare.crsCodeGeneration.CodeGeneratorBase.Companion.NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class ConstantClassGeneratorTest {

    @Test
    fun getEmptyStringIfNoError() {
        val args: Array<String> = arrayOf("v9_5_3", "dbName", "dbUser", "dbPassword", "java")
        val result = ConstantClassGenerator.getValidationErrorMessageOrEmptyStringIfNoError(args)
        assertEquals("", result)
    }

    @Test
    fun getValidationErrorMessageWhenTheFirstVersionParameterIsNotCorrect() {
        val args: Array<String> = arrayOf("x9_5_3", "dbName", "dbUser", "dbPassword")
        // the first letter should be "v" and not "x" as above
        val result = ConstantClassGenerator.getValidationErrorMessageOrEmptyStringIfNoError(args)
        assertNotEquals("", result)
    }

    @Test
    fun getValidationErrorMessageWhenTooFewParameters() {
        val args: Array<String> = arrayOf("x9_5_3", "dbName", "dbUser")
        // there should be a fourth parameter with a password
        val result = ConstantClassGenerator.getValidationErrorMessageOrEmptyStringIfNoError(args)
        assertNotEquals("", result)
    }


    @Test
    fun isValidAsVersionPrefix() {
        assertTrue(ConstantClassGenerator.isValidAsVersionPrefix("v9"))
        assertTrue(ConstantClassGenerator.isValidAsVersionPrefix("v9_5"))
        assertTrue(ConstantClassGenerator.isValidAsVersionPrefix("v9_5_3"))

        assertFalse(ConstantClassGenerator.isValidAsVersionPrefix("v9_"))
        assertFalse(ConstantClassGenerator.isValidAsVersionPrefix("v9_5_3_"))
        assertFalse(ConstantClassGenerator.isValidAsVersionPrefix("x9_5_3"))
        assertFalse(ConstantClassGenerator.isValidAsVersionPrefix("v9x_5_3"))
        assertFalse(ConstantClassGenerator.isValidAsVersionPrefix("v9.5.3"))
        assertFalse(ConstantClassGenerator.isValidAsVersionPrefix("v9_5_x"))

    }
    
    @Test
    fun getDirectoryForCodeGenerationModule() {
        // The tested method "getDirectoryForCodeGenerationModule"  
        // is defined in the abstract base class, 
        // which is also shown below by using explicit typing for the base class. 
        val constantGenerator: CodeGeneratorBase = ConstantClassGenerator()
        val file = constantGenerator.getDirectoryForCodeGenerationModule()
        assertEquals(NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION, file.name)
    }
}
