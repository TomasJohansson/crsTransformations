package com.programmerare.crsCodeGeneration.constantsGenerator.programmingLanguageStrategies

import com.programmerare.crsCodeGeneration.constantsGenerator.renderStrategies.RenderStrategy
import java.io.File

// ---------------------------------------------------------------------
// interface for programming language (Java vs C#) specific differences
// and their implementations for Java and C#
interface ProgrammingLanguageStrategy {
    fun getNameOfFreemarkerTemplateForConstants(): String
    
    fun getDirectoryWhereTheClassFilesShouldBeGenerated(): File
    
    fun getRenderStrategy(renderStrategy: RenderStrategy): RenderStrategy
    fun getNameOfPackageOrNamespaceToBeGenerated(nameOfJavaPackage: String): String
    fun getFileExtensionForClassFile(): String
    
    // can be used if an implementation want to modify the path or file name
    // compared to the default generated path and file, with the path being based 
    // on the java package, and the file name the same as the class name.
    // This method was added when support for Dart was added,
    // which do not use the same package concept as Java/Kotlin (which is similar to the namespace for C#/F#)
    fun getCustomFile(file: File): File
}
