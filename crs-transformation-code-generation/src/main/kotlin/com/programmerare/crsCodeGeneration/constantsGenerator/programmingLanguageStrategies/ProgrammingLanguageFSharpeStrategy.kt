package com.programmerare.crsCodeGeneration.constantsGenerator.programmingLanguageStrategies

import com.programmerare.crsCodeGeneration.CodeGeneratorBase
import com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies.RenderStrategy
import com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies.RenderStrategyDecoratorForCSharpe
import com.programmerare.crsCodeGeneration.utils.JavaPackageToModuleNameForOtherLanguageConverter
import java.io.File

class ProgrammingLanguageFSharpeStrategy(baseDirectory: File):
    ProgrammingLanguageStrategyBase(baseDirectory),
    ProgrammingLanguageStrategy
{
    override fun getRenderStrategy(renderStrategy: RenderStrategy): RenderStrategy {
        // purpose: render "string" (F# or C#) instead of "String" (Java)
        return RenderStrategyDecoratorForCSharpe(renderStrategy) // string in F# too so can reuse the same Strategy as C# string
    }
    override fun getNameOfFreemarkerTemplateForConstants(): String {
        // public const val NAME_OF_FREEMARKER_TEMPLATE_FILE_FOR_FSHARPE_CONSTANTS = "ConstantsFSharpe.ftlh"
        return "ConstantsFSharpe.ftlh"
    }
    override fun getDirectoryWhereTheClassFilesShouldBeGenerated(): File {
        return File(baseDirectory, "/fsharpe_constants")
    }
    override fun getNameOfPackageOrNamespaceToBeGenerated(nameOfJavaPackage: String): String {
        // same package name for F# as C# so therefore can reuse the class below 
        return JavaPackageToModuleNameForOtherLanguageConverter.getAsNameOfCSharpeNameSpace(
            nameOfJavaPackage
        )
    }
    override fun getFileExtensionForClassFile(): String {
        // val FILE_EXTENSION_FOR_FSHARPE_FILE = ".fs"
        return ".fs"
    }
}
