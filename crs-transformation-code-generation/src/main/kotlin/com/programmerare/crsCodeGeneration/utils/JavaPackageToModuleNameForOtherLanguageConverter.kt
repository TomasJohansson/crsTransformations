package com.programmerare.crsCodeGeneration.utils

object JavaPackageToModuleNameForOtherLanguageConverter {

    private val regexMatchingVersionPart = Regex("""v[_\d]+""")

    /**
     * @param nameOfJavaPackage e.g. "com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9"
     * @return something like "Programmerare.CrsConstants.ConstantsByAreaNameNumber.v9_8_9"
     */
    fun getAsNameOfCSharpeNameSpace(nameOfJavaPackage: String): String {
        val parts = nameOfJavaPackage.split('.')
        val sb = StringBuilder()
        for(i in parts.indices) {
            val part = parts[i]
            if(i == 0 && part == "com") continue;
            if(!sb.isEmpty()) {
                sb.append(".")
            }
            if(regexMatchingVersionPart.matches(part)) {
                sb.append(part)
            }
            else {
                sb.append(part.capitalize())
            }
        }
        return sb.toString()
    }
    // actually the "dart module" name will rather be used for the path to the generated file
    // for example the java package name:
    // "com.programmerare.crsConstants.constantsByAreaNameNumber.v9_9_1"
    // will just use the last part with the version and then return "crs_constants.v9_9_1"
    // which will NOT be used for a "namespace/module/package" name in Dart 
    // but will be used for creating the directory path "crs_constants/v9_9_1"
    // since code for other languages do it like that ...
    fun getAsNameOfDartModule(nameOfJavaPackage: String): String {
        val lastIndexOf = nameOfJavaPackage.lastIndexOf('.');
        return "crs_constants" + nameOfJavaPackage.substring(lastIndexOf);
    }

    fun getAsNameOfTypeScriptModule(nameOfJavaPackage: String): String {
        // simply do the same thing for TypeScript as Dart: 
        return getAsNameOfDartModule(nameOfJavaPackage)
    }    
}
