package com.programmerare.crsCodeGeneration.utils

import com.programmerare.crsCodeGeneration.CodeGeneratorBase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

internal class FileUtilityTest {
    @Test
    fun getParentDirectoryByNavigatingUpwards() {
        val subPathsToTest = listOf(
            """\build\classes\kotlin\test\""",
            """build\classes\kotlin\test""",
            "build/classes/kotlin/test",
            "build/classes/kotlin/test",
            "build/classes/kotlin/*", // "build/classes/kotlin/main"
            "*/*/*/*"
        )
        subPathsToTest.forEach {
            getParentDirectoryByNavigatingUpwardsHelper(it)  
        }
    }

    // examples of the subPath parameter:
        // """\build\classes\kotlin\test\"""
        // """build\classes\kotlin\test"""
        // "/build/classes/kotlin/test/"
        // "build/classes/kotlin/test"
        // "/build/classes/kotlin/main/"
        // "build/classes/kotlin/main"
    private fun getParentDirectoryByNavigatingUpwardsHelper(subPath: String) {
        val pathToDirectoryForClassFiles: String? = FileUtility.javaClass.getResource("/").path
        // println("pathToRootDirectoryForClassFiles: " + pathToRootDirectoryForClassFiles)
        // the path retrieved above is now assumed to be like this:
        // " .../crsTransformations/crs-transformation-code-generation/build/classes/kotlin/test/"        
        val directoryForClassFiles = File(pathToDirectoryForClassFiles)
        assertTrue(directoryForClassFiles.exists())
        assertTrue(directoryForClassFiles.isDirectory)
        val directory: File = FileUtility.getParentDirectoryByNavigatingUpwards(
            subDirectoryWhereTheUpwardsNavigationWillStart = directoryForClassFiles,
            theLastPartsOfThePathsToTryNavigateUpFrom = listOf(subPath),
            expectedNameOfTargetBaseDirectoryToFind = CodeGeneratorBase.NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION
        )
        assertEquals(CodeGeneratorBase.NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION, directory.name)
        assertEquals(CodeGeneratorBase.NAME_OF_BASE_DIRECTORY_CONTAINING_THE_MODULES, directory.parentFile.name)        
    }
}
