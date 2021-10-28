package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.exception.CommandExecutionException
import com.synopsys.integration.pipeline.scm.GitStage
import org.jenkinsci.plugins.workflow.cps.CpsScript

class JenkinsScriptWrapperImpl implements JenkinsScriptWrapper {
    final CpsScript script

    JenkinsScriptWrapperImpl(final CpsScript script) {
        this.script = script
    }

    @Override
    void archiveArtifacts(String artifactPattern) {
        script.archiveArtifacts artifactPattern
    }

    @Override
    int bat(String command) throws CommandExecutionException {
        try {
            return script.bat(script: command, returnStatus: true)
        } catch (Exception e) {
            throw new CommandExecutionException(e.getMessage(), e)
        }
    }

    @Override
    String bat(String command, Boolean returnStdout) throws CommandExecutionException {
        try {
            return script.bat(script: command, returnStdout: returnStdout)
        } catch (Exception e) {
            throw new CommandExecutionException(e.getMessage(), e)
        }
    }

    @Override
    void checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll, String credentialsId) {
        String localBranch = branch.replace("origin/", "")
        script.checkout changelog: changelog, poll: poll, scm: [$class : 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false,
                                                                extensions: [[$class: 'WipeWorkspace'], [$class: 'LocalBranch', localBranch: localBranch]],
                                                                gitTool: gitToolName, submoduleCfg: [], userRemoteConfigs: [[credentialsId: credentialsId, url: url]]]
    }

    void closure(Closure closure) {
        closure.call()
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
    int executeCommand(String command) {
        if (isUnix()) {
            return sh(command)
        }
        return bat(command)
    }

    @Override
    String executeCommand(String command, Boolean returnStdout) {
        if (isUnix()) {
            return sh(command, returnStdout)
        }
        return bat(command, returnStdout)
    }

    @Override
    void executeCommandWithException(String command) throws CommandExecutionException {
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
    void executeGitPushToGithub(PipelineConfiguration pipelineConfiguration, String url, String githubCredentialsId, String gitPath) throws CommandExecutionException {
        assert url.startsWith(GitStage.GITHUB_HTTPS) : "Required to use " + GitStage.GITHUB_HTTPS + " when publishing to github"
        script.withCredentials([script.usernamePassword(credentialsId: githubCredentialsId, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
            String gitPassword = pipelineConfiguration.getScriptWrapper().getJenkinsProperty('GIT_PASSWORD')
            String gitUsername= pipelineConfiguration.getScriptWrapper().getJenkinsProperty('GIT_USERNAME')
            String adjustedBranch = url.replace("https://","https://${gitUsername}:${gitPassword}@")

            executeCommandWithException("${gitPath} push ${adjustedBranch}")
        }
    }

    String getJenkinsProperty(String propertyName) {
        script.env.getProperty(propertyName)
    }

    void setJenkinsProperty(String propertyName, String value) {
        script.env.setProperty(propertyName, value)
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
    void pipelineProperties(List pipelineOptions) {
        script.properties(pipelineOptions)
    }

    @Override
    String readFile(String fileName) {
        return script.readFile(fileName)
    }

    @Override
    void step(String[] fields) {
        script.step fields
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
        try {
            return script.sh(script: command, returnStatus: true)
        } catch (Exception e) {
            throw new CommandExecutionException(e.getMessage(), e)
        }
    }

    @Override
    String sh(String command, Boolean returnStdout) throws CommandExecutionException {
        try {
            return script.sh(script: command, returnStdout: returnStdout)
        } catch (Exception e) {
            throw new CommandExecutionException(e.getMessage(), e)
        }
    }

    @Override
    void stage(String stageName, Closure closure) {
        script.stage(stageName, closure)
    }

    @Override
    String tool(String toolName) {
        return script.tool(toolName)
    }

    @Override
    void writeFile(final String fileName, final String text) {
        script.writeFile(file: fileName, text: text)
    }
}
