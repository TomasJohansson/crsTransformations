package com.programmerare.crsCodeGeneration.constantsGenerator.programmingLanguageStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies.RenderStrategy
import com.programmerare.crsCodeGeneration.CodeGeneratorBase
import com.programmerare.crsCodeGeneration.constantsGenerator.ConstantClassGenerator
import com.programmerare.crsCodeGeneration.utils.JavaPackageToModuleNameForOtherLanguageConverter
import java.io.File

class ProgrammingLanguageDartStrategy: ProgrammingLanguageStrategyBase(),
    ProgrammingLanguageStrategy {
    //                // will generate a file at this kind of path:
    //                //          (but of course the version number may change from the example in below)  
    //                // ... src/main/resources/generated/dart_constants/crs_constants/v9_9_1/epsg_number.dart
    //                constantClassGenerator.generateFilesWithDartConstants()        
    override fun getRenderStrategy(renderStrategy: RenderStrategy): RenderStrategy {
        return renderStrategy
    }
    override fun getNameOfFreemarkerTemplateForConstants(): String {
        // public const val NAME_OF_FREEMARKER_TEMPLATE_FILE_FOR_DART_CONSTANTS = "ConstantsDart.ftlh"
        return "ConstantsDart.ftlh"
    }
    override fun getDirectoryWhereTheClassFilesShouldBeGenerated(
        getFileOrDirectoryFunction: (nameOfModuleDirectory: String, subpathToFileOrDirectoryRelativeToModuleDirectory: String, throwExceptionIfNotExisting: Boolean) -> File
    ): File {
        return getFileOrDirectoryFunction(CodeGeneratorBase.NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION, CodeGeneratorBase.RELATIVE_PATH_TO_TARGET_DIRECTORY_FOR_GENERATED_CODE_WITHIN_RESOURCES_DIRECTORY + "/dart_constants", false) // throwExceptionIfNotExisting
    }
    override fun getNameOfPackageOrNamespaceToBeGenerated(nameOfJavaPackage: String): String {
        return JavaPackageToModuleNameForOtherLanguageConverter.getAsNameOfDartModule(
            nameOfJavaPackage
        )
    }
    override fun getFileExtensionForClassFile(): String {
        return CodeGeneratorBase.FILE_EXTENSION_FOR_DART_FILE
    }
    override fun getCustomFile(file: File): File {
        return if(file.name.startsWith(ConstantClassGenerator.CLASS_NAME_INTEGER_CONSTANTS)) {
            File(file.parentFile, "epsg_number.dart")
        }
        else {
            file
        }
    }        
}
