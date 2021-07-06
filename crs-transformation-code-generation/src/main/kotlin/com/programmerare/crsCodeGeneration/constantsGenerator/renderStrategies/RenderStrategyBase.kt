package com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.DataType

abstract class RenderStrategyBase: RenderStrategy {
    protected val fieldSeparator = "__"

    protected fun getDataTypeForConstantInteger(): DataType {
        return DataType.INTEGER
    }
    protected fun getDataTypeForConstantString(): DataType {
        return DataType.STRING_JAVA
    }
    protected fun getValueForConstantAsInteger(epsgNumber: Int): String {
        return epsgNumber.toString()
    }
    protected fun getValueForConstantAsString(epsgNumber: Int): String {
        return "\"EPSG:" + epsgNumber + "\""
    }
    protected fun getNameForConstantAdjusted(part1: String, part2: String, part3: String): String {
        val nameForConstant = part1 + fieldSeparator + part2 + fieldSeparator + part3
        return nameForConstant.adjusted()
    }
}

fun String.adjusted(): String {
    return this.getUppercasedWithOnylValidCharacters()
}

fun String.getUppercasedWithOnylValidCharacters(): String {
    // the last regexp below just makes sure there are will not be more than two "_" in a row
    return this.uppercase().replace("[^a-zA-Z0-9_]".toRegex(), "_").replace("_{2,}".toRegex(), "__")
}
