package com.synopsys.integration.pipeline.jenkins


import com.synopsys.integration.pipeline.logging.PipelineLogger
import org.jenkinsci.plugins.workflow.cps.CpsScript

class JenkinsScriptWrapperDryRun extends JenkinsScriptWrapperImpl {
    final PipelineLogger logger

    JenkinsScriptWrapperDryRun(CpsScript script, PipelineLogger logger) {
        super(script)
        this.logger = logger
    }

    @Override
    int bat(String command) {
        logger.alwaysLog("bat script: ${command}, returnStatus: true")
        return 0
    }

    @Override
    void checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll) {
        logger.alwaysLog("checkout url:${url} branch:${branch} gitTool:${gitToolName} changelog:${changelog} poll:${poll}")
    }


    @Override
    void deleteDir() {
        logger.alwaysLog("deleteDir")
    }

    @Override
    void dir(String relativeDirectory, Closure closure) {
        logger.alwaysLog("dir ${relativeDirectory}")
    }

    @Override
    void emailext(String content, String subjectLine, String recipientList) {
        logger.alwaysLog("emailext body:${content}, subject:${subjectLine}, to:${recipientList}")
    }

    @Override
    EnvActionWrapper env() {
        return new EnvActionWrapperDryRun(script.env, logger)
    }

    @Override
    void jacoco(LinkedHashMap jacocoOptions) {
        logger.alwaysLog("jacoco ${jacocoOptions}")
    }

    @Override
    void junit(LinkedHashMap junitOptions) {
        logger.alwaysLog("junit ${junitOptions}")
    }

    @Override
    void println(String message) {
        script.println message
    }

    @Override
    void pipelineProperties(List pipelineOptions) {
        logger.alwaysLog("properties ${pipelineOptions}")
    }

    @Override
    void step(String[] fields) {
        logger.alwaysLog("step ${fields}")
    }

    @Override
    void archiveArtifacts(String artifactPattern) {
        logger.alwaysLog("archiveArtifacts ${artifactPattern}")
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
        logger.alwaysLog("sh script: ${command}, returnStatus: true")
        return 0
    }

    @Override
    void stage(String stageName, Closure closure) {
        logger.alwaysLog("stage name: ${stageName}")
    }

    @Override
    String tool(String toolName) {
        logger.alwaysLog("tool name: ${toolName}")
        return script().tool(toolName)
    }

}
