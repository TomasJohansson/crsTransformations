package com.programmerare.crsTransformations.utils

import java.lang.RuntimeException

internal object StringUtility {
    
    private val regularExpressionForExtractionOfLastVersionNumberString = Regex("""^.*?((\d|\d\.)*\d)+[^\d]*$""")

    /**
     * The purpose of this regular expression is to use it for extracting the name of a jar file 
     * from an input string that might include the full "URL" path.
     * Example: Input string "C:/...6d0/junit-jupiter-api-5.6.2.jar!/org/junit..."
     * Expected string to return with above input: "junit-jupiter-api-5.6.2.jar"
     */
    private val regularExpressionForExtractingTheNameOfJarFileFromUrlPath = Regex("""^.*/(.+?.jar)!.*$""")
    
    val returnValueIfNoNumericalValueIsFound = "?"
    
    /**
     * Should never return null or throw an Exception but instead return "?" 
     * if a version could not be returned.
     */
    @JvmStatic
    fun getLastNumericalValueFromString(inputString: String?): String {
        if(inputString == null) return returnValueIfNoNumericalValueIsFound
        val matchGroupCollection: MatchGroupCollection? = regularExpressionForExtractionOfLastVersionNumberString.matchEntire(inputString)?.groups
        if(matchGroupCollection == null) return returnValueIfNoNumericalValueIsFound
        return matchGroupCollection.get(1)?.value!!
    }

    /**
     * @param urlPathForJarFile some path string returned from methods such as URL.toExternalForm() or URL.getLocation().toExternalForm()
     */
    @JvmStatic
    fun getJarFileNameWithoutThePath(urlPathForJarFile: String): String {
        try {
            val pathWithoutBackslashes = urlPathForJarFile.replace('\\', '/')
            // the path string could be "... 6d0/junit-jupiter-api-5.6.2.jar!/org/jun ..."
            // i.e. normally something like that above but below is a simple way to make the method        
            // be able to handle e.g. "junit-jupiter-api-5.6.2.jar" by adding the "/" and "!" as prefix and suffix below          
            val matchResult = regularExpressionForExtractingTheNameOfJarFileFromUrlPath.find(
                "/" + pathWithoutBackslashes + "!",
                0 // startindex
            )
            val groupValues = matchResult!!.groupValues
            val jarFileName = groupValues.get(1).trim()
            sanityCheck(jarFileName)
            return jarFileName
        }
        catch(e: Throwable) {
            return ""
        }
    }

    private fun sanityCheck(jarFilePath: String) {
        if(!jarFilePath.endsWith(".jar")) {
            throw RuntimeException("Does not seem to be a jar file: " + jarFilePath)
        }
    }    
}
