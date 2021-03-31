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
    override fun getDirectoryWhereTheClassFilesShouldBeGenerated(getFileOrDirectoryFunction: (String, String, throwExceptionIfNotExisting: Boolean) -> File): File {
          
        // // protected fun getFileOrDirectory(nameOfModuleDirectory: String, subpathToFileOrDirectoryRelativeToModuleDirectory: String, throwExceptionIfNotExisting: Boolean = true): File {
        //return getFileOrDirectory(NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION, RELATIVE_PATH_TO_TARGET_DIRECTORY_FOR_GENERATED_CODE_WITHIN_RESOURCES_DIRECTORY + "/csharpe_constants", throwExceptionIfNotExisting = false)
        return getFileOrDirectoryFunction(CodeGeneratorBase.NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION, CodeGeneratorBase.RELATIVE_PATH_TO_TARGET_DIRECTORY_FOR_GENERATED_CODE_WITHIN_RESOURCES_DIRECTORY + "/csharpe_constants", false)
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
