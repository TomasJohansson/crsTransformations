package com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.DataType

interface RenderStrategy {
    fun getValueForConstant(epsgNumber: Int): String
    fun getDataTypeForConstant(): DataType
    fun getNameForConstant(crsName: String, areaName: String, epsgNumber: Int): String
}
