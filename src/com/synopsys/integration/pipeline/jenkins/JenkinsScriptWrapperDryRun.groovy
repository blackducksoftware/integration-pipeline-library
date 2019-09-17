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
        dryRunPipelineBuilder.addPipelineLine("bat script: ${command}, returnStatus: true")
        return 0
    }

    @Override
    void checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll) {
        dryRunPipelineBuilder.addPipelineLine("checkout url:${url} branch:${branch} gitTool:${gitToolName} changelog:${changelog} poll:${poll}")
    }

    @Override
    BuildWrapper currentBuild() {
        return new BuildWrapperDryRun(script.currentBuild, dryRunPipelineBuilder)
    }


    @Override
    void deleteDir() {
        dryRunPipelineBuilder.addPipelineLine("deleteDir")
    }

    @Override
    void dir(String relativeDirectory, Closure closure) {
        dryRunPipelineBuilder.addPipelineLine("dir(${relativeDirectory}) {")
        dryRunPipelineBuilder.increaseIndent()
        closure.call()
        dryRunPipelineBuilder.decreaseIndent()
        dryRunPipelineBuilder.addPipelineLine("}")
    }

    @Override
    void emailext(String content, String subjectLine, String recipientList) {
        dryRunPipelineBuilder.addPipelineLine("emailext body:${content}, subject:${subjectLine}, to:${recipientList}")
    }

    @Override
    EnvActionWrapper env() {
        return new EnvActionWrapperDryRun(script.env, dryRunPipelineBuilder)
    }

    @Override
    void jacoco(LinkedHashMap jacocoOptions) {
        dryRunPipelineBuilder.addPipelineLine("jacoco ${jacocoOptions}")
    }

    @Override
    void junit(LinkedHashMap junitOptions) {
        dryRunPipelineBuilder.addPipelineLine("junit ${junitOptions}")
    }

    @Override
    void println(String message) {}

    @Override
    void pipelineProperties(List pipelineOptions) {
        dryRunPipelineBuilder.addPipelineLine("properties ${pipelineOptions}")
    }

    @Override
    void step(String[] fields) {
        dryRunPipelineBuilder.addPipelineLine("step ${fields}")
    }

    @Override
    void archiveArtifacts(String artifactPattern) {
        dryRunPipelineBuilder.addPipelineLine("archiveArtifacts ${artifactPattern}")
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
        dryRunPipelineBuilder.addPipelineLine("sh script: ${command}, returnStatus: true")
        return 0
    }

    @Override
    void stage(String stageName, Closure closure) {
        dryRunPipelineBuilder.addPipelineLine("stage(${stageName}) {")
        dryRunPipelineBuilder.increaseIndent()
        closure.call()
        dryRunPipelineBuilder.decreaseIndent()
        dryRunPipelineBuilder.addPipelineLine("}")
    }

    @Override
    String tool(String toolName) {
        dryRunPipelineBuilder.addPipelineLine("tool name: ${toolName}")
        return script().tool(toolName)
    }

    DryRunPipelineBuilder getDryRunPipelineBuilder() {
        return dryRunPipelineBuilder
    }
}
