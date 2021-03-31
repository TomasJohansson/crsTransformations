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
    
// TODO use the baseDirectory instead of the less readable (more difficult to understand)
// function parameter defined in the interface ProgrammingLanguageStrategy:  
    //fun getDirectoryWhereTheClassFilesShouldBeGenerated(
    //  getFileOrDirectoryFunction: 
        //  ( 
            //  nameOfModuleDirectory: String,
            //  subpathToFileOrDirectoryRelativeToModuleDirectory: String,
            //  throwExceptionIfNotExisting: Boolean
        //  ) -> File
    //): File
    
): ProgrammingLanguageStrategy {
    override fun getCustomFile(file: File): File {
        return file;
    }
}
