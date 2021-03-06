package com.programmerare.crsCodeGeneration.constantsGenerator.programmingLanguageStrategies

import com.programmerare.crsCodeGeneration.CodeGeneratorBase
import com.programmerare.crsCodeGeneration.constantsGenerator.ConstantClassGenerator
import com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies.RenderStrategy
import com.programmerare.crsCodeGeneration.utils.JavaPackageToModuleNameForOtherLanguageConverter
import java.io.File

class ProgrammingLanguagePythonStrategy(baseDirectory: File):
    ProgrammingLanguageStrategyBase(baseDirectory),
    ProgrammingLanguageStrategy
{
    //                // will generate a file at this kind of path:
    //                //          (but of course the version number may change from the example in below)                
    //                // ... src/main/resources/generated/python_constants/crs_constants/v10_011/epsg_number.py
    override fun getRenderStrategy(renderStrategy: RenderStrategy): RenderStrategy {
        return renderStrategy
    }
    override fun getNameOfFreemarkerTemplateForConstants(): String {
        // public const val NAME_OF_FREEMARKER_TEMPLATE_FILE_FOR_PYTHON_CONSTANTS = "ConstantsPython.ftlh"
        return "ConstantsPython.ftlh"
    }
    override fun getDirectoryWhereTheClassFilesShouldBeGenerated(): File {
        return File(baseDirectory, "/python_constants")
    }
    override fun getNameOfPackageOrNamespaceToBeGenerated(nameOfJavaPackage: String): String {
        return JavaPackageToModuleNameForOtherLanguageConverter.getAsNameOfPythonModule(
            nameOfJavaPackage
        )
    }
    override fun getFileExtensionForClassFile(): String {
        // val FILE_EXTENSION_FOR_PYTHON_FILE = ".py"
        return ".py"
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
