package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.exception.CommandExecutionException
import org.jenkinsci.plugins.workflow.cps.CpsScript

interface JenkinsScriptWrapper extends Serializable {
    int bat(String command)

    void checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll)

    BuildWrapper currentBuild()

    void deleteDir()

    void dir(String relativeDirectory, Closure closure)

    void emailext(String content, String subjectLine, String recipientList)

    EnvActionWrapper env()

    int executeCommand(String command)

    void executeCommandWithException(String command) throws CommandExecutionException

    boolean isUnix()

    void jacoco(LinkedHashMap jacocoOptions)

    void junit(LinkedHashMap junitOptions)

    void println(String message)

    void pipelineProperties(List pipelineOptions)

    void step(String[] fields)

    void archiveArtifacts(String artifactPattern)

    CpsScript script()

    int sh(String command)

    void stage(String stageName, Closure closure)

    String tool(String toolName)

}
