package com.programmerare.crsTransformations.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JarFileFinderTest {

    private final String expectedNameOfTestedJarFile = "junit-jupiter-api-5.8.2.jar";
    private final Class theTestedClass = Test.class;

    @Test
    void getNameOfJarFileUsingProtectionDomainCodeSource() {
        // this seems to work from Java, Kotlin, Scala, Groovy, JRuby
        // but NOT from Jython !        
        assertEquals(
            expectedNameOfTestedJarFile,
            JarFileFinder.getNameOfJarFileUsingProtectionDomainCodeSource(theTestedClass)
        );
    }
    // if the runtime code tries both of the above and below tested methods 
    // then it should work from most JVM languages 
    @Test
    void getNameOfJarFileUsingMethodGetResource() {
        // this seems to work from Jython ! (and also for example when testing the Kotlin code here from Java test)        
        assertEquals(
            expectedNameOfTestedJarFile,
            JarFileFinder.getNameOfJarFileUsingMethodGetResource(theTestedClass)
        );
    }

}
