package com.programmerare.crsCodeGeneration.utils

import java.io.File

object FileUtility {

    // Example of the three parameters:
    //  file object for a directory such as .../crs-transformation-code-generation/build/classes/kotlin/main/
    // listOf("build/classes/kotlin/main", "build/classes/kotlin/test")
    // "crs-transformation-code-generation"
    fun getParentDirectoryByNavigatingUpwards(
        subDirectoryWhereTheUpwardsNavigationWillStart: File,
        theLastPartsOfThePathsToTryNavigateUpFrom: List<String>,
        expectedNameOfTargetBaseDirectoryToFind: String
    ): File {
        // File.separator
        var returnDirectory = subDirectoryWhereTheUpwardsNavigationWillStart
        theLastPartsOfThePathsToTryNavigateUpFrom.forEach { subPath ->
            // examples of the current subPath:
                // """\build\classes\kotlin\test\"""
                // """build\classes\kotlin\test"""
                // "/build/classes/kotlin/test/"
                // "build/classes/kotlin/test"
                // "/build/classes/kotlin/main/"
                // "build/classes/kotlin/main"
            val pathWithForwardSlashes = subPath.replace('\\', '/')
            // println("pathWithForwardSlashes " + pathWithForwardSlashes)
            val arr = pathWithForwardSlashes.split('/')
            val directoryNamesToNavigateUpwards = arr.filter { !it.isNullOrBlank() }.reversed()
            // directoryNamesToNavigateUpwards.forEach { println(it) }
            returnDirectory = subDirectoryWhereTheUpwardsNavigationWillStart
            var success = true
            directoryNamesToNavigateUpwards.forEach { expectedDirectoryName ->
                if(
                    expectedDirectoryName == returnDirectory.name
                    ||
                    expectedDirectoryName == "*"
                ) {
                    returnDirectory = returnDirectory.parentFile
                }
                else {
                    success = false
                }
            }
            if(success) {
                if(returnDirectory.name == expectedNameOfTargetBaseDirectoryToFind) {
                    return returnDirectory    
                }
            }
        }
        val errorMessageSuffix = """
            
            If you see this message then it is maybe because of a bug which has assumed that 
             the compiled classes are located within a certain subdirectory as when you are running the code 
             as for example a gradle project from within Intellij IDEA.
             This might not be true when running the code in other ways, i.e. the assumed paths as below 
             might not be valid subdirectories within the base module:
            build/classes/kotlin/main
            build/classes/kotlin/test
            but there should also be other paths that you should work, and you can try to modify the input with paths such as 
            */*/*
            */*/*/*
            (i.e. if you do not care about the name but want to match any sub directory name when trying to determine the path for the base directory)
        """.trimIndent()
        throw RuntimeException("input file '${subDirectoryWhereTheUpwardsNavigationWillStart.absolutePath}' and subPaths '$theLastPartsOfThePathsToTryNavigateUpFrom' and expectedNameOfTargetDirectory $expectedNameOfTargetBaseDirectoryToFind " + errorMessageSuffix)
    }
}
