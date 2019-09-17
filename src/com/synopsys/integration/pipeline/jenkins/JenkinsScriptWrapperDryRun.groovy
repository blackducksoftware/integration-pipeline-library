package com.synopsys.integration.pipeline.jenkins


import org.jenkinsci.plugins.workflow.cps.CpsScript

class JenkinsScriptWrapperDryRun extends JenkinsScriptWrapperImpl {
    public final DryRunPipelineBuilder dryRunPipelineBuilder

    JenkinsScriptWrapperDryRun(CpsScript script, DryRunPipelineBuilder dryRunPipelineBuilder) {
        super(script)
        this.dryRunPipelineBuilder = dryRunPipelineBuilder
    }

    @Override
    int bat(String command) {
        getDryRunPipelineBuilder().addPipelineLine("bat script: ${command}, returnStatus: true")
        return 0
    }

    @Override
    void checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll) {
        getDryRunPipelineBuilder().addPipelineLine("checkout url:${url} branch:${branch} gitTool:${gitToolName} changelog:${changelog} poll:${poll}")
    }

    @Override
    BuildWrapper currentBuild() {
        return new BuildWrapperDryRun(script.currentBuild, getDryRunPipelineBuilder())
    }


    @Override
    void deleteDir() {
        getDryRunPipelineBuilder().addPipelineLine("deleteDir")
    }

    @Override
    void dir(String relativeDirectory, Closure closure) {
        getDryRunPipelineBuilder().addPipelineLine("dir(${relativeDirectory}) {")
        getDryRunPipelineBuilder().increaseIndent()
        closure.call()
        getDryRunPipelineBuilder().decreaseIndent()
        getDryRunPipelineBuilder().addPipelineLine("}")
    }

    @Override
    void emailext(String content, String subjectLine, String recipientList) {
        getDryRunPipelineBuilder().addPipelineLine("emailext body:${content}, subject:${subjectLine}, to:${recipientList}")
    }

    String getJenkinsProperty(String propertyName) {
        getDryRunPipelineBuilder().addPipelineLine("getProperty name: ${propertyName}")
    }

    void setJenkinsProperty(String propertyName, String value) {
        getDryRunPipelineBuilder().addPipelineLine("setProperty name: ${propertyName} value: ${value}")
    }


    @Override
    void jacoco(LinkedHashMap jacocoOptions) {
        getDryRunPipelineBuilder().addPipelineLine("jacoco ${jacocoOptions}")
    }

    @Override
    void junit(LinkedHashMap junitOptions) {
        getDryRunPipelineBuilder().addPipelineLine("junit ${junitOptions}")
    }

    @Override
    void println(String message) {}

    @Override
    void pipelineProperties(List pipelineOptions) {
        getDryRunPipelineBuilder().addPipelineLine("properties ${pipelineOptions}")
    }

    @Override
    void step(String[] fields) {
        getDryRunPipelineBuilder().addPipelineLine("step ${fields}")
    }

    @Override
    void archiveArtifacts(String artifactPattern) {
        getDryRunPipelineBuilder().addPipelineLine("archiveArtifacts ${artifactPattern}")
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
        getDryRunPipelineBuilder().addPipelineLine("sh script: ${command}, returnStatus: true")
        return 0
    }

    @Override
    void stage(String stageName, Closure closure) {
        getDryRunPipelineBuilder().addPipelineLine("stage(${stageName}) {")
        getDryRunPipelineBuilder().increaseIndent()
        closure.call()
        getDryRunPipelineBuilder().decreaseIndent()
        getDryRunPipelineBuilder().addPipelineLine("}")
    }

    @Override
    String tool(String toolName) {
        getDryRunPipelineBuilder().addPipelineLine("tool name: ${toolName}")
        return script().tool(toolName)
    }

    DryRunPipelineBuilder getDryRunPipelineBuilder() {
        return dryRunPipelineBuilder
    }
}
