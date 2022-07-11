package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.exception.CommandExecutionException
import net.sf.json.JSONObject
import org.jenkinsci.plugins.workflow.cps.CpsScript

interface JenkinsScriptWrapper extends Serializable {
    void archiveArtifacts(String artifactPattern)

    int bat(String command) throws CommandExecutionException

    String bat(String command, Boolean returnStdout) throws CommandExecutionException

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

    void executeCommandWithHttpStatusCheck(String command, String expectedHttpStatusCode, String jsonResponseFileName, String githubCredentialsId, PipelineConfiguration pipelineConfiguration)

    void executeCommandWithHttpStatusCheckNumber(String command, String expectedHttpStatusCode, String jsonResponseFileName, int assetNum)

    void executeCommandWithException(String command) throws CommandExecutionException

    void executeCommandWithCatchError(String command)

    void executeGitPushToGithub(PipelineConfiguration pipelineConfiguration, String url, String githubCredentialsId, String gitPath) throws CommandExecutionException

    boolean isUnix()

    void jacoco(LinkedHashMap jacocoOptions)

    void junit(LinkedHashMap junitOptions)

    void println(String message)

    void pipelineProperties(List pipelineOptions)

    String readFile(String fileName)

    CpsScript script()

    int sh(String command) throws CommandExecutionException

    String sh(String command, Boolean returnStdout) throws CommandExecutionException

    void stage(String stageName, Closure closure)

    void step(String[] fields)

    String tool(String toolName)

    void writeFile(String fileName, String text)

    void writeJsonFile(String fileName, Map data)

    JSONObject readJsonFile(String fileName)

}
