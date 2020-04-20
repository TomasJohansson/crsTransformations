package com.programmerare.crsTransformations.utils

import java.lang.RuntimeException

internal object StringUtility {
    
    private val regularExpressionForExtractionOfLastVersionNumberString = Regex("""^.*?((\d|\d\.)*\d)+[^\d]*$""")
    
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

    @JvmStatic
    fun getJarFileNameWithoutThePath(pathForJarFile: String): String {
        try {
            val pathWithoutBackslashes = pathForJarFile.replace('\\', '/')
            val regex = """^.*/(.+?.jar)!.*$""".toRegex()
            // the path string could be "... 6d0/junit-jupiter-api-5.6.2.jar!/org/jun ..."
            // i.e. normally something like that above but below is a simple way to make the method        
            // be able to handle e.g. "junit-jupiter-api-5.6.2.jar" by adding the "/" and "!" as prefix and suffix below          
            val matchResult = regex.find("/" + pathWithoutBackslashes + "!", 0)
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
