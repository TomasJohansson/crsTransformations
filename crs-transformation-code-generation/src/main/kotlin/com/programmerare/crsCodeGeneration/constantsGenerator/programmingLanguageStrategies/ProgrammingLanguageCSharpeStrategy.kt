package com.programmerare.crsCodeGeneration.constantsGenerator.programmingLanguageStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies.RenderStrategy
import com.programmerare.crsCodeGeneration.CodeGeneratorBase
import com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies.RenderStrategyDecoratorForCSharpe
import com.programmerare.crsCodeGeneration.utils.JavaPackageToModuleNameForOtherLanguageConverter
import java.io.File

class ProgrammingLanguageCSharpeStrategy(baseDirectory: File):
    ProgrammingLanguageStrategyBase(baseDirectory),
    ProgrammingLanguageStrategy
{
    override fun getRenderStrategy(renderStrategy: RenderStrategy): RenderStrategy {
        // purpose: render "string" (C#) instead of "String" (Java)
        return RenderStrategyDecoratorForCSharpe(renderStrategy)
    }
    override fun getNameOfFreemarkerTemplateForConstants(): String {
        // public const val NAME_OF_FREEMARKER_TEMPLATE_FILE_FOR_CSHARPE_CONSTANTS = "ConstantsCSharpe.ftlh"
        return "ConstantsCSharpe.ftlh"
    }
    override fun getDirectoryWhereTheClassFilesShouldBeGenerated(): File {
        return File(baseDirectory, "/csharpe_constants")      
    }
    override fun getNameOfPackageOrNamespaceToBeGenerated(nameOfJavaPackage: String): String {
        return JavaPackageToModuleNameForOtherLanguageConverter.getAsNameOfCSharpeNameSpace(
            nameOfJavaPackage
        )
    }
    override fun getFileExtensionForClassFile(): String {
        // val FILE_EXTENSION_FOR_CSHARPE_FILE = ".cs"
        return ".cs"
    }
}
