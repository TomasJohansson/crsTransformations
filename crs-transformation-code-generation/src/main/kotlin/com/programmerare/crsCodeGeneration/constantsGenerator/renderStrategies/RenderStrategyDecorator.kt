package com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.DataType

abstract class RenderStrategyDecorator(private val renderStrategy: RenderStrategy): RenderStrategy {
    override fun getValueForConstant(epsgNumber: Int): String {
        return renderStrategy.getValueForConstant(epsgNumber)
    }
    override fun getDataTypeForConstant(): DataType {
        return renderStrategy.getDataTypeForConstant()
    }
    override fun getNameForConstant(crsName: String, areaName: String, epsgNumber: Int): String {
        return renderStrategy.getNameForConstant(crsName, areaName, epsgNumber)
    }
}
