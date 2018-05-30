package com.programmerare.com.programmerare.testData;

import static org.junit.jupiter.api.Assertions.*;
import com.programmerare.crsConstants.constantsByAreaNameNumber.v9_3.EpsgCode;
import com.programmerare.crsTransformationFacadeGeoPackageNGA.CrsTransformationFacadeGeoPackageNGA;
import com.programmerare.crsTransformationFacadeGeoTools.CrsTransformationFacadeGeoTools;
import com.programmerare.crsTransformationFacadeGooberCTL.CrsTransformationFacadeGooberCTL;
import com.programmerare.crsTransformationFacadeOrbisgisCTS.CrsTransformationFacadeOrbisgisCTS;
import com.programmerare.crsTransformationFacadeProj4J.CrsTransformationFacadeProj4J;
import com.programmerare.crsTransformations.Coordinate;
import com.programmerare.crsTransformations.CrsTransformationFacade;
import com.programmerare.crsTransformations.TransformResult;
import org.junit.jupiter.api.Test;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class CoordinateTestDataGeneratedFromEpsgDatabaseTest {

    //private final static double deltaLimit = 0.000001;
    private final static double DELTA_LIMIT_FOR_SUCCESS = 0.0001;

    @Test
    void testAllTransformationsInGeneratedCsvFileWithDifferentImplementations() {
        List<EpsgCrsAndAreaCodeWithCoordinates> list = getCoordinatesFromGeneratedCsvFile();

        boolean createNewRegressionFile = true;
        //boolean createNewRegressionFile = false;

        double deltaLimitForSuccess = DELTA_LIMIT_FOR_SUCCESS;


        TestResult testResultForGeoPackage = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationFacadeGeoPackageNGA(), list);
        handleTestResults(testResultForGeoPackage, deltaLimitForSuccess, createNewRegressionFile);
        if(true) return;

        TestResult testResultForGoober = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationFacadeGooberCTL(), list);
        handleTestResults(testResultForGoober, deltaLimitForSuccess, createNewRegressionFile);

        TestResult testResultForProj4J = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationFacadeProj4J(), list);
        handleTestResults(testResultForProj4J, deltaLimitForSuccess, createNewRegressionFile);

        TestResult testResultForOrbisgis = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationFacadeOrbisgisCTS(), list);
        handleTestResults(testResultForOrbisgis, deltaLimitForSuccess, createNewRegressionFile);

        TestResult testResultForGeoTools = runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(new CrsTransformationFacadeGeoTools(), list);
        handleTestResults(testResultForGeoTools, deltaLimitForSuccess, createNewRegressionFile);

        // TODO: compute standard deviations for the results e.g.
        // the deviations from the original coordinate when transforming back and forth,
        // and also compare them with each other and caluclate the standard deviation
        // from the median value ...
    }

    private TestResult runAllTransformationsOfTheCoordinatesInTheGeneratedCsvFile(
        CrsTransformationFacade crsTransformationFacade,
        List<EpsgCrsAndAreaCodeWithCoordinates> coordinatesFromGeneratedCsvFile
    ) {
        ArrayList<TestResultItem> testResultItems = new ArrayList<>();
        int counter = 0;

        long startTime = System.nanoTime();
        for (EpsgCrsAndAreaCodeWithCoordinates item : coordinatesFromGeneratedCsvFile) {
            final Coordinate inputCoordinateWGS84 = Coordinate.createFromXLongYLat(item.centroidX, item.centroidY, EpsgCode.WORLD__WGS_84__4326);
            final TransformResult resultOfTransformationFromWGS84 = crsTransformationFacade.transformToResultObject(inputCoordinateWGS84, item.epsgCrsCode);
            TransformResult resultOfTransformationBackToWGS84 = null;
            if (resultOfTransformationFromWGS84.isSuccess()) {
                resultOfTransformationBackToWGS84 = crsTransformationFacade.transformToResultObject(resultOfTransformationFromWGS84.getOutputCoordinate(), EpsgCode.WORLD__WGS_84__4326);
            }
            testResultItems.add(new TestResultItem(item, inputCoordinateWGS84, resultOfTransformationFromWGS84, resultOfTransformationBackToWGS84));
            if (counter++ % 1000 == 0) // just to show some progress
                System.out.println("counter: " + counter + " (of the total " + coordinatesFromGeneratedCsvFile.size() + ") for facade " + crsTransformationFacade.getClass().getSimpleName()); // to show some progress
            // if(counter > 300) break;
        }
        long elapsedNanos = System.nanoTime() - startTime;
        long totalNumberOfSecondsForAllTransformations = TimeUnit.NANOSECONDS.toSeconds(elapsedNanos);
        return new TestResult(crsTransformationFacade, totalNumberOfSecondsForAllTransformations, testResultItems);
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
    private void handleTestResults(TestResult testResult, double deltaLimitForSuccess, boolean createNewRegressionFile) {
        System.out.println("-------------------------------");
        System.out.println("testResults for " + testResult.facade.getClass().getSimpleName());
        System.out.println("seconds: " + testResult.totalNumberOfSecondsForAllTransformations);
        List<TestResultItem> testResultItems = testResult.testResultItems;
        int countOfFailures = 0;
        int countOfSuccess = 0;
        boolean isSuccess;
        ArrayList<String> linesWithCurrentResults = new ArrayList<>();
        for (TestResultItem testResultItem : testResultItems) {
            String s = testResultItem.getResultStringForRegressionFile();
            linesWithCurrentResults.add(s);
            isSuccess = testResultItem.resultOfTransformationFromWGS84.isSuccess();
            if (isSuccess) {
                TransformResult resultOfTransformationBackToWGS84 = testResultItem.resultOfTransformationBackToWGS84;
                isSuccess = resultOfTransformationBackToWGS84 != null && resultOfTransformationBackToWGS84.isSuccess();
                if (isSuccess) {
                    Coordinate inputCoordinateWGS84 = testResultItem.inputCoordinateWGS84;
                    Coordinate wgs84Again = resultOfTransformationBackToWGS84.getOutputCoordinate();
                    final double deltaLong = Math.abs(inputCoordinateWGS84.getXLongitude() - wgs84Again.getXLongitude());
                    final double deltaLat = Math.abs(inputCoordinateWGS84.getYLatitude() - wgs84Again.getYLatitude());
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

        final File file = getFileForRegressionResults(testResult.facade);
        if (createNewRegressionFile) {
            createNewRegressionFile(file, linesWithCurrentResults);
        } else {
            compareWithRegressionFileContent(file, linesWithCurrentResults);
        }
    }

    private File getFileForRegressionResults(CrsTransformationFacade facade) {
        File directoryForRegressionsResults = getDirectoryForRegressionsResults();
        File file = new File(directoryForRegressionsResults, facade.getClass().getSimpleName() + ".csv");
        return file;
    }

    private File getDirectoryForRegressionsResults() {
        // https://docs.oracle.com/javase/7/docs/api/java/io/File.html
        // "... system property user.dir, and is typically the directory in which the Java virtual machine was invoked"
        File userDir = new File(System.getProperty("user.dir"));
        File directoryForRegressionsResults = new File(userDir, "src/test/resources/regression_results");
        if (!directoryForRegressionsResults.exists() || !directoryForRegressionsResults.isDirectory()) {
            throw new RuntimeException("Directory does not exist: " + directoryForRegressionsResults.getAbsolutePath());
        }
        return directoryForRegressionsResults;
    }

    private void createNewRegressionFile(File file, ArrayList<String> linesWithCurrentResults) {
        try {
            Path write = Files.write(file.toPath(), linesWithCurrentResults, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compareWithRegressionFileContent(File file, ArrayList<String> linesWithCurrentResults) {
        final List<String> linesWithPreviousResults;
        try {
            linesWithPreviousResults = Resources.readLines(file.toURI().toURL(), Charset.forName("UTF-8"));
            assertEquals(linesWithPreviousResults.size(), linesWithCurrentResults.size(), "Not even the same number of results as previously");
            for (int i = 0; i < linesWithPreviousResults.size(); i++) {
                assertEquals(linesWithPreviousResults.get(i), linesWithCurrentResults.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The file with coordinates are generated from another module "crsCodeGeneration".
     *  (the class 'CoordinateTestDataGenerator' which are creating the data from a MS Access file
     *  and from shapefile with polygon used for creating the coordinates as centroid points
     *  within a certain area where the EPSG code is defined to be used)
     */
    private List<EpsgCrsAndAreaCodeWithCoordinates> getCoordinatesFromGeneratedCsvFile() {
        final ArrayList<EpsgCrsAndAreaCodeWithCoordinates> list = new ArrayList<>();
        try {
            final String pathToTestDataFile = "generated/CoordinateTestDataGeneratedFromEpsgDatabase.csv";
            final URL url = Resources.getResource(pathToTestDataFile);
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

    // @Test
    void temporaryTestAsStartingPointOfExecutionWhenTroubleShooting() {
        testOneRowFromCsvFile(
            new CrsTransformationFacadeGeoPackageNGA(),
            "3006|1225|Sweden|17.083659606206545|61.98770256318016"
        );
    }

    /**
     *
     * @param crsTransformationFacade
     * @param oneRowFromCsvFile e.g. "3006|1225|Sweden|17.083659606206545|61.98770256318016"
     *  (can be copied from the file "generated/CoordinateTestDataGeneratedFromEpsgDatabase.csv" )
     */
    private void testOneRowFromCsvFile(CrsTransformationFacade crsTransformationFacade, String oneRowFromCsvFile) {
        EpsgCrsAndAreaCodeWithCoordinates item = createEpsgCrsAndAreaCodeWithCoordinatesFromLineInCsvFile("3006|1225|Sweden|17.083659606206545|61.98770256318016");
        final Coordinate inputCoordinateWGS84 = Coordinate.createFromXLongYLat(item.centroidX, item.centroidY, EpsgCode.WORLD__WGS_84__4326);
        final TransformResult resultOfTransformationFromWGS84 = crsTransformationFacade.transformToResultObject(inputCoordinateWGS84, item.epsgCrsCode);
        if(!resultOfTransformationFromWGS84.isSuccess()) {
            System.out.println(resultOfTransformationFromWGS84.getException());
        }
        assertTrue(resultOfTransformationFromWGS84.isSuccess());
    }
    private EpsgCrsAndAreaCodeWithCoordinates createEpsgCrsAndAreaCodeWithCoordinatesFromLineInCsvFile(String line) {
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

    class TestResult {
        private final CrsTransformationFacade facade;
        private final long totalNumberOfSecondsForAllTransformations;
        private final List<TestResultItem> testResultItems;

        TestResult(
            CrsTransformationFacade facade,
            long totalNumberOfSecondsForAllTransformations,
            List<TestResultItem> testResultItems
        ) {
            this.facade = facade;
            this.totalNumberOfSecondsForAllTransformations = totalNumberOfSecondsForAllTransformations;
            this.testResultItems = testResultItems;
        }
    }

    class TestResultItem {
        private final EpsgCrsAndAreaCodeWithCoordinates item;
        private final Coordinate inputCoordinateWGS84;
        private final TransformResult resultOfTransformationFromWGS84;
        private final TransformResult resultOfTransformationBackToWGS84;

        TestResultItem(EpsgCrsAndAreaCodeWithCoordinates item, Coordinate inputCoordinateWGS84, TransformResult resultOfTransformationFromWGS84, TransformResult resultOfTransformationBackToWGS84) {
            this.item = item;
            this.inputCoordinateWGS84 = inputCoordinateWGS84;
            this.resultOfTransformationFromWGS84 = resultOfTransformationFromWGS84;
            this.resultOfTransformationBackToWGS84 = resultOfTransformationBackToWGS84;
        }

        private final static String SEPARATOR = "|";

        public String getResultStringForRegressionFile() {
            String res1 = SEPARATOR + SEPARATOR; // two separators arae as many as will be used below if successful content will be used between the separators
            String res2 = res1;
            if (resultOfTransformationFromWGS84 != null && resultOfTransformationFromWGS84.isSuccess()) {
                final Coordinate outputCoordinate = resultOfTransformationFromWGS84.getOutputCoordinate();
                res1 = SEPARATOR + outputCoordinate.getXLongitude() + SEPARATOR + outputCoordinate.getYLatitude();
            }
            if (resultOfTransformationBackToWGS84 != null && resultOfTransformationBackToWGS84.isSuccess()) {
                final Coordinate outputCoordinate = resultOfTransformationBackToWGS84.getOutputCoordinate();
                res2 = SEPARATOR + outputCoordinate.getXLongitude() + SEPARATOR + outputCoordinate.getYLatitude();
            }
            return item.centroidX + SEPARATOR + item.centroidY + SEPARATOR + item.epsgCrsCode + res1 + res2;
        }
    }

    class EpsgCrsAndAreaCodeWithCoordinates {
        final int epsgCrsCode;
        final int epsgAreaCode;
        final String epsgAreaName;
        final double centroidX;
        final double centroidY;

        private final static String SEPARATOR = "_";

        EpsgCrsAndAreaCodeWithCoordinates(
            int epsgCrsCode,
            int epsgAreaCode,
            String epsgAreaName,
            double centroidX,
            double centroidY
        ) {
            this.epsgCrsCode = epsgCrsCode;
            this.epsgAreaCode = epsgAreaCode;
            this.epsgAreaName = epsgAreaName;
            this.centroidX = centroidX;
            this.centroidY = centroidY;
        }

        @Override
        public String toString() {
            return
                "EpsgCrsAndAreaCodeWithCoordinates{" +
                "epsgAreaCode=" + epsgAreaCode +
                ", epsgCrsCode=" + epsgCrsCode +
                ", epsgAreaName='" + epsgAreaName + '\'' +
                ", centroidX=" + centroidX +
                ", centroidY=" + centroidY +
                '}';
        }
    }
}
//// The test results below were created when the following delta value was used:
//double DELTA_LIMIT_FOR_SUCCESS = 0.0001;
//-------------------------------
//testResults for CrsTransformationFacadeGooberCTL
//seconds: 2
//countOfSuccess: 19
//countOfFailures: 6416
//-------------------------------
//testResults for CrsTransformationFacadeProj4J
//seconds: 222
//countOfSuccess: 3916
//countOfFailures: 2519
//-------------------------------
//testResults for CrsTransformationFacadeOrbisgisCTS
//seconds: 455
//countOfSuccess: 3799
//countOfFailures: 2636
//-------------------------------
//testResults for CrsTransformationFacadeGeoTools
//seconds: 210
//countOfSuccess: 4036
//countOfFailures: 2399
//-------------------------------
//testResults for CrsTransformationFacadeGeoPackageNGA
//seconds: 249
//countOfSuccess: 3918
//countOfFailures: 2517
//-------------------------------
