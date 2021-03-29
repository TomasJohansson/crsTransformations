package com.programmerare.crsCodeGeneration.constantsGenerator

import com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies.ConstantNameRenderer

/**
 * It might seem as if this class is never being used when looking at "grayed"
 * method names in an IDE such as IntelliJ IDEA.
 * However, note that this class is used from a freemarker template !
 * ( .\crsCodeGeneration\src\main\resources\freemarker_templates\ConstantsJava.ftlh )
 */
class ConstantTypeNameValue(
    private val constantNameRenderer: ConstantNameRenderer,
    val epsgNumber: Int,
    val areaName: String,
    val crsName: String
) {
    fun getDocumentationCommentForConstant(): String {
        return "EPSG:" + epsgNumber + " , " + crsName + " , " + areaName
    }

    fun getValueForConstant(): String {
        return constantNameRenderer.getValueForConstant(epsgNumber)
    }

    fun getNameForConstant(): String {
        return constantNameRenderer.getNameForConstant(crsName, areaName, epsgNumber)
    }

    fun getDataTypeForConstant(): DataType {
        return constantNameRenderer.getDataTypeForConstant()
    }
}
