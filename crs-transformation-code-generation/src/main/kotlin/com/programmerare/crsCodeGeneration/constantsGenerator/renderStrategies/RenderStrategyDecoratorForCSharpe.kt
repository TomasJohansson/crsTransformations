package com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.DataType

class RenderStrategyDecoratorForCSharpe(private val renderStrategy: RenderStrategy): RenderStrategyDecorator(renderStrategy) {
    override fun getDataTypeForConstant(): DataType {
        if(super.getDataTypeForConstant() == DataType.STRING_JAVA) {
            return DataType.STRING_CSHARPE
        }
        else {
            return super.getDataTypeForConstant()
        }
    }
}
