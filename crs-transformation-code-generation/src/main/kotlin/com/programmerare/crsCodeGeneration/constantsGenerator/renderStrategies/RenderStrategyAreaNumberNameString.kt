package com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.DataType

class RenderStrategyAreaNumberNameString: RenderStrategyBase() , RenderStrategy {
    override fun getValueForConstant(epsgNumber: Int): String {
        return super.getValueForConstantAsString(epsgNumber)
    }

    override fun getDataTypeForConstant(): DataType {
        return super.getDataTypeForConstantString()
    }

    override fun getNameForConstant(crsName: String, areaName: String, epsgNumber: Int): String {
        return super.getNameForConstantAdjusted(areaName, epsgNumber.toString(), crsName)
    }
}
