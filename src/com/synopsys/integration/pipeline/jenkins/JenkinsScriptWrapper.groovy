package com.synopsys.integration.pipeline.jenkins


import com.synopsys.integration.pipeline.exception.CommandExecutionException
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import org.jenkinsci.plugins.workflow.cps.CpsScript
import org.jenkinsci.plugins.workflow.cps.EnvActionImpl
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

class JenkinsScriptWrapper implements Serializable {
    final CpsScript script

    JenkinsScriptWrapper(final CpsScript script) {
        this.script = script
    }

    int executeCommand(String command) {
        if (isUnix()) {
            return sh(command)
        }
        return bat(command)
    }

    void executeCommandWithException(String command) throws CommandExecutionException {
        PipelineLogger logger = new DefaultPipelineLoger(this)
        int errorStatus
        if (isUnix()) {
            errorStatus = sh(command)
        } else {
            errorStatus = bat(command)
        }
        if (errorStatus != 0) {
            throw new CommandExecutionException(errorStatus, "Executing command '${command}', resulted in error status code '${errorStatus}'")
        }
    }

    // Add options to get the standard out from the commands

    int sh(String command) {
        return script.sh(script: command, returnStatus: true)
    }

    int bat(String command) {
        return script.bat(script: command, returnStatus: true)
    }

    boolean isUnix() {
        return script.isUnix()
    }

    void checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll) {
        script.checkout changelog: changelog, poll: poll, scm: [$class: 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false, gitTool: gitToolName, submoduleCfg: [], userRemoteConfigs: [[url: url]]]
    }

    void stage(String stageName, Closure closure) {
        script.stage(stageName, closure)
    }

    void dir(String relativeDirectory, Closure closure) {
        script.dir(relativeDirectory, closure)
    }

    void println(String message) {
        script.println message
    }

    void step(String[] fields) {
        script.step fields
    }

    void archiveArtifacts(String artifactPattern) {
        script.archiveArtifacts artifactPattern
    }

    void junit(boolean allowEmptyResults, String xmlPattern) {
        script.junit allowEmptyResults: allowEmptyResults, testResults: xmlPattern
    }

    void jacoco(Object object) {
        PipelineLogger logger = new DefaultPipelineLoger(this)
        logger.info("Jacoco options ${object}")
        script.jacoco()
    }

    /**
     * org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper
     **/
    RunWrapper currentBuild() {
        return script.currentBuild
    }

    /**
     * org.jenkinsci.plugins.workflow.cps.EnvActionImpl
     **/
    EnvActionImpl env() {
        return script.env
    }

    void emailext(String content, String subjectLine, String recipientList) {
        script.emailext(body: content, subject: subjectLine, to: recipientList)
    }

    String tool(String toolName) {
        return script.tool(toolName)
    }

    /**
     * WorkflowScript/CpsScript
     **/
    CpsScript script() {
        return script
    }

}
