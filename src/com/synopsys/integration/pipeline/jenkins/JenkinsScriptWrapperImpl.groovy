package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.exception.CommandExecutionException
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import org.jenkinsci.plugins.workflow.cps.CpsScript

class JenkinsScriptWrapperImpl implements JenkinsScriptWrapper, Serializable {
    final CpsScript script

    JenkinsScriptWrapperImpl(final CpsScript script) {
        this.script = script
    }

    @Override
    int bat(String command) {
        return script.bat(script: command, returnStatus: true)
    }

    @Override
    void checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll) {
        script.checkout changelog: changelog, poll: poll, scm: [$class : 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false,
                                                                gitTool: gitToolName, submoduleCfg: [], userRemoteConfigs: [[url: url]]]
    }

    @Override
    BuildWrapper currentBuild() {
        return new BuildWrapperImpl(script.currentBuild)
    }

    @Override
    void deleteDir() {
        script.deleteDir()
    }

    @Override
    void dir(String relativeDirectory, Closure closure) {
        script.dir(relativeDirectory, closure)
    }

    @Override
    void emailext(String content, String subjectLine, String recipientList) {
        script.emailext(body: content, subject: subjectLine, to: recipientList)
    }

    @Override
    EnvActionWrapper env() {
        return new EnvActionWrapperImpl(script.env)
    }

    @Override
    int executeCommand(String command) {
        if (isUnix()) {
            return sh(command)
        }
        return bat(command)
    }

    @Override
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

    @Override
    boolean isUnix() {
        return script.isUnix()
    }

    @Override
    void jacoco(LinkedHashMap jacocoOptions) {
        script.jacoco jacocoOptions
    }

    @Override
    void junit(LinkedHashMap junitOptions) {
        script.junit junitOptions
    }

    @Override
    void println(String message) {
        script.println message
    }

    @Override
    void pipelineProperties(ArrayList pipelineOptions) {
        script.properties(pipelineOptions)
    }

    @Override
    void step(String[] fields) {
        script.step fields
    }

    @Override
    void archiveArtifacts(String artifactPattern) {
        script.archiveArtifacts artifactPattern
    }

    /**
     * WorkflowScript/CpsScript
     **/
    @Override
    CpsScript script() {
        return script
    }

    @Override
    int sh(String command) {
        return script.sh(script: command, returnStatus: true)
    }

    @Override
    void stage(String stageName, Closure closure) {
        script.stage(stageName, closure)
    }

    @Override
    String tool(String toolName) {
        return script.tool(toolName)
    }

}
