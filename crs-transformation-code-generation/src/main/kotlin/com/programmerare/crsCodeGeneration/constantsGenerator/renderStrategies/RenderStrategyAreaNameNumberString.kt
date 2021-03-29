package com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.DataType

class RenderStrategyAreaNameNumberString: RenderStrategyBase() , RenderStrategy {
    override fun getValueForConstant(epsgNumber: Int): String {
        return super.getValueForConstantAsString(epsgNumber)
    }

    override fun getDataTypeForConstant(): DataType {
        return super.getDataTypeForConstantString()
    }

    override fun getNameForConstant(crsName: String, areaName: String, epsgNumber: Int): String {
        return super.getNameForConstantAdjusted(areaName, crsName, epsgNumber.toString())
    }
}
