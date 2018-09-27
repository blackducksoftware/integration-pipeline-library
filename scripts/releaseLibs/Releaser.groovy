import ToolRunner

class Releaser {
    static final String OPERATION_PRINTLIBRARIES = "print-libraries"
    static final String OPERATION_CLONE = "clone"
    static final String OPERATION_UPDATEVERSIONS = "update-versions"
    static final String OPERATION_BUILD = "build"
    static final String OPERATION_COMMIT = "commit"
    static final String OPERATION_RESET = "reset"
    static final String OPERATION_DIFF = "diff"
    static final Map<String, Integer> operationArgumentCounts
    static {
        operationArgumentCounts = new HashMap<>()
        operationArgumentCounts.put(OPERATION_PRINTLIBRARIES, 0)
        operationArgumentCounts.put(OPERATION_CLONE, 1)
        operationArgumentCounts.put(OPERATION_UPDATEVERSIONS, 1)
        operationArgumentCounts.put(OPERATION_BUILD, 1)
        operationArgumentCounts.put(OPERATION_COMMIT, 1)
        operationArgumentCounts.put(OPERATION_RESET, 1)
        operationArgumentCounts.put(OPERATION_DIFF, 1)
    }
    
    static List<String> libraries = Arrays.asList("integration-common", "integration-reporting", "integration-rest", "integration-bdio",
            "phone-home-client",
            "hub-common-api", "hub-common-rest", "hub-common-reporting", "hub-common")

    static void main(String[] args) {
        if (args.size() < 1) {
            println "Error: Too few arguments"
            showUsage()
            return
        }

        String operation = args[0]
        if (!operationArgumentCounts.keySet().contains(operation)) {
            println "Error: Invalid operation"
            showUsage()
            return
        }
        
        if (args.size() < (operationArgumentCounts.get(operation)+1)) {
            println "Error: This operation requires ${operationArgumentCounts.get(operation)} arguments"
            showUsage()
            return
        } 
        
        String workspaceDirPath = null
        if (operationArgumentCounts.get(operation) > 0) {
            workspaceDirPath = args[1]
        }

        Releaser releaser = new Releaser(new ToolRunner(), operation, workspaceDirPath)
        releaser.run()
    }

    static void showUsage() {
        println "Usage: groovy Releaser <operation>"
        println "       operation:"
        println "           ${OPERATION_PRINTLIBRARIES}:\t\t\t\tPrint a list of the libraries that will be operated on"
        println "           ${OPERATION_CLONE} <workspace-dir-path>:\tClone the libraries from github to the given workspace"
        println "           ${OPERATION_UPDATEVERSIONS} <workspace-dir-path>:\tAdjust versions in libraries' build.gradle files"
        println "           ${OPERATION_BUILD} <workspace-dir-path>:\t\t\tDo a './gradlew clean build install' on each library"
        println "           ${OPERATION_COMMIT} <workspace-dir-path>:\t\t\tDo a 'git add/commit/push' on each library"
        println "           ${OPERATION_DIFF} <workspace-dir-path>:\t\t\tDo a 'git diff' on each library"
        println "           ${OPERATION_RESET} <workspace-dir-path>:\t\t\tDo a 'git reset --hard' on each library"
    }

    // Non-static
    final ToolRunner toolRunner
    File workspaceDir
    final String operation
    Map<String, String> currentLibraryVersions = new HashMap<>()
    Map<String, String> finalLibraryVersions = new HashMap<>()

    Releaser(ToolRunner toolRunner, String operation, String workspaceDirPath) {
        this.toolRunner = toolRunner
        this.operation = operation
        if (workspaceDirPath != null) {
            workspaceDir = new File(workspaceDirPath)
            workspaceDir.mkdirs()
        }
    }

    void run() {
        printf "Releaser operation: ${operation}"
        if (workspaceDir != null) {
            printf ", workspace: ${workspaceDir.getAbsolutePath()}"
        }
        println ""

        if (OPERATION_CLONE.equals(operation)) {
            cloneLibraries()
        } else if (OPERATION_PRINTLIBRARIES.equals(operation)) {
            printLibraries()
        } else if (OPERATION_UPDATEVERSIONS.equals(operation)) {
            updateVersions()
        } else if (OPERATION_BUILD.equals(operation)) {
            build()
        } else if (OPERATION_COMMIT.equals(operation)) {
            commit()
        } else if (OPERATION_DIFF.equals(operation)) {
            diff()
        } else if (OPERATION_RESET.equals(operation)) {
            reset()
        }
    }
    
    void printLibraries() {
        printLines(libraries)
    }
    
    void cloneLibraries() {

        // Clone the libraries to the given workspace dir
        for (String libraryDirName : libraries) {
            println "Cloning ${libraryDirName}"
            toolRunner.cloneLibraries(workspaceDir, "git@github.com:blackducksoftware/${libraryDirName}.git")
        }
    }

    void updateVersions() {

        // Calculate new library versions
        for (String libraryDirName : libraries) {
            File libraryDir = new File(workspaceDir, libraryDirName)

            // Get orig version
            String currentVersion = toolRunner.getProjectVersionString(libraryDir)

            // Calculate final version
            String finalVersion = getFinalVersion(currentVersion)

            // Store current and final version for this library
            // TODO: we don't actually use this current version map
            currentLibraryVersions.put(libraryDirName, currentVersion)
            finalLibraryVersions.put(libraryDirName, finalVersion)

            println "${libraryDirName}: starting version: ${currentVersion}; new version: ${finalVersion}"
        }

        // Adjust library versions and dependency versions
        for (String libraryDirName : libraries) {
            println "Setting version and library dependency versions in: ${libraryDirName}:"
            File libraryDir = new File(workspaceDir, libraryDirName)

            // Get new version
            String newVersion = finalLibraryVersions.get(libraryDirName)

            // Read build.gradle file
            String versionLine = ''
            File buildDotGradleFile = new File(libraryDir, "build.gradle")
            String fileText = buildDotGradleFile.text
            String[] buildDotGradleFileLines = fileText.split('\n')

            setNewLibraryVersionInBuildDotGradleFile(libraryDirName, buildDotGradleFileLines, newVersion)
            setNewDependencyLibraryVersionsInBuildDotGradleFile(libraryDirName, buildDotGradleFileLines)

            // Write modified file contents
            String finalFileText = buildDotGradleFileLines.join('\n')
            buildDotGradleFile.write(finalFileText)

            // Read version back from file to check
            String readBackVersion = toolRunner.getProjectVersionString(libraryDir)
            if (!readBackVersion.equals(newVersion)) {
                println "ERROR: actual new version (${readBackVersion}) does not match intended new version (${newVersion})"
            }
        }

        // Check new dependency library versions via "gradle dependencies"
        for (String libraryDirName : libraries) {
            println "Checking library dependency versions in: ${libraryDirName}:"
            File libraryDir = new File(workspaceDir, libraryDirName)
            checkNewDependencies(libraryDir)
        }

        println "Done\n\n"
        return
    }
    
    void build() {
        for (String libraryDirName : libraries) {
            File libraryDir = new File(workspaceDir, libraryDirName)
            toolRunner.build(libraryDir)
        }
    }

    void commit() {

        // Collect currentLibraryVersions
        for (String libraryDirName : libraries) {
            File libraryDir = new File(workspaceDir, libraryDirName)

            // Get current version
            String currentVersion = toolRunner.getProjectVersionString(libraryDir)
            if (currentVersion.endsWith('-SNAPSHOT')) {
                String msg = "Error: library ${} version ${} is a snapshot"
                throw new RuntimeException(msg)
            }

            // Store current version for this library
            currentLibraryVersions.put(libraryDirName, currentVersion)

            println "${libraryDirName}: will commit version: ${currentVersion}"
        }

        // Commit all changes
        for (String libraryDirName : libraries) {
            File libraryDir = new File(workspaceDir, libraryDirName)
            String currentVersion = currentLibraryVersions.get(libraryDirName)
            println "Committing ${libraryDirName} v${currentVersion}"
            List<String> commitOutput = toolRunner.commit(libraryDir, currentVersion)
            printLines(commitOutput)
            println ""
        }
    }

    void reset() {
        for (String libraryDirName : libraries) {
            File libraryDir = new File(workspaceDir, libraryDirName)
            println "Resetting ${libraryDir.getName()}"
            toolRunner.reset(libraryDir)
            println "\tReset done"
        }
    }

    void diff() {
        for (String libraryDirName : libraries) {
            File libraryDir = new File(workspaceDir, libraryDirName)
            println "Diffing ${libraryDir.getName()}"
            List<String> outputLines = toolRunner.diff(libraryDir)
            println ""
            printLines(outputLines)
            println "------"
        }
    }

    void printLines(List<String> lines) {
        for (String line : lines) {
            println line
        }
    }

    void checkNewDependencies(File libraryDir) {
        List<String> actualDependencies = toolRunner.getCompileDependencies(libraryDir)

        for (String possibleDependencyLibraryName : finalLibraryVersions.keySet()) {
            String possibleDependencyPattern = getPatternForGradleReportedDependencyLine(possibleDependencyLibraryName)
            for (String actualDependency : actualDependencies) {
                if (actualDependency.matches(possibleDependencyPattern)) {
                    int versionIndex = actualDependency.lastIndexOf(':') + 1
                    String actualVersion = actualDependency.substring(versionIndex)
                    String expectedDependencyVersion = finalLibraryVersions.get(possibleDependencyLibraryName)
                    if (!expectedDependencyVersion.equals(actualVersion)) {
                        String msg = "ERROR: For ${libraryDir.getName()} dependency on ${possibleDependencyLibraryName}: Expected version ${expectedDependencyVersion}; found version ${actualVersion}"
                        throw new RuntimeException(msg)
                    } else {
                        println "\tChecked library ${libraryDir.getName()}'s dependency on ${possibleDependencyLibraryName} and found the expected version ${actualVersion}"
                    }
                }
            }
        }
    }

    String getFinalVersion(String currentVersion) {
        int versionIncrement = 1
        String currentReleaseVersion = currentVersion
        if (currentVersion.endsWith('-SNAPSHOT')) {
            versionIncrement = 0
            int end = currentVersion.lastIndexOf('-SNAPSHOT')
            currentReleaseVersion = currentVersion.substring(0, end)
        }
        int finalVersionPieceIndex = currentReleaseVersion.lastIndexOf('.') + 1
        def finalVersionPiece = currentReleaseVersion.substring(finalVersionPieceIndex)
        String modifiedVersion = currentReleaseVersion.substring(0, finalVersionPieceIndex)
        modifiedVersion = "${modifiedVersion}${Integer.valueOf(finalVersionPiece) + versionIncrement}"
    }
    void setNewLibraryVersionInBuildDotGradleFile(String libraryName, String[] buildDotGradleFileLines, String newVersion) {

        def numberedMatchedLine = findLine(buildDotGradleFileLines, '^version =.*')
        if (numberedMatchedLine == null) {
            println "Did NOT set new library version"
            return
        }
        String versionLine = numberedMatchedLine.line
        int versionLineIndex = numberedMatchedLine.lineNumber

        // parse old version out of version line
        String version = versionLine.substring(versionLine.indexOf('=') + 1).replace("'", '').trim()

        // plug the new version into version line
        versionLine = versionLine.replace(version, newVersion)

        // replace old version line with new
        buildDotGradleFileLines[versionLineIndex] = versionLine
    }

    void setNewDependencyLibraryVersionsInBuildDotGradleFile(String currentLibraryName, String[] buildDotGradleFileLines) {
        for (String dependencyLibraryName : finalLibraryVersions.keySet()) {
            if (dependencyLibraryName.equals(currentLibraryName)) {
                continue
            }

            String dependencyLinePattern = getPatternForBuildDotGradleDependencyLine(dependencyLibraryName)
            def numberedMatchedLine = findLine(buildDotGradleFileLines, dependencyLinePattern)
            if (numberedMatchedLine == null) {
                continue
            }

            // parse old version out of dependency line
            String oldVersion = numberedMatchedLine.line.substring(numberedMatchedLine.line.lastIndexOf(':') + 1).replace("'", '').trim()

            // plug the new version into dependency line
            String newDependencyLine = "    ${numberedMatchedLine.line.replace(oldVersion, finalLibraryVersions.get(dependencyLibraryName))}"

            // replace old version line with new
            buildDotGradleFileLines[numberedMatchedLine.lineNumber] = newDependencyLine
        }
    }

    String getPatternForBuildDotGradleDependencyLine(String dependencyLibraryName) {
        String pattern = "\\s*compile\\s+'com\\.(synopsys|blackducksoftware)\\.integration:${dependencyLibraryName}:.*"
        return pattern
    }

    String getPatternForGradleReportedDependencyLine(String dependencyLibraryName) {
        String pattern = "com\\.(synopsys|blackducksoftware)\\.integration:${dependencyLibraryName}:.*"
        return pattern
    }

    def findLine(String[] fileLines, String pattern) {
        def versionLineIndex = 0
        for (int i = 0; i < fileLines.size(); i++) {
            String trimmedLine = fileLines[i].trim()
            if (trimmedLine.matches(pattern)) {
                NumberedLine numberedLine = new NumberedLine(lineNumber: i, line: trimmedLine)
                return numberedLine
            } else {
            }
        }
        return null
    }
    
    class NumberedLine {
        int lineNumber;
        String line;
    }
}
