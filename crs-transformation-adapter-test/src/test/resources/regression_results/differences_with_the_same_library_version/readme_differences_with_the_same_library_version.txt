When upgrading to Kotlin 1.5.20 (from Kotlin 1.4.31) there were some differences in the decimals for the transformations 
with the two libraries Proj4J 0.1.0 and CTS 1.5.2.

Both versions for each of these transformations have been saved manually into this dierctory to make it easy to look at the differences 
with e.g. WinMerge directly from the file system (i.e. without having to find the versions to compare in the git history log).

A suffix showing the used kotlin version has been added to these file names in this directory.

The hypothesis is that these differences has something to do with the upgrade of Kotlin version rather than the upgrade of versions for gradle/junit/mockito/guava which were done at the same time.

TODO (maybe, but it takes time to compile and run all tests...):
Check out an older commit with Kotlin 1.4.31 and re-run the tests that generate these csv files,
and then change *ONLY* the Kotlin version to Kotlin 1.5.20 and aagin re-run the tests to see that the Kotlin version really seems to be the cause of the differences.
ALSO test to do this both from the IDE (IntelliJ IDEA) and from the command prompt i.e. with "gradlew test"
