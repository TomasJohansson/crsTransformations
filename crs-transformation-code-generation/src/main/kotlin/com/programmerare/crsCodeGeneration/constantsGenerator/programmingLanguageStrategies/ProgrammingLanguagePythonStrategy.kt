package com.programmerare.crsCodeGeneration.constantsGenerator.programmingLanguageStrategies

import com.programmerare.crsCodeGeneration.CodeGeneratorBase
import com.programmerare.crsCodeGeneration.constantsGenerator.ConstantClassGenerator
import com.programmerare.crsCodeGeneration.constantsGenerator.RenderStrategy
import com.programmerare.crsCodeGeneration.utils.JavaPackageToModuleNameForOtherLanguageConverter
import java.io.File

class ProgrammingLanguagePythonStrategy: ProgrammingLanguageStrategyBase(),
    ProgrammingLanguageStrategy {
    //                // will generate a file at this kind of path:
    //                //          (but of course the version number may change from the example in below)                
    //                // ... src/main/resources/generated/python_constants/crs_constants/v10_011/epsg_number.py
    override fun getRenderStrategy(renderStrategy: RenderStrategy): RenderStrategy {
        return renderStrategy
    }
    override fun getNameOfFreemarkerTemplateForConstants(): String {
        return ConstantClassGenerator.NAME_OF_FREEMARKER_TEMPLATE_FILE_FOR_PYTHON_CONSTANTS
    }
    override fun getDirectoryWhereTheClassFilesShouldBeGenerated(
        getFileOrDirectoryFunction: (nameOfModuleDirectory: String, subpathToFileOrDirectoryRelativeToModuleDirectory: String, throwExceptionIfNotExisting: Boolean) -> File
    ): File {
        return getFileOrDirectoryFunction(CodeGeneratorBase.NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION, CodeGeneratorBase.RELATIVE_PATH_TO_TARGET_DIRECTORY_FOR_GENERATED_CODE_WITHIN_RESOURCES_DIRECTORY + "/python_constants", false) // throwExceptionIfNotExisting
    }
    override fun getNameOfPackageOrNamespaceToBeGenerated(nameOfJavaPackage: String): String {
        return JavaPackageToModuleNameForOtherLanguageConverter.getAsNameOfPythonModule(
            nameOfJavaPackage
        )
    }
    override fun getFileExtensionForClassFile(): String {
        return CodeGeneratorBase.FILE_EXTENSION_FOR_PYTHON_FILE
    }
    override fun getCustomFile(file: File): File {
        return if(file.name.startsWith(ConstantClassGenerator.CLASS_NAME_INTEGER_CONSTANTS)) {
            File(file.parentFile, "epsg_number.py")
        }
        else {
            file
        }
    }
}
