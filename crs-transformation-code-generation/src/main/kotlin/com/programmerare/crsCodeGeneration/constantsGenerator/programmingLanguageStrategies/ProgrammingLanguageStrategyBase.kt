package com.programmerare.crsCodeGeneration.constantsGenerator.programmingLanguageStrategies

import java.io.File

abstract class ProgrammingLanguageStrategyBase(
    protected val baseDirectory: File
    // The 'baseDirectory' will be two different directories,
    // one for the Java constants, and another base directory for the others, as explained below. 
    // For most ProgrammingLanguageStrategy implementations
    // the constants file will be generated within the directory 
    //  "...\crs-transformation-code-generation\src\main\resources\generated"
    // but the exception is the java constants which will be generated directly into 
    // a module since it is also used within this multi-moduled gradle kotlin/java project.
    // The 'ProgrammingLanguageJavaStrategy' will instead use the below baseDirectory: 
    // ".../crs-transformation-constants/src/main/java"
    
): ProgrammingLanguageStrategy {
    override fun getCustomFile(file: File): File {
        return file;
    }
}
