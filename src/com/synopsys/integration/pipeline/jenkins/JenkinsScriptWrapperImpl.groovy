package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.exception.CommandExecutionException
import com.synopsys.integration.pipeline.scm.GitStage
import net.sf.json.JSONObject
import org.apache.commons.lang3.StringUtils
import org.jenkinsci.plugins.workflow.cps.CpsScript

class JenkinsScriptWrapperImpl implements JenkinsScriptWrapper {
    final CpsScript script
    String USERNAME_SEARCH_TOKEN = 'bob'
    String PASSWORD_SEARCH_TOKEN = 'joe'

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
    Map<String, String> checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll, String credentialsId) {
        String localBranch = branch.replace("origin/", "")
        Map<String, String> checkoutData = script.checkout changelog: changelog, poll: poll, scm: [$class    : 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false,
                                                                                                   extensions: [[$class: 'WipeWorkspace'], [$class: 'LocalBranch', localBranch: localBranch]],
                                                                                                   gitTool   : gitToolName, submoduleCfg: [], userRemoteConfigs: [[credentialsId: credentialsId, url: url]]]
        return checkoutData
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
    void executeCommandWithHttpStatusCheck(String command, String expectedHttpStatusCode, String jsonResponseFileName, String githubCredentialsId, PipelineConfiguration pipelineConfiguration, String assetNaming) {
        if (assetNaming.length() > 1)
            jsonResponseFileName = "asset-" + StringUtils.substringAfterLast(assetNaming, '/') + ".json"

        // adding the http code checker command and sending output into jsonResponseFileName file
        //script.withCredentials([script.usernamePassword(credentialsId: githubCredentialsId, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
        //    String gitPassword = pipelineConfiguration.getScriptWrapper().getJenkinsProperty('GIT_PASSWORD')
        //    String newCommand = command + " -H \"Authorization: token ${gitPassword}\" -o ${jsonResponseFileName} -w %{http_code}"

            //taking the Http status code
        //    String receivedHttpStatusCode = executeCommand(newCommand, true)
            // If receivedHttpStatusCode != expectedHttpStatusCode throw. 201 is the success code
        //    if (receivedHttpStatusCode != (expectedHttpStatusCode)) {
         //       throw new Exception("Did not return ${expectedHttpStatusCode} HTTP code, not successful. Instead returned ${receivedHttpStatusCode}")
        //    }
       // }

        String newCommand = command + " -H \"Authorization: token ${PASSWORD_SEARCH_TOKEN}\" -o ${jsonResponseFileName} -w %{http_code}"

        //taking the Http status code
        String receivedHttpStatusCode = executeWithCredentials(pipelineConfiguration, newCommand, githubCredentialsId)
        // If receivedHttpStatusCode != expectedHttpStatusCode throw. 201 is the success code
        if (receivedHttpStatusCode != (expectedHttpStatusCode)) {
            throw new Exception("Did not return ${expectedHttpStatusCode} HTTP code, not successful. Instead returned ${receivedHttpStatusCode}")
        }

        //ensuring the output json file is in pretty formatting
        writeJsonFile(jsonResponseFileName, readJsonFile(jsonResponseFileName))

        //adding the json output as an artifact to the release
        archiveArtifacts(jsonResponseFileName)
    }

    @Override
    String executeWithCredentials(PipelineConfiguration pipelineConfiguration, String command, String githubCredentialsId) throws CommandExecutionException {
        script.withCredentials([script.usernamePassword(credentialsId: githubCredentialsId, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
            String gitUsername = pipelineConfiguration.getScriptWrapper().getJenkinsProperty('GIT_USERNAME')
            String gitPassword = pipelineConfiguration.getScriptWrapper().getJenkinsProperty('GIT_PASSWORD')
            String adjustedCommand = command.replaceAll(USERNAME_SEARCH_TOKEN, gitUsername).replaceAll(PASSWORD_SEARCH_TOKEN, gitPassword)

            return executeCommand(adjustedCommand, true)
        }
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
    void executeCommandWithCatchError(String command) {
        script.catchError(stageResult: 'FAILURE') {
            executeCommandWithException(command)
        }
    }

    @Override
    void executeGitPushToGithub(PipelineConfiguration pipelineConfiguration, String url, String githubCredentialsId, String gitPath) throws CommandExecutionException {
        assert url.startsWith(GitStage.GITHUB_HTTPS): "Required to use " + GitStage.GITHUB_HTTPS + " when publishing to github"

        //String adjustedBranch = url.replace("https://", "https://${USERNAME_SEARCH_TOKEN}:${PASSWORD_SEARCH_TOKEN}@")
        String adjustedBranch = url.replace("https://", "https://bob:george@")
        String pushCommand = "${gitPath} push ${adjustedBranch} --porcelain 2>&1"
        String pushCommandStdOut = executeWithCredentials(pipelineConfiguration, pushCommand, githubCredentialsId)
        pipelineConfiguration.getLogger().info("hello33 " + pushCommandStdOut)
        assert !pushCommandStdOut.contains("!\t")

        //script.withCredentials([script.usernamePassword(credentialsId: githubCredentialsId, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
        //    String gitPassword = pipelineConfiguration.getScriptWrapper().getJenkinsProperty('GIT_PASSWORD')
        //    String gitUsername = pipelineConfiguration.getScriptWrapper().getJenkinsProperty('GIT_USERNAME')
        //    String adjustedBranch = url.replace("https://", "https://${gitUsername}:${gitPassword}@")

        //    executeCommandWithException("${gitPath} push ${adjustedBranch}")
       // }
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

    @Override
    void writeJsonFile(String fileName, Map data) {
        script.writeJSON(file: fileName, json: data, pretty: 4)
    }

    @Override
    JSONObject readJsonFile(String fileName) {
        return script.readJSON(file: fileName)
    }
}
