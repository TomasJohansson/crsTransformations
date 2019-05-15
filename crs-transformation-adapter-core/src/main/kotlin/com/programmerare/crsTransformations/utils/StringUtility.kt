package com.programmerare.crsTransformations.utils

internal object StringUtility {
    
    private val regularExpressionForExtractionOfLastNumericalValue = Regex("""^.*?((\d|\d\.)*\d)+[^\d]*$""")
    
    private val returnValueIfNoNumericalValueIsFound = "?"
    
    @JvmStatic
    fun getLastNumericalValueFromString(inputString: String?): String {
        if(inputString == null) return returnValueIfNoNumericalValueIsFound
        val matchGroupCollection: MatchGroupCollection? = regularExpressionForExtractionOfLastNumericalValue.matchEntire(inputString)?.groups
        if(matchGroupCollection == null) return returnValueIfNoNumericalValueIsFound
        return matchGroupCollection.get(1)?.value!!
        //val res: String = matchGroupCollection.get(1)?.value!!
        //println(res)
        //return res
    }    
}