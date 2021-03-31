package com.programmerare.crsCodeGeneration

import com.programmerare.crsCodeGeneration.utils.FileUtility
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.sql.Driver

abstract class CodeGeneratorBase {

    protected val freemarkerConfiguration: Configuration

    init {
        verifyJdbcDriver()

        freemarkerConfiguration = Configuration(Configuration.VERSION_2_3_28) // TODO try to update this version
//        freemarkerConfiguration.setDirectoryForTemplateLoading(File(DIRECTORY_FOR_FREEMARKER_TEMPLATES))
        freemarkerConfiguration.setClassForTemplateLoading(javaClass, DIRECTORY_FOR_FREEMARKER_TEMPLATES)
        freemarkerConfiguration.setDefaultEncoding(ENCODING_UTF_8)
        freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
        freemarkerConfiguration.setLogTemplateExceptions(false)
        freemarkerConfiguration.setWrapUncheckedExceptions(true)
    }

    protected fun getJdbcTemplate(): JdbcTemplate {
        val driverManagerDataSource = DriverManagerDataSource(getConnectionStringForEpsgDatabaseMariaDB())
        return JdbcTemplate(driverManagerDataSource)
    }

    private fun verifyJdbcDriver() {
        var driver: Driver? = null
        var throwable: Throwable? = null
        try {
            driver = org.mariadb.jdbc.Driver()
        }
        catch (e: Throwable) {
            throwable = e
        }
        if(driver == null) {
            val message = "Problem with the database driver '${JDBC_DRIVER_CLASS_NAME_MARIADB}'"
            if(throwable == null) {
                throw RuntimeException(message)
            }
            else {
                throw RuntimeException(message, throwable)
            }
        }
    }

    /**
     * @return a file object for the directory "crs-transformation-code-generation"
     */
    fun getDirectoryForCodeGenerationModule(): File {
        val pathToRootDirectoryForClassFiles: String? = CodeGeneratorBase.javaClass.getResource("/").path
        // the path retrieved above is now assumed to be like this:
        // " .../crs-transformation-code-generation/build/classes/kotlin/main/"
        // (and therefore by navigating upwards four directory we should find the directory "crs-transformation-code-generation")
        val rootDirectoryForClassFiles = File(pathToRootDirectoryForClassFiles)
        throwExceptionIfDirectoryDoesNotExist(rootDirectoryForClassFiles)
        
        // val rootDirectoryForModule = rootDirectoryForClassFiles.parentFile.parentFile.parentFile.parentFile
        // Note that there is a unit test for the below FileUtility method 
        val rootDirectoryForModule = FileUtility.getParentDirectoryByNavigatingUpwards(
            rootDirectoryForClassFiles,
            RELATIVE_PATH_TO_SUBDIRECTORIES_WITH_COMPILED_CLASSES, 
            CodeGeneratorBase.NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION
        )
        
        throwExceptionIfDirectoryDoesNotExist(rootDirectoryForModule)
        if(!rootDirectoryForModule.name.equals(NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION)) {
            throw RuntimeException("Assumption about directory structure was not valid. Expected 4 parent directories of the following dir to be named '${NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION}' : " + pathToRootDirectoryForClassFiles)
        }
        return rootDirectoryForModule
    }

    /**
     * @param nameOfModuleDirectory should be e.g. NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION or NAME_OF_MODULE_DIRECTORY_FOR_CONSTANTS
     */
    protected fun getModuleDirectory(nameOfModuleDirectory: String): File {
        val codeGenerationDirectory = getDirectoryForCodeGenerationModule()
        val baseDirectoryWithAllModules = codeGenerationDirectory.parentFile
        val moduleDirectory = File(baseDirectoryWithAllModules, nameOfModuleDirectory)
        throwExceptionIfDirectoryDoesNotExist(moduleDirectory)
        return moduleDirectory
    }

    /**
     * @nameOfModuleDirectory should be e.g. NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION or NAME_OF_MODULE_DIRECTORY_FOR_CONSTANTS
     * @subpathToFileOrDirectoryRelativeToModuleDirectory should be e.g. RELATIVE_PATH_TO_JAVA_FILES
     */
    protected fun getFileOrDirectory(
        nameOfModuleDirectory: String,
        subpathToFileOrDirectoryRelativeToModuleDirectory: String,
        throwExceptionIfNotExisting: Boolean = true
    ): File {
        val baseDir = getModuleDirectory(nameOfModuleDirectory)
        var directoryOrFile = baseDir.resolve(subpathToFileOrDirectoryRelativeToModuleDirectory)
        if(throwExceptionIfNotExisting) throwExceptionIfFileOrDirectoryDoesNotExist(directoryOrFile)
        return directoryOrFile
    }

    protected fun throwExceptionIfFileDoesNotExist(file: File, errorPrefix: String) {
        if(!file.exists()) {
            throw RuntimeException(errorPrefix + file.absolutePath)
        }
        if(!file.isFile()) {
            throw RuntimeException("The path is not a file: " + file.absolutePath)
        }
    }
    protected fun throwExceptionIfDirectoryDoesNotExist(dir: File) {
        if(!dir.exists()) {
            throw RuntimeException("directory does not exist: " + dir.absolutePath)
        }
        if(!dir.isDirectory()) {
            throw RuntimeException("file is not directory: " + dir.absolutePath)
        }
    }

    protected fun throwExceptionIfFileOrDirectoryDoesNotExist(dirOrFile: File) {
        if(!dirOrFile.exists()) {
            throw RuntimeException("File or directory does not exist: " + dirOrFile.absolutePath)
        }
    }

    protected fun createFile(
        nameOfFreemarkerTemplate: String,
        rootHashMapWithDataToBeUsedByFreemarkerTemplate: HashMap<String, Any>,
        fileToBecomeCreated: File
    ) {
        val template = freemarkerConfiguration.getTemplate(nameOfFreemarkerTemplate)
        val outputStreamWriterWithUTF8encoding = OutputStreamWriter(
            FileOutputStream(fileToBecomeCreated),
            Charset.forName(ENCODING_UTF_8).newEncoder()
        )
        template.process(rootHashMapWithDataToBeUsedByFreemarkerTemplate, outputStreamWriterWithUTF8encoding)
        outputStreamWriterWithUTF8encoding.close()
        println("File created: " + fileToBecomeCreated.absolutePath)
    }

    companion object {

        @JvmField
        val NAME_OF_BASE_DIRECTORY_CONTAINING_THE_MODULES = "crsTransformations"
        
        // The above and below constants mean that there are directories like this:
        //      .../crsTransformations/crs-transformation-code-generation
        //      .../crsTransformations/crs-transformation-constants
        //      .../crsTransformations/crs-transformation-adapter-test
        
        @JvmField
        val NAME_OF_MODULE_DIRECTORY_FOR_CODE_GENERATION = "crs-transformation-code-generation"

        @JvmField
        val NAME_OF_MODULE_DIRECTORY_FOR_CONSTANTS = "crs-transformation-constants"

        @JvmField
        val NAME_OF_MODULE_DIRECTORY_FOR_TESTS = "crs-transformation-adapter-test"

        @JvmField
        val RELATIVE_PATH_TO_SRC_MAIN = "src/main"
        
        @JvmField
        val RELATIVE_PATH_TO_JAVA_FILES = "$RELATIVE_PATH_TO_SRC_MAIN/java"
//        @JvmStatic
//        protected val RELATIVE_PATH_TO_KOTLIN_FILES = "src/main/kotlin"

        @JvmField
        val RELATIVE_PATH_TO_RESOURCES_DIRECTORY = "src/main/resources"

        @JvmField
        val RELATIVE_PATH_TO_SUBDIRECTORIES_WITH_COMPILED_CLASSES = listOf(
            "build/classes/kotlin/main",
            "build/classes/kotlin/test",            
            // The purpose of these relative paths above is to use them for 
            // 'navigation upwards' to the base directory of the module for the code generation 
            // i.e. the directory with the name 'crs-transformation-code-generation'
            // but if they would not work with paths as above
            // then at least some of these below should work 
            // i.e. try to navigate up 3, 4 or 5 directories, regardless of the name,
            // and then the cdoe will check to verify that the destination is a 
            // directory named 'crs-transformation-code-generation'
            "*/*/*",
            "*/*/*/*",
            "*/*/*/*/*",
        )        

        @JvmField
        val RELATIVE_PATH_TO_TARGET_DIRECTORY_FOR_GENERATED_CODE_WITHIN_RESOURCES_DIRECTORY = RELATIVE_PATH_TO_RESOURCES_DIRECTORY + "/generated"

        @JvmField
        val ENCODING_UTF_8 = "UTF-8"

       @JvmField
        val DIRECTORY_FOR_FREEMARKER_TEMPLATES = "/freemarker_templates" // means the directory ".../src/main/resources/freemarker_templates"


        // TODO maybe move the database code below to the subclass using that kind of code
        protected var _databaseName: String = "epsg_version_NotYetDefined"
        protected var _databaseUserName: String = "TheUserNameIsNotSet"
        protected var _databaseUserPassword: String = "ThePasswordIsNotSet"
        // the values for the above fields should be set by a main method
        // throgh invoking the method below
        public fun setDatabaseInformationForMariaDbConnection(
            databaseName: String,
            databaseUserName: String,
            databaseUserPassword: String
        ) {
            _databaseName = databaseName
            _databaseUserName = databaseUserName
            _databaseUserPassword = databaseUserPassword
        }

        protected fun getConnectionStringForEpsgDatabaseMariaDB(): String {
            return "jdbc:mariadb://localhost:3306/" + _databaseName + "?user=" + _databaseUserName + "&password=" + _databaseUserPassword
        }

        @JvmField
        val JDBC_DRIVER_CLASS_NAME_MARIADB = "org.mariadb.jdbc.Driver"

        // The SQL below works for the downloaded database MySQL/MariaDB database
        // (but had slightly different names in the previously used MS Access database)
        // for example the table names are using the prefix "epsg_" in the MySQL database but not in the MS Access database.
//        @JvmField val SQL_STATEMENT_SELECTING_CRSCODE_CRSNAME_AREANAME_old_database_before_version_10 = """
//            SELECT
//                area.area_name,
//                crs.area_of_use_code,
//                crs.coord_ref_sys_code,
//                crs.coord_ref_sys_name
//            FROM
//                epsg_coordinatereferencesystem AS crs
//                INNER JOIN
//                epsg_area AS area
//                    ON
//                    crs.area_of_use_code = area.area_code
//        """
        // Below is the SQL statement for EPSG database model version 10 (the above SQL worked until version 9)
        @JvmField val SQL_STATEMENT_SELECTING_CRSCODE_CRSNAME_AREANAME = """
            SELECT
                extent.extent_name, -- area.area_name,
                extent.extent_code, -- crs.area_of_use_code,
                crs.coord_ref_sys_code,
                crs.coord_ref_sys_name            
            FROM
                epsg_coordinatereferencesystem AS crs
                INNER JOIN 
                epsg_usage AS usag 
                    ON crs.coord_ref_sys_code = usag.object_code 
                    AND usag.object_table_name = 'epsg_coordinatereferencesystem'
                INNER JOIN 
                epsg_extent AS extent 
                    ON extent.extent_code = usag.extent_code 
        """
        @JvmField val SQL_COLUMN_CRSCODE = "coord_ref_sys_code"
        @JvmField val SQL_COLUMN_CRSNAME = "coord_ref_sys_name"
        @JvmField val SQL_COLUMN_AREANAME = "extent_name"// before version 10: "area_name"
        @JvmField val SQL_COLUMN_AREACODE = "extent_code"// before version 10: "area_of_use_code"
    }
}
