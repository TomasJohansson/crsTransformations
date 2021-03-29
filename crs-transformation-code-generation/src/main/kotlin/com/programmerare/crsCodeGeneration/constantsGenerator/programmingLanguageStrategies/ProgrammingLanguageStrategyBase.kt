package com.programmerare.crsCodeGeneration.constantsGenerator.programmingLanguageStrategies

import java.io.File

abstract class ProgrammingLanguageStrategyBase: ProgrammingLanguageStrategy {
    override fun getCustomFile(file: File): File {
        return file;
    }
}
