class ToolRunner {
    static String TOKEN_PRECEDING_DEPENDENCY = '---'
    static String DEPENDENCY_TRAILING_DELIMITER = ' '

    String getProjectVersionString(File dir) {
        try {
            List<String> propsOutputLines = execute(dir, Arrays.asList("./gradlew", "properties", "-q"), null)
            int versionLineIndex = 0
            for (int i = 0; i < propsOutputLines.size(); i++) {
                String trimmedLine = propsOutputLines[i].trim()
                if (trimmedLine.startsWith('version: ')) {
                    String versionLine = trimmedLine
                    String version = versionLine.substring(versionLine.indexOf(':') + 1).trim()
                    return version
                }
            }
            println "Failed to find the project version in the gradle properties command"
        } catch (Exception e) {
            String msg = "Error running the gradle properties command to get the project version, or interpreting its output: ${e.getMessage()}"
            throw new RuntimeException(msg, e)
        }
        return null
    }

    List<String> getCompileDependencies(File dir) {
        List<String> dependencies = new ArrayList<>()
        List<String> dependenciesOutputLines = execute(dir, Arrays.asList("./gradlew", "dependencies", "--configuration", "compile"), null)
        boolean inDependenciesList = false
        for (String dependenciesOutputLine : dependenciesOutputLines) {
            if (!inDependenciesList) {
                // we haven't reached dependencies yet
                if (dependenciesOutputLine.startsWith("compile - Dependencies")) {
                    inDependenciesList = true
                }
                continue
            }
            // we're in, or done with, dependencies
            if (dependenciesOutputLine.contains(TOKEN_PRECEDING_DEPENDENCY)) {
                int tokenPrecedingDependencyIndex = dependenciesOutputLine.indexOf(TOKEN_PRECEDING_DEPENDENCY);
                String dep = dependenciesOutputLine.substring(tokenPrecedingDependencyIndex+(TOKEN_PRECEDING_DEPENDENCY.length()))
                dep = dep.trim()
                int delimeterFollowingDependency = dep.indexOf(DEPENDENCY_TRAILING_DELIMITER);
                if (delimeterFollowingDependency >= 0) {
                    dep = dep.substring(0, delimeterFollowingDependency)
                }
                dependencies.add(dep)
                continue
            }
            // we've reached the line after end of dependencies
            break
        }
        return dependencies
    }

    List<String> diff(File libraryDir) {
        return execute(libraryDir, Arrays.asList("git", "diff"), null)
    }
    
    void cloneLibraries(File workspaceDir, String url) {
        List<String> gitCloneOutput = execute(workspaceDir, Arrays.asList("git", "clone", url), "fatal:")
    }

    List<String> commit(File libraryDir, String commitMessage) {
        List<String> gitAddOutput = execute(libraryDir, Arrays.asList("git", "add", "build.gradle"), null)
        println "gitAddOutput: ${gitAddOutput}"

        List<String> gitCommitOutput = execute(libraryDir, Arrays.asList("git", "commit", "-m", commitMessage), null)
        println "gitCommitOutput: ${gitCommitOutput}"

        List<String> gitPushOutput = execute(libraryDir, Arrays.asList("git", "push"), null)
        println "gitPushOutput: ${gitPushOutput}"
        return gitPushOutput
    }
    
    boolean build(File libraryDir) {
        printf "Building ${libraryDir.getName()}... "
        List<String> buildOutput = execute(libraryDir, Arrays.asList("./gradlew", "clean", "build", "install"), null)
        boolean succeeded = evaluateBuildOutput(buildOutput)
        if (succeeded) {
            println "Succeeded"
        } else {
            println "FAILED"
        }
        return succeeded
    }

    void reset(File libraryDir) {
        execute(libraryDir, Arrays.asList("git", "reset", "--hard"), null)
    }
    
    boolean evaluateBuildOutput(List<String> buildOutput) {
        for (String line : buildOutput) {
            if (line.contains("BUILD SUCCESSFUL")) {
                return true
            }
        }
        return false
    }

    List<String> execute(File dir, List<String> args, errorIndicatorStringStdErr) {
        if (!dir.isDirectory()) {
            String msg = "ERROR: directory ${dir.getAbsolutePath()} does not exist or is not a directory"
            throw new RuntimeException(msg)
        }
        ProcessBuilder pb = new ProcessBuilder(args)
        pb = pb.directory(dir)
        Process process = pb.start()
        process.waitFor()
        InputStream is = process.getInputStream()
        List<String> outputLines = new ArrayList<>()
        String line = null;
        BufferedReader bufferedReader = null
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))
            while ((line = bufferedReader.readLine()) != null) {
                outputLines.add(line);
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close()
            }
        }
        
        if (errorIndicatorStringStdErr != null) {
            InputStream isStdErr = process.getErrorStream()
            String lineStdErr = null;
            BufferedReader bufferedReaderStdErr = null
            try {
                bufferedReaderStdErr = new BufferedReader(new InputStreamReader(isStdErr, java.nio.charset.StandardCharsets.UTF_8))
                while ((lineStdErr = bufferedReaderStdErr.readLine()) != null) {
                    if (lineStdErr.contains(errorIndicatorStringStdErr)) {
                        throw new RuntimeException("Error: ${args.get(0)} failed: ${lineStdErr}")
                    }
                }
            } finally {
                if (bufferedReaderStdErr != null) {
                    bufferedReaderStdErr.close()
                }
            }

        }
        return outputLines
    }
}