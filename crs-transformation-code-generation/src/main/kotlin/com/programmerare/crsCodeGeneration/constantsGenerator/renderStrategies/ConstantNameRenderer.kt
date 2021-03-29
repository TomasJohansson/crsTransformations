package com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.DataType

class ConstantNameRenderer(var renderStrategy: RenderStrategy) : RenderStrategy {
    override fun getValueForConstant(epsgNumber: Int): String {
        return renderStrategy.getValueForConstant(epsgNumber)
    }

    override fun getNameForConstant(crsName: String, areaName: String, epsgNumber: Int): String {
        return renderStrategy.getNameForConstant(crsName, areaName, epsgNumber)
    }

    override fun getDataTypeForConstant(): DataType {
        return renderStrategy.getDataTypeForConstant()
    }

    private val fieldSeparator = "__"
}
