package com.programmerare.crsCodeGeneration.constantsGenerator.programmingLanguageStrategies

import com.programmerare.crsCodeGeneration.CodeGeneratorBase
import com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies.RenderStrategy
import java.io.File

class ProgrammingLanguageJavaStrategy(baseDirectory: File):
    ProgrammingLanguageStrategyBase(baseDirectory),
    ProgrammingLanguageStrategy
{
    override fun getRenderStrategy(renderStrategy: RenderStrategy): RenderStrategy {
        return renderStrategy
    }
    override fun getNameOfFreemarkerTemplateForConstants(): String {
        // public const val NAME_OF_FREEMARKER_TEMPLATE_FILE_FOR_CSHARPE_CONSTANTS = "ConstantsCSharpe.ftlh"
        return "ConstantsJava.ftlh"
    }
    override fun getDirectoryWhereTheClassFilesShouldBeGenerated(
        getFileOrDirectoryFunction: (nameOfModuleDirectory: String, subpathToFileOrDirectoryRelativeToModuleDirectory: String, throwExceptionIfNotExisting: Boolean) -> File
    ): File {
        return getFileOrDirectoryFunction(
            CodeGeneratorBase.NAME_OF_MODULE_DIRECTORY_FOR_CONSTANTS,
            CodeGeneratorBase.RELATIVE_PATH_TO_JAVA_FILES, false)
    }
    override fun getNameOfPackageOrNamespaceToBeGenerated(nameOfJavaPackage: String): String {
        return nameOfJavaPackage;
    }
    override fun getFileExtensionForClassFile(): String {
        // val FILE_EXTENSION_FOR_JAVA_FILE = ".java"
        return ".java"
    }
}
