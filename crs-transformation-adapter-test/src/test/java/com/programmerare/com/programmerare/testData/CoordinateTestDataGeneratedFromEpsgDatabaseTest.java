package com.programmerare.com.programmerare.testData;

import com.google.common.io.Resources; // library "com.google.guava:guava" 
import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_8_9.EpsgNumber;
import com.programmerare.crsTransformationAdapterGeoPackageNGA.CrsTransformationAdapterGeoPackageNGA;
import com.programmerare.crsTransformationAdapterGeoTools.CrsTransformationAdapterGeoTools;
import com.programmerare.crsTransformationAdapterGooberCTL.CrsTransformationAdapterGooberCTL;
import com.programmerare.crsTransformationAdapterOrbisgisCTS.CrsTransformationAdapterOrbisgisCTS;
import com.programmerare.crsTransformationAdapterProj4J.CrsTransformationAdapterProj4J;
import com.programmerare.crsTransformationAdapterProj4jLocationtech.CrsTransformationAdapterProj4jLocationtech;
import com.programmerare.crsTransformations.compositeTransformations.CrsTransformationAdapterCompositeFactory;
import com.programmerare.crsTransformations.coordinate.CrsCoordinate;
import com.programmerare.crsTransformations.CrsTransformationAdapter;
import com.programmerare.crsTransformations.CrsTransformationResult;
import com.programmerare.crsTransformations.coordinate.CrsCoordinateFactory;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifier;
import com.programmerare.crsTransformations.crsIdentifier.CrsIdentifierFactory;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The CSV file used in this test:
 *  src/test/resources/generated/CoordinateTestDataGeneratedFromEpsgDatabase.csv
 * The above file has been created with the following class:
 *  \crsCodeGeneration\src\main\kotlin\com\programmerare\crsCodeGeneration\coordinateTestDataGenerator\CoordinateTestDataGenerator.kt
 * The relevant columns are the first column (EPSG code) and the last two column with WGS84 coordinates.
 * The WGS84 coordinate defines the "centroid" within an area where some other coordinate
 * system is used (and that other coordinate system is defined by the EPSG code in the first column)
 * Thus the file defines a list of appropriate WGS84 coordinates which can be transformed back and forth
 * to/from the coordinate system in the first EPSG column.
 */
@Disabled // you may want to temporary change this line if you want to run the "tests"     
// (and also see comments in the class TestCategory regarding that this "test" file creates files and produces output to the console)
class CoordinateTestDataGeneratedFromEpsgDatabaseTest {

    private final static String OUTPUT_DIRECTORY_FOR_REGRESSION_RESULTS = "src/test/resources/regression_results";
    private final static String OUTPUT_DIRECTORY_FOR_GENERATED_FILES = "src/test/resources/generated";

    // the below file is used with method 'Resources.getResource' (the path is test/resources/generated/CoordinateTestDataGeneratedFromEpsgDatabase.csv )
    // and the content (columns) of the file are as below:
    // e.g. "3006|1225|Sweden|17.083659606206545|61.98770256318016"
    //      epsgCrsCode | epsgAreaCode | epsgAreaName | centroidX | centroidY
    private final static String INPUT_TEST_DATA_FILE = "generated/CoordinateTestDataGeneratedFromEpsgDatabase.csv";


    private boolean createNewRegressionFile = true;

    private final static double DELTA_LIMIT_FOR_SUCCESS = 0.0001;

    private static List<EpsgCrsAndAreaCodeWithCoordinates> list;

    @BeforeAll
    static void before() {
        list = getCoordinatesFromGeneratedCsvFile();
    }

    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------
    // Below: "tests" labeled with 'TestCategory.SideEffectFileCreation'

    // To run all tests excluding tests labeled with @Tag("SlowTest")
    // as below, in IntelliJ IDEA:
    // Run --> Edit configuration --> Junit --> Test kind --> Tags --> Tag expression: !SlowTest

    @Test
    // @Tag("SlowTest") // actually not at all slow but very fast since very few coordinate systems are supported
    @Tag(TestCategory.SideEffectFileCreation) // test/resources/regression_results/
    void testAllTransformationsInGeneratedCsvFileWithGoober() {
        TestResult testResultForGoober = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationAdapterGooberCTL(), list);
        handleTestResults(
            testResultForGoober,
            DELTA_LIMIT_FOR_SUCCESS,
            createNewRegressionFile,
            "_version_1.1" // build.gradle: implementation("com.github.goober:coordinate-transformation-library:1.1")
        );
    }

    @Test
    // @Disabled
    @Tag(TestCategory.SlowTest) // e.g. 224 seconds for this test method while all other (approx 80) tests (except those in this test class) take about one minute
    @Tag(TestCategory.SideEffectFileCreation) // test/resources/regression_results/
    void testAllTransformationsInGeneratedCsvFileWithGeoTools() {
        //    seconds: 224
        //    countOfSuccess: 4036
        //    countOfFailures: 2399
        TestResult testResultForGeoTools = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationAdapterGeoTools(), list);
        handleTestResults(
            testResultForGeoTools,
            DELTA_LIMIT_FOR_SUCCESS,
            createNewRegressionFile,
            "_version_25.1"  // build.gradle: implementation("org.geotools:gt-main:25.1")
            // file created: "test/resources/regression_results/CrsTransformationAdapterGeoTools_version_25.1.csv
        );
        // There are differences in the GeoTools generated files (e.g. when using version 21.0 instead of 19.1)
        // but when roughly looking at the files with WinMerge the differences seem to be very small.
        // However: TODO: use code to detect significant differences, and if those exist,
        // then try to figure out if it is improvement or the opposite.
        // If the later version seem to have introduced a bug/error then try to report it to the GeoTools project

        // TODO: compute standard deviations for the results e.g.
        // the deviations from the original coordinate when transforming back and forth,
        // and also compare them with each other and caluclate the standard deviation
        // from the median value ...
    }

    @Test
    @Tag(TestCategory.SlowTest) // e.g. 122 seconds for this test method while all other (approx 80) tests (except those in this test class) take about one minute
    @Tag(TestCategory.SideEffectFileCreation)
    void testAllTransformationsInGeneratedCsvFileWithGeoPackage() {
        //    seconds: 122
        //    countOfSuccess: 3918
        //    countOfFailures: 2517
        TestResult testResultForGeoPackage = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationAdapterGeoPackageNGA(), list);
        handleTestResults(
            testResultForGeoPackage,
            DELTA_LIMIT_FOR_SUCCESS,
            createNewRegressionFile,
            "_version_5.0.0" // build.gradle: 'mil.nga.geopackage:geopackage:5.0.0'
            // file created: "test/resources/regression_results/CrsTransformationAdapterGeoPackageNGA_version_5.0.0.csv
        );
        // The above created latest output file "CrsTransformationAdapterGeoPackageNGA_version_5.0.0.csv"
        // was identical with the file created for the previous version (generated by GeoPackage 3.5.0)
    }

    @Test
    @Tag(TestCategory.SlowTest) // e.g. 201 seconds for this test method while all other (approx 80) tests (except those in this test class) take about one minute
    @Tag(TestCategory.SideEffectFileCreation)
    void testAllTransformationsInGeneratedCsvFileWithProj4J() {
        //    seconds: 201
        //    countOfSuccess: 3916
        //    countOfFailures: 2519
        TestResult testResultForProj4J = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationAdapterProj4J(), list);
        handleTestResults(
            testResultForProj4J,
            DELTA_LIMIT_FOR_SUCCESS,
            createNewRegressionFile,
            "_version_0.1.0" // build.gradle: implementation("org.osgeo:proj4j:0.1.0")
        );
    }

    @Test
    @Tag(TestCategory.SlowTest) // e.g. 245 seconds for this test method
    @Tag(TestCategory.SideEffectFileCreation)
    void testAllTransformationsInGeneratedCsvFileWithProj4jLocationtech() {
        // seconds: 245
        // countOfSuccess: 5320
        // countOfFailures: 1115
        TestResult testResultForProj4jLocationtech = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationAdapterProj4jLocationtech(), list);
        handleTestResults(
            testResultForProj4jLocationtech,
            DELTA_LIMIT_FOR_SUCCESS,
            createNewRegressionFile,
            "_version_1.1.3" // build.gradle: implementation("org.locationtech.proj4j:proj4j:1.1.3")
        );
    }    

    @Test
    @Tag(TestCategory.SlowTest) // e.g. 384 seconds for this test method while all other (approx 80) tests (except those in this test class) take about one minute
    @Tag(TestCategory.SideEffectFileCreation)
    void testAllTransformationsInGeneratedCsvFileWithOrbisgis() {
        //    seconds: 384
        //    countOfSuccess: 3799
        //    countOfFailures: 2636
        TestResult testResultForOrbisgis = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationAdapterOrbisgisCTS(), list);
        handleTestResults(
            testResultForOrbisgis,
            DELTA_LIMIT_FOR_SUCCESS,
            createNewRegressionFile,
            "_version_1.5.2" // build.gradle: implementation("org.orbisgis:cts:1.5.2")
        );
    }

    // Above: "tests" labeled with 'TestCategory.SideEffectFileCreation'
    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------
    // Below: "tests" comparing results with different versions of the same implementation

    private final static double deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForSameImplementation = 0.000000000001;

    @Test // currently not a real test with assertions but printing console output with differences
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void compareResultsForDifferentVersionsOfGeoTools() {
        // filename e.g. "CrsTransformationAdapterGeoTools_version_21.2.csv"
        compareTheTwoLatestVersion(
                "GeoTools",
                deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForSameImplementation,
                true // shouldAlsoDisplayDifferencesWhenValueIsMissing
        );
    }

    @Test // currently not a real test with assertions but printing console output with differences
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void compareResultsForDifferentVersionsOfNGA() {
        // filename e.g. "CrsTransformationAdapterGeoPackageNGA_version_3.3.0.csv"
        compareTheTwoLatestVersion(
                "NGA",
                deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForSameImplementation,
                true // shouldAlsoDisplayDifferencesWhenValueIsMissing
        );
    }
    
    @Test // currently not a real test with assertions but printing console output with differences
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void compareResultsForDifferentVersionsOfGoober() {
        // filename e.g. "CrsTransformationAdapterGooberCTL_version_1.1.csv"
        compareTheTwoLatestVersion(
                "Goober",
                deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForSameImplementation,
                true // shouldAlsoDisplayDifferencesWhenValueIsMissing
        );
    }

    @Test // currently not a real test with assertions but printing console output with differences
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void compareResultsForDifferentVersionsOfOrbis() {
        // filename e.g. "CrsTransformationAdapterOrbisgisCTS_version_1.5.2.csv"
        compareTheTwoLatestVersion(
                "Orbis",
                deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForSameImplementation,
                true // shouldAlsoDisplayDifferencesWhenValueIsMissing
        );
    }

    @Test // currently not a real test with assertions but printing console output with differences
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void compareResultsForDifferentVersionsOfProj4J() {
        // filename e.g. "CrsTransformationAdapterProj4J_version_0.1.0.csv"
        compareTheTwoLatestVersion(
                "Proj4J",
                deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForSameImplementation,
                true // shouldAlsoDisplayDifferencesWhenValueIsMissing
        );
    }

    // Above: "tests" comparing results with different versions of the same implementation
    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------

    // Below: "tests" comparing results with different implementations

    private final static double deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForDIFFERENTImplementation = 0.00001;

    // -------------------------------------------------------------------------------------
    // Comparing the latest results of GeoTools with the results from the other files
    // (since GeoTools seem to support the greatest number of EPSG codes, based on the file sizes for the regression files)
    @Test // currently not a real test with assertions but printing console output with differences
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void compareResultsForLatestGeoToolsAndGoober() {
        // filenames e.g. "CrsTransformationAdapterGeoTools_version_21.2.csv" and "CrsTransformationAdapterGooberCTL_version_1.1.csv"
        File geoToolsFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("GeoTools")[0];
        File gooberFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("Goober")[0];
        compareWithRegressionFileContent(
                geoToolsFile,
                gooberFile,
                deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForDIFFERENTImplementation,
                false // shouldAlsoDisplayDifferencesWhenValueIsMissing
        );
    }

    @Test // currently not a real test with assertions but printing console output with differences
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void compareResultsForLatestGeoToolsAndGeoPackageNGA() {
        File geoToolsFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("GeoTools")[0];
        File geoPackageNGAFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("NGA")[0];
        compareWithRegressionFileContent(
                geoToolsFile,
                geoPackageNGAFile,
                deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForDIFFERENTImplementation,
                false // shouldAlsoDisplayDifferencesWhenValueIsMissing
        );
    }

    @Test // currently not a real test with assertions but printing console output with differences
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void compareResultsForLatestGeoToolsAndProj4J() {
        File geoToolsFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("GeoTools")[0];
        File proj4JFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("Proj4J")[0];
        compareWithRegressionFileContent(
                geoToolsFile,
                proj4JFile,
                deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForDIFFERENTImplementation,
                false // shouldAlsoDisplayDifferencesWhenValueIsMissing
        );
    }

    @Test // currently not a real test with assertions but printing console output with differences
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void compareResultsForLatestGeoToolsAndOrbisgis() {
        File geoToolsFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("GeoTools")[0];
        File orbisFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("Orbis")[0];
        compareWithRegressionFileContent(
                geoToolsFile,
                orbisFile,
                deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForDIFFERENTImplementation,
                false // shouldAlsoDisplayDifferencesWhenValueIsMissing
        );
    }

    // Above: "tests" comparing results with different implementations
    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------
    //
    // Below: comparing files for more than two implementations at a time,
    // to find the outliers that are significantly different for the others,
    // i.e. to make it easier to find implementations which are potentially very incorrect (buggy)
    // for transformatsion regarding a certain EPSG code where the significant differences were found

    @Test // currently not a real test with assertions but printing console output with differences
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void findPotentialBuggyImplementations() { // similarly named method in another class, see comment there (current class name: CoordinateTestDataGeneratedFromEpsgDatabaseTest2')
        final File geoToolsFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("GeoTools")[0];
        final File gooberFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("Goober")[0];
        final File ngaFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("NGA")[0];
        final File orbisFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("Orbis")[0];
        final File projFile = this.getFilesWithRegressionsResultsSortedWithLatesFirst("Proj")[0];
        final List<File> filesToCompare = Arrays.asList(geoToolsFile, gooberFile, ngaFile, orbisFile, projFile);
        final double deltaValueForDifferencesToIgnore = 0.001;
        compareFiles(
            filesToCompare,
            deltaValueForDifferencesToIgnore
        );
    }

    private void compareFiles(
        final List<File> filesToCompare,
        final double deltaValueForDifferencesToIgnore
    ) {
        final ResultAggregator resultAggregator = new ResultAggregator();
        final boolean shouldShowALLdifferences = deltaValueForDifferencesToIgnore < 0;
        final List<List<String>> listOfRowsPerFile = new ArrayList<List<String>>();
        int numberOfRowsInFile = -1;
        for(File file: filesToCompare) {
            final List<String> rowsInFile = getAllLinesFromTextFileUTF8(file);
            listOfRowsPerFile.add(rowsInFile);
            resultAggregator.addRowsFromFile(rowsInFile, file);
            if(numberOfRowsInFile < 0) {
                numberOfRowsInFile = rowsInFile.size();
            }
            else {
                if(rowsInFile.size() != numberOfRowsInFile) {
                    final String errorMessage = "Not even the same number of rows in the files";
                    System.out.println(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            }
        }
        final Set<Integer> indexesForRowsWithSignificantDifference = resultAggregator.getIndexesForRowsWithSignificantDifference(deltaValueForDifferencesToIgnore);
        for (Integer ind : indexesForRowsWithSignificantDifference) {
            System.out.println("-----------------");
            System.out.println("index " + ind);
            for (int i=0; i < listOfRowsPerFile.size(); i++) {
                final File file = filesToCompare.get(i);
                final String rowContent = listOfRowsPerFile.get(i).get(ind);
                System.out.println(rowContent + " ; " + file.getName());
            }
        }
        System.out.println("-------------------------------------------------");
    }
    // -------------------------------------------------------------------------------------


    private TestResult runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(
            CrsTransformationAdapter crsTransformationAdapter,
            List<EpsgCrsAndAreaCodeWithCoordinates> coordinatesFromGeneratedCsvFile
    ) {
        ArrayList<TestResultItem> testResultItems = new ArrayList<>();
        int counter = 0;

        long startTime = System.nanoTime();
        for (EpsgCrsAndAreaCodeWithCoordinates item : coordinatesFromGeneratedCsvFile) {
            final CrsCoordinate inputCoordinateWGS84 = CrsCoordinateFactory.createFromXEastingLongitudeAndYNorthingLatitude(item.centroidX, item.centroidY, EpsgNumber.WORLD__WGS_84__4326);
            final CrsTransformationResult resultOfTransformationFromWGS84 = crsTransformationAdapter.transform(inputCoordinateWGS84, item.epsgCrsCode);
            CrsTransformationResult resultOfTransformationBackToWGS84 = null;
            if (resultOfTransformationFromWGS84.isSuccess()) {
                resultOfTransformationBackToWGS84 = crsTransformationAdapter.transform(resultOfTransformationFromWGS84.getOutputCoordinate(), EpsgNumber.WORLD__WGS_84__4326);
            }
            testResultItems.add(new TestResultItem(item, inputCoordinateWGS84, resultOfTransformationFromWGS84, resultOfTransformationBackToWGS84));
            if (counter++ % 500 == 0) // just to show some progress
                System.out.println(this.getClass().getSimpleName() + " , counter: " + counter + " (of the total " + coordinatesFromGeneratedCsvFile.size() + ") for adapter " + crsTransformationAdapter.getClass().getSimpleName()); // to show some progress
            // if(counter > 300) break;
        }
        long elapsedNanos = System.nanoTime() - startTime;
        long totalNumberOfSecondsForAllTransformations = TimeUnit.NANOSECONDS.toSeconds(elapsedNanos);
        return new TestResult(crsTransformationAdapter, totalNumberOfSecondsForAllTransformations, testResultItems);
    }

    /**
     * Iterates the test results and counts the number of successes and failures
     * including considering a maximum delta difference between the coordinates
     * when having transformed back and forth.
     * While iterating, lines of strings are also created, with the resulting coordinates.
     * Depennding on a boolean parameter, those string lines are either written to
     * a file, or compared with a previous created file.
     *
     * @param testResult
     * @param deltaLimitForSuccess
     * @param createNewRegressionFile if false, then instead compare with previous regression file
     */
    private void handleTestResults(
        TestResult testResult,
        double deltaLimitForSuccess,
        boolean createNewRegressionFile,
        String fileNameSuffixExcludingExtension
    ) {
        System.out.println("-------------------------------");
        System.out.println("testResults for " + testResult.adapter.getClass().getSimpleName());
        System.out.println("seconds: " + testResult.totalNumberOfSecondsForAllTransformations);
        List<TestResultItem> testResultItems = testResult.testResultItems;
        int countOfFailures = 0;
        int countOfSuccess = 0;
        boolean isSuccess;
        ArrayList<String> linesWithCurrentResults = new ArrayList<>();
        for (TestResultItem testResultItem : testResultItems) {
            String s = testResultItem.getResultStringForRegressionFile();
            linesWithCurrentResults.add(s);
            isSuccess = testResultItem.isSuccessfulTransformationFromWGS84();
            if (isSuccess) {
                isSuccess  = testResultItem.isSuccessfulTransformationBackToWGS84();
                if (isSuccess) {
                    CrsCoordinate inputCoordinateWGS84 = testResultItem.getInputCoordinateWGS84();
                    //Coordinate wgs84Again = resultOfTransformationBackToWGS84.getOutputCoordinate();
                    CrsCoordinate wgs84Again = testResultItem.getCoordinateOutputTransformationBackToWGS84();
                    final double deltaLong = Math.abs(inputCoordinateWGS84.getXEastingLongitude() - wgs84Again.getXEastingLongitude());
                    final double deltaLat = Math.abs(inputCoordinateWGS84.getYNorthingLatitude() - wgs84Again.getYNorthingLatitude());
                    isSuccess = deltaLong < deltaLimitForSuccess && deltaLat < deltaLimitForSuccess;
                }
            }
            if (isSuccess) {
                countOfSuccess++;
            } else {
                countOfFailures++;
            }
        }
        System.out.println("countOfSuccess: " + countOfSuccess);
        System.out.println("countOfFailures: " + countOfFailures);
        System.out.println("-------------------------------");

        final File file = getFileForRegressionResults(testResult.adapter, fileNameSuffixExcludingExtension);
        if (createNewRegressionFile) {
            createNewRegressionFile(file, linesWithCurrentResults);
        }
    }

    private File getFileForRegressionResults(CrsTransformationAdapter adapter, String fileNameSuffixExcludingExtension) {
        File directoryForRegressionsResults = getDirectoryForRegressionsResults();
        File file = new File(directoryForRegressionsResults, adapter.getClass().getSimpleName() + fileNameSuffixExcludingExtension + ".csv");
        return file;
    }

    /**
     * @param pathRelativeFromProjectDirectory
     *  the path relative to the "crs-transformation-adapter-test" e.g. a path such as "src/test/resources"
     * @return the directory
     */
    private File getDirectory(String pathRelativeFromProjectDirectory) {
        // https://docs.oracle.com/javase/7/docs/api/java/io/File.html
        // "... system property user.dir, and is typically the directory in which the Java virtual machine was invoked"
        File userDir = new File(System.getProperty("user.dir"));
        File directory = new File(userDir, pathRelativeFromProjectDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("Directory does not exist: " + directory.getAbsolutePath());
        }
        return directory;        
    }
    private File getDirectoryForRegressionsResults() {
        return getDirectory(OUTPUT_DIRECTORY_FOR_REGRESSION_RESULTS);
    }
    private File getDirectoryForGeneratedFiles() {
        return getDirectory(OUTPUT_DIRECTORY_FOR_GENERATED_FILES);
    }

    private void createNewRegressionFile(File file, ArrayList<String> linesWithCurrentResults) {
        try {
            Path write = Files.write(file.toPath(), linesWithCurrentResults, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Tag(TestCategory.SideEffectPrintingConsoleOutput)
    void showDifferenceIfSignificantTest() {
        DifferenceWhenComparingCoordinateValues differenceWhenComparingCoordinateValues = showDifferenceIfSignificant(
            "35.00827072383671|31.517029225386523|2039|200816.30213267874|602774.2381723676|35.00827072137521|31.517029283149466",
            "35.00827072383671|31.517029225386523|2039|200816.30213267755|602774.2381723677|35.00827072137521|31.517029283149473",
            deltaValueForDifferencesToIgnoreWhenComparingDifferentVersionForSameImplementation,
            true // shouldAlsoDisplayDifferencesWhenValueIsMissing
        );
        if(differenceWhenComparingCoordinateValues != DifferenceWhenComparingCoordinateValues.SIGNIFICANT_VALUE_DIFFERENCE) {
//            System.out.println("no significant differenceWhenComparingCoordinateValues");
        }
        assertNotEquals(differenceWhenComparingCoordinateValues, DifferenceWhenComparingCoordinateValues.SIGNIFICANT_VALUE_DIFFERENCE);
    }

    private void compareWithRegressionFileContent(
        File fileWithLatestResults,
        File fileWithSecondLatestResults,
        double deltaValueForDifferencesToIgnore, // if negative value then show ANY difference
        boolean shouldAlsoDisplayDifferencesWhenValueIsMissing
    ) {
        boolean shouldShowALLdifferences = deltaValueForDifferencesToIgnore < 0;
        System.out.println("-------------------------------------------------");
        System.out.println("Will now compare the files " + fileWithLatestResults.getName() + " and " + fileWithSecondLatestResults.getName());
        final List<String> linesWithLatestResults = getAllLinesFromTextFileUTF8(fileWithLatestResults);
        final List<String> linesWithSecondLatestResults = getAllLinesFromTextFileUTF8(fileWithSecondLatestResults);

        // assertEquals(linesWithLatestResults.size(), linesWithSecondLatestResults.size(), "Not even the same number of results as previously");
        if(linesWithLatestResults.size() != linesWithSecondLatestResults.size()) {
            System.out.println("Not even the same number of results as previously: " + linesWithLatestResults.size() + " vs " + linesWithSecondLatestResults.size());
        }
        for (int i = 0; i < linesWithLatestResults.size(); i++) {
            //assertEquals(linesWithLatestResults.get(i), linesWithSecondLatestResults.get(i));
            if(!linesWithLatestResults.get(i).equals(linesWithSecondLatestResults.get(i))) {
                if(shouldShowALLdifferences) {
                    System.out.println("Diff lines:");
                    System.out.println(linesWithLatestResults.get(i));
                    System.out.println(linesWithSecondLatestResults.get(i));
                }
                else {
                    showDifferenceIfSignificant(
                        linesWithLatestResults.get(i),
                        linesWithSecondLatestResults.get(i),
                        deltaValueForDifferencesToIgnore,
                        shouldAlsoDisplayDifferencesWhenValueIsMissing
                    );
                }
            }
        }
        System.out.println("-------------------------------------------------");
    }

    private DifferenceWhenComparingCoordinateValues showDifferenceIfSignificant(
        String lineFromFileWithRegressionResults,
        String lineFromFileWithRegressionResults2,
        double deltaValueForDifferencesToIgnore,
        boolean shouldAlsoDisplayDifferencesWhenValueIsMissing
    ) {
        TestResultItem t1 = new TestResultItem(lineFromFileWithRegressionResults);
        TestResultItem t2 = new TestResultItem(lineFromFileWithRegressionResults2);
        DifferenceWhenComparingCoordinateValues diff = t1.isDeltaDifferenceSignificant(t2, deltaValueForDifferencesToIgnore);
        if(
            (shouldAlsoDisplayDifferencesWhenValueIsMissing &&
                (
                    diff == DifferenceWhenComparingCoordinateValues.SIGNIFICANT_VALUE_DIFFERENCE
                    ||
                    diff == DifferenceWhenComparingCoordinateValues.EXISTING_VS_NOT_EXISTING
                )
            )
            ||
            ( !shouldAlsoDisplayDifferencesWhenValueIsMissing &&
                (
                    diff == DifferenceWhenComparingCoordinateValues.SIGNIFICANT_VALUE_DIFFERENCE
                )
            )
        ) {
            System.out.println("Diff lines with significant delta " + deltaValueForDifferencesToIgnore + " : ");
            System.out.println(lineFromFileWithRegressionResults);
            System.out.println(lineFromFileWithRegressionResults2);
        }
        return diff;
    }

    private List<String> getAllLinesFromTextFileUTF8(File file) {
        try {
            return Resources.readLines(file.toURI().toURL(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The file with coordinates are generated from another module "crsCodeGeneration".
     *  (the class 'CoordinateTestDataGenerator' which are creating the data from a MS Access file
     *  and from shapefile with polygon used for creating the coordinates as centroid points
     *  within a certain area where the EPSG code is defined to be used)
     */
    public static List<EpsgCrsAndAreaCodeWithCoordinates> getCoordinatesFromGeneratedCsvFile() {
        final ArrayList<EpsgCrsAndAreaCodeWithCoordinates> list = new ArrayList<>();
        try {
            final URL url = Resources.getResource(INPUT_TEST_DATA_FILE);
            final List<String> lines = Resources.readLines(url, Charset.forName("UTF-8"));
            for (String line : lines) {
                final EpsgCrsAndAreaCodeWithCoordinates epsgCrsAndAreaCodeWithCoordinates = createEpsgCrsAndAreaCodeWithCoordinatesFromLineInCsvFile(line);
                list.add(epsgCrsAndAreaCodeWithCoordinates);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private static EpsgCrsAndAreaCodeWithCoordinates createEpsgCrsAndAreaCodeWithCoordinatesFromLineInCsvFile(String line) {
        final String trimmedLine = line.trim();
        // e.g. "3006|1225|Sweden|17.083659606206545|61.98770256318016"
        final String[] parts = trimmedLine.split("\\|");
        return new EpsgCrsAndAreaCodeWithCoordinates(
            Integer.parseInt(parts[0]),     // epsgCrsCode
            Integer.parseInt(parts[1]),     // epsgAreaCode
            parts[2],                       // epsgAreaName
            Double.parseDouble(parts[3]),   // centroidX
            Double.parseDouble(parts[4])    // centroidY
        );
    }


    private File[] getFilesWithRegressionsResultsSortedWithLatesFirst(
        String partOfTheFileName
    ) {
        File directoryForRegressionsResults = getDirectoryForRegressionsResults();
        File[] files = directoryForRegressionsResults.listFiles(file -> file.getName().contains(partOfTheFileName));
        sortFilesWithLatestFirst(files);
        for (File f : files) {
            System.out.println(f.getName());
        }
        return files;
    }

    /**
     * @param partOfTheFileName e.g. "GeoTools" to match file names such as "CrsTransformationAdapterGeoTools_version_19.1"
     */
    private void compareTheTwoLatestVersion(
        String partOfTheFileName,
        double deltaValueForDifferencesToIgnore,
        boolean shouldAlsoDisplayDifferencesWhenValueIsMissing
    ) {
        File[] files = getFilesWithRegressionsResultsSortedWithLatesFirst(partOfTheFileName);
        if(files.length < 2) {
            System.out.println("There are not two files containing the filename part " + partOfTheFileName + " in the directory " + getDirectoryForRegressionsResults().getAbsolutePath());
            return;
        }
        //compareWithRegressionFileContent(files[0], files[1], 0.0000000000000001);
        compareWithRegressionFileContent(files[0], files[1], deltaValueForDifferencesToIgnore, shouldAlsoDisplayDifferencesWhenValueIsMissing);
    }

    private void sortFilesWithLatestFirst(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long diff = o2.lastModified() - o1.lastModified();
                if(diff > 0) return 1;
                if(diff < 0) return -1;
                return 0;
            }
        });
    }

    // Filtering the input file "generated/CoordinateTestDataGeneratedFromEpsgDatabase.csv"
    // (which has been generated by the Kotlin code in the following file:
    //  crs-transformation-code-generation/src/main/kotlin/com/programmerare/crsCodeGeneration/coordinateTestDataGenerator/CoordinateTestDataGenerator.kt)
    // The file is filtered for swedish local coordinate reference systems with EPSG codes 3007-3024 (SWEREF99 and RT90) 
    // and then a coordinate (WGS84 longitude/latitude) which is within the region for that CRS 
    // is used for transforming to that CRS, and also for transforming to the swedish national CRS SWEREF99TM.
    // The purpose of this file is to use it in another project for verifying correctly transformed coordinates.
    // Since this file below is generated with median values, using many different implementations,
    // the resulting file are likely to contain quite accurate result.
    // (so if that other project, where this file is intended to be used, also can create very similar result,
    // then that transformation implementation can be considered as creating correct results)
    // Example output, i.e. some of the lines for the file generated by this method:
    //      4326|12.146151472138385|58.46573396912418|3006|333538.2957000149|6484098.2550872|3007|158529.85136620898|6483166.205771873|6|6
    //      4326|13.470524334397624|58.58400993288184|3006|411075.37423013494|6494745.783843142|3008|148285.48862749408|6496331.697852695|6|6
    //      4326|14.977576047737374|58.78282472189397|3006|498703.5890065983|6515870.052578431|3009|148703.07023469123|6518477.443555852|6|6
    //@Test // Disabled with comments "//" since this is not a real "test" method but it was just used once as a starting point for the execution when generating the file  
    void createFileWithTransformationResultsForCoordinatesInSweden()
        throws IOException
    {
        final List<EpsgCrsAndAreaCodeWithCoordinates> list = getCoordinatesFromGeneratedCsvFile();
        final List<EpsgCrsAndAreaCodeWithCoordinates> filteredList = list.stream().filter(
            e -> 3007 <= e.epsgCrsCode && e.epsgCrsCode <= 3024
        ).collect(Collectors.toList());

        final String separator = "|";
        final String headerRow =
                  "EPSG 4326 (WGS84)" // col1 
                + separator + "Longitude for WGS84 (EPSG 4326)"// col2 
                + separator + "Latitude for WGS84 (EPSG 4326)" // col3
                + separator + "EPSG 3006"      // col4 
                + separator + "X for EPSG 3006"// col5
                + separator + "Y for EPSG 3006"// col6
                + separator + "EPSG 3007-3024" // col7
                + separator + "X for EPSG 3007-3024" // col8
                + separator + "Y for EPSG 3007-3024" // col9
                // The X and Y values generated (below) to the file for the 3006 and 3007-3024
                // transformations are median values for many implementations that support these EPSG codes,
                // and the last two columns shows how many implementations were used for those median values.
                + separator + "Implementation count for EPSG 3006 transformation" // col10
                + separator + "Implementation count for EPSG 3007-3024 transformation";// col11
        // Each row will have coordinates for EPSG 4326 and EPSG 3006 
        final String col1 = EpsgNumber.WORLD__WGS_84__4326 + "";
        final String col4 = EpsgNumber.SWEDEN__SWEREF99_TM__3006 + "";
        final CrsIdentifier crsIdentifierSweref99TM = CrsIdentifierFactory.createFromEpsgNumber(EpsgNumber.SWEDEN__SWEREF99_TM__3006);
        final CrsTransformationAdapter crsTransformationAdapter = CrsTransformationAdapterCompositeFactory.createCrsTransformationMedian();
        final List<String> linesFourOutputFile = filteredList.stream().map( e -> {
            final CrsCoordinate coordinateWGS84         = CrsCoordinateFactory.createFromLongitudeLatitude(e.centroidX, e.centroidY); // WGS84 is the default CRS
            final CrsTransformationResult resultSweref99tm = crsTransformationAdapter.transform(coordinateWGS84, crsIdentifierSweref99TM);
            final CrsTransformationResult resultSwedishCrs = crsTransformationAdapter.transform(coordinateWGS84, e.epsgCrsCode);
            final CrsCoordinate coordinateSweref99tm = resultSweref99tm.getOutputCoordinate();
            final CrsCoordinate coordinateSwedishCrs = resultSwedishCrs.getOutputCoordinate();

            // WGS84 (EPSG 4326) coordinates in col 2 and col 3:
            final String col2 = e.centroidX + "";
            final String col3 = e.centroidY + "";

            // SWEREF99_TM (EPSG 3006) coordinates in col 5 and col 6: 
            final String col5 = coordinateSweref99tm.getXEastingLongitude() + "";
            final String col6 = coordinateSweref99tm.getYNorthingLatitude() + "";

            // Some local swedish CRS (SWEREF or RT90, with EPSG code 3007-3024)
            final String col7 = e.epsgCrsCode + "";
            final String col8 = coordinateSwedishCrs.getXEastingLongitude() + "";
            final String col9 = coordinateSwedishCrs.getYNorthingLatitude() + "";

            // The number of implementations used to generate median values above in column 5/6 and column 8/9: 
            final String col10 = resultSweref99tm.getCrsTransformationResultStatistic().getNumberOfResults() + "";
            final String col11 = resultSwedishCrs.getCrsTransformationResultStatistic().getNumberOfResults() + "";
            
            final String row =
                      col1 + separator + col2 + separator + col3
                    + separator + col4 + separator + col5 + separator + col6
                    + separator + col7 + separator + col8 + separator + col9
                    + separator + col10 + separator + col11;
            return row;
        }).collect(Collectors.toList());        
        final File directoryForOutputFile = getDirectoryForGeneratedFiles();
        final File outputFile_swedish_crs_coordinates = new File(directoryForOutputFile, "swedish_crs_coordinates.csv");
        linesFourOutputFile.add(0, headerRow);
        Files.write(outputFile_swedish_crs_coordinates.toPath(), linesFourOutputFile, Charset.forName("UTF-8"));
    }    
}
//// The test results below were created when the following delta value was used:
//double DELTA_LIMIT_FOR_SUCCESS = 0.0001;
//-------------------------------
//testResults for CrsTransformationAdapterGooberCTL
//seconds: 2
//countOfSuccess: 19
//countOfFailures: 6416
//-------------------------------
//testResults for CrsTransformationAdapterProj4J
//seconds: 222
//countOfSuccess: 3916
//countOfFailures: 2519
//-------------------------------
//testResults for CrsTransformationAdapterOrbisgisCTS
//seconds: 455
//countOfSuccess: 3799
//countOfFailures: 2636
//-------------------------------
//testResults for CrsTransformationAdapterGeoTools
//seconds: 210
//countOfSuccess: 4036
//countOfFailures: 2399
//-------------------------------
//testResults for CrsTransformationAdapterGeoPackageNGA
//seconds: 249
//countOfSuccess: 3918
//countOfFailures: 2517
//-------------------------------
