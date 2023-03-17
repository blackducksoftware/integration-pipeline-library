package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.exception.CommandExecutionException
import net.sf.json.JSONObject
import org.jenkinsci.plugins.workflow.cps.CpsScript

class JenkinsScriptWrapperDryRun extends JenkinsScriptWrapperImpl {
    public final DryRunPipelineBuilder dryRunPipelineBuilder

    JenkinsScriptWrapperDryRun(CpsScript script, DryRunPipelineBuilder dryRunPipelineBuilder) {
        super(script)
        this.dryRunPipelineBuilder = dryRunPipelineBuilder
    }

    @Override
    int bat(String command) throws CommandExecutionException {
        getDryRunPipelineBuilder().addPipelineLine("bat script: ${command}, returnStatus: true")
        return 0
    }

    @Override
    String bat(String command, Boolean returnStdout) throws CommandExecutionException {
        getDryRunPipelineBuilder().addPipelineLine("bat script: ${command}, returnStdout: ${returnStdout}")
        return ""
    }


    @Override
    Map<String, String> checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll, String credentialsId) {
        getDryRunPipelineBuilder().addPipelineLine("checkout url:${url} branch:${branch} gitTool:${gitToolName} changelog:${changelog} poll:${poll} credentialsId:${credentialsId}")
        return new HashMap<>()
    }

    void closure(Closure closure) {
        getDryRunPipelineBuilder().addPipelineLine("closure {CAN NOT SHOW CUSTOM CLOSURE}")
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
    void pipelineProperties(List pipelineOptions) {
        getDryRunPipelineBuilder().addPipelineLine("properties ${pipelineOptions}")
    }

    @Override
    String readFile(String fileName) {
        getDryRunPipelineBuilder().addPipelineLine("readFile file: ${fileName}")
        return ""
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
    int sh(String command) throws CommandExecutionException {
        getDryRunPipelineBuilder().addPipelineLine("sh script: ${command}, returnStatus: true")
        return 0
    }

    @Override
    String sh(String command, Boolean returnStdout) throws CommandExecutionException {
        getDryRunPipelineBuilder().addPipelineLine("sh script: ${command}, returnStdout: ${returnStdout}")
        return ""
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

    @Override
    void writeFile(String fileName, String text) {
        getDryRunPipelineBuilder().addPipelineLine("writeFile file: ${fileName}")
    }

    @Override
    void writeJsonFile(String fileName, Map data) {
        getDryRunPipelineBuilder().addPipelineLine("writeJsonFile file: ${fileName}")
    }

    @Override
    JSONObject readJsonFile(String fileName) {
        getDryRunPipelineBuilder().addPipelineLine("readJsonFile file: ${fileName}")
        return new JSONObject()
    }

    @Override
    Map<?, ?> readYamlFile(String fileName) {
        getDryRunPipelineBuilder().addPipelineLine("readYamlFile file: ${fileName}")
        return new HashMap<>()
    }
}
