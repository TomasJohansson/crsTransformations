package com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.DataType

class RenderStrategyNameNumberAreaString: RenderStrategyBase() , RenderStrategy {
    override fun getValueForConstant(epsgNumber: Int): String {
        return super.getValueForConstantAsString(epsgNumber)
    }

    override fun getDataTypeForConstant(): DataType {
        return super.getDataTypeForConstantString()
    }

    override fun getNameForConstant(crsName: String, areaName: String, epsgNumber: Int): String {
        return super.getNameForConstantAdjusted(crsName, epsgNumber.toString(), areaName)
    }
}
