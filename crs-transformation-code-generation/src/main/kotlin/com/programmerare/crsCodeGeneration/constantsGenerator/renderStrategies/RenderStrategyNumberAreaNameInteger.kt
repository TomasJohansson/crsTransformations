package com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.DataType

class RenderStrategyNumberAreaNameInteger: RenderStrategyBase() , RenderStrategy {
    override fun getValueForConstant(epsgNumber: Int): String {
        return super.getValueForConstantAsInteger(epsgNumber)
    }

    override fun getDataTypeForConstant(): DataType {
        return super.getDataTypeForConstantInteger()
    }

    override fun getNameForConstant(crsName: String, areaName: String, epsgNumber: Int): String {
        return super.getNameForConstantAdjusted("_" + epsgNumber.toString(), areaName, crsName)
    }
}
