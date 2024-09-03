package com.blackduck.integration.pipeline.jenkins


import net.sf.json.JSONObject
import org.jenkinsci.plugins.workflow.cps.CpsScript

interface JenkinsScriptWrapper extends Serializable {
    void archiveArtifacts(String artifactPattern)

    int bat(String command) throws com.blackduck.integration.pipeline.exception.CommandExecutionException

    String bat(String command, Boolean returnStdout) throws com.blackduck.integration.pipeline.exception.CommandExecutionException

    Map<String, String> checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll, String credentialsId)

    void closure(Closure closure)

    BuildWrapper currentBuild()

    void deleteDir()

    void dir(String relativeDirectory, Closure closure)

    void emailext(String content, String subjectLine, String recipientList)

    String getJenkinsProperty(String propertyName)

    void setJenkinsProperty(String propertyName, String value)

    int executeCommand(String command)

    String executeCommand(String command, Boolean returnStdout)

    void executeCommandWithHttpStatusCheck(String command, int expectedHttpStatusCode, String jsonResponseFileName, String githubCredentialsId, PipelineConfiguration pipelineConfiguration)

    String executeWithCredentials(PipelineConfiguration pipelineConfiguration, String command, String githubCredentialsId)

    void executeCommandWithException(String command) throws com.blackduck.integration.pipeline.exception.CommandExecutionException

    void executeCommandWithCatchError(String command)

    void executeGitPushToGithub(PipelineConfiguration pipelineConfiguration, String url, String githubCredentialsId, String gitPath) throws com.blackduck.integration.pipeline.exception.CommandExecutionException

    boolean isUnix()

    void jacoco(LinkedHashMap jacocoOptions)

    void junit(LinkedHashMap junitOptions)

    void println(String message)

    void pipelineProperties(List pipelineOptions)

    String readFile(String fileName)

    CpsScript script()

    int sh(String command) throws com.blackduck.integration.pipeline.exception.CommandExecutionException

    String sh(String command, Boolean returnStdout) throws com.blackduck.integration.pipeline.exception.CommandExecutionException

    void stage(String stageName, Closure closure)

    void step(String[] fields)

    String tool(String toolName)

    void writeFile(String fileName, String text)

    void writeJsonFile(String fileName, Map data)

    JSONObject readJsonFile(String fileName)

    Map<?, ?> readYamlFile(String fileName)

    File[] findFileGlob(String glob)

    void triggerPushToGCR(String imageList, String gcrRepo)
}
