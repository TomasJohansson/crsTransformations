package com.programmerare.crsTransformations.utils

import com.programmerare.crsTransformations.utils.StringUtility.getJarFileNameWithoutThePath
import org.slf4j.LoggerFactory
import java.lang.RuntimeException

internal object JarFileFinder {
    private val logger = LoggerFactory.getLogger(JarFileFinder::class.java)
    
    @JvmStatic
    fun getNameOfJarFile(theTestedClass: Class<*>?): String {
        val res1 = getNameOfJarFileUsingProtectionDomainCodeSource(theTestedClass)
        if(!res1.equals("")) {
            // logger.info("JarFileFinder#getNameOfJarFileUsingProtectionDomainCodeSource " + res1)
            // this seems to work from Java, Kotlin, Scala, Groovy, JRuby
            // but NOT from Jython !
            return res1
        }
        val res2 = getNameOfJarFileUsingMethodGetResource(theTestedClass)
        // this seems to work from Jython !
        // When the below logger statement was activated, it printed out e.g. res2 "geopackage-core-3.5.0.jar"
        // when running from Jython i.e. the above "res1" had failed but not this alternative method
        // logger.info("JarFileFinder#getNameOfJarFileUsingMethodGetResource " + res2)
        return res2
    }
    
    @JvmStatic
    fun getNameOfJarFileUsingMethodGetResource(theTestedClass: Class<*>?): String {
        try {
            val url = theTestedClass!!.getResource(theTestedClass.simpleName + ".class")
            val externalForm = url.toExternalForm()
            return getJarFileNameWithoutThePath(externalForm)            
        }
        catch(e: Throwable) {
            return ""
        }
    }
    
    @JvmStatic
    fun getNameOfJarFileUsingProtectionDomainCodeSource(theTestedClass: Class<*>?): String {
        try {
            val externalForm = theTestedClass!!.protectionDomain.codeSource.location.toExternalForm()
            return getJarFileNameWithoutThePath(externalForm)
        }
        catch(e: Throwable) {
            return ""
        }        
    }
}
