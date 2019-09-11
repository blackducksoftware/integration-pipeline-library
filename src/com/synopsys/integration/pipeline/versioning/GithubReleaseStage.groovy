package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.pipeline.exception.GitHubReleaseException
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.exception.PrepareForReleaseException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage

class GithubReleaseStage extends Stage {
    public static final String DEFAULT_GITHUB_OWNER = 'blackducksoftware'
    public static final String DEFAULT_RELEASE_MESSAGE = 'Auto Release'
    public static final String DEFAULT_SCRIPT_URL = 'https://github.com/blackducksoftware/github-auto-release/releases/download/1.2.0/github_auto_release.sh'

    private final JenkinsScriptWrapper scriptWrapper
    private final PipelineLogger logger

    private final String artifactFile
    private final String artifactPattern
    private final String artifactDirectory
    private final String project
    private final String branch

    private String gitToolName = GitStage.DEFAULT_GIT_TOOL
    private String owner = DEFAULT_GITHUB_OWNER
    private String releaseDescription = DEFAULT_RELEASE_MESSAGE
    private String releaseScriptUrl = DEFAULT_SCRIPT_URL

    GithubReleaseStage(JenkinsScriptWrapper scriptWrapper, PipelineLogger logger, String stageName, String project, String branch) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
        this.logger = logger
        this.artifactFile = null
        this.artifactPattern = null
        this.artifactDirectory = null
        this.project = project
        this.branch = branch
    }

    GithubReleaseStage(JenkinsScriptWrapper scriptWrapper, PipelineLogger logger, String stageName, String artifactFile,
                       String project, String branch) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
        this.logger = logger
        this.artifactFile = artifactFile
        this.artifactPattern = null
        this.artifactDirectory = null
        this.project = project
        this.branch = branch
    }

    GithubReleaseStage(JenkinsScriptWrapper scriptWrapper, PipelineLogger logger, String stageName, String artifactPattern, String artifactDirectory,
                       String project, String branch) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
        this.logger = logger
        this.artifactFile = null
        this.artifactPattern = artifactPattern
        this.artifactDirectory = artifactDirectory
        this.project = project
        this.branch = branch
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        String version = scriptWrapper.env().GITHUB_RELEASE_VERSION
        if (null == version || version.trim().length() == 0) {
            throw new PrepareForReleaseException('Could not find the "GITHUB_RELEASE_VERSION" environment variable. Will not perform the GitHub release.')
        }
        List<String> options = []
        options.add('-o')
        options.add(owner)
        options.add('-v')
        options.add(version)
        if (null != artifactFile && artifactFile.trim().length() > 0) {
            options.add('-f')
            options.add(artifactFile)
        }
        if (null != artifactPattern && artifactPattern.trim().length() > 0) {
            options.add('-t')
            options.add(artifactPattern)
        }
        if (null != artifactDirectory && artifactDirectory.trim().length() > 0) {
            options.add('-d')
            options.add(artifactDirectory)
        }
        if (null != project && project.trim().length() > 0) {
            options.add('-p')
            options.add(project)
        }
        options.add('-m')
        options.add("\"${releaseDescription}\"")
        options.add('-br')
        options.add(branch)

        String commandOptions = options.join(' ')

        logger.info("GitHub Auto Release options ${commandOptions}")

        List<String> commandLines = []
        commandLines.add("#!/bin/bash")
        commandLines.add("wget -N \"${releaseScriptUrl}\"")
        commandLines.add("chmod 777 github_auto_release.sh")
        commandLines.add("./github_auto_release.sh ${commandOptions}")
        try {
            scriptWrapper.executeCommand(commandLines.join(" \n"))
        } catch (Exception e) {
            throw new GitHubReleaseException("Failed to run the GitHub auto release ${e.getMessage()}")
        }
    }

    String getArtifactFile() {
        return artifactFile
    }

    String getArtifactPattern() {
        return artifactPattern
    }

    String getArtifactDirectory() {
        return artifactDirectory
    }

    String getProject() {
        return project
    }

    String getBranch() {
        return branch
    }

    String getGitToolName() {
        return gitToolName
    }

    void setGitToolName(String gitToolName) {
        this.gitToolName = gitToolName
    }

    String getOwner() {
        return owner
    }

    void setOwner(String owner) {
        this.owner = owner
    }

    String getReleaseDescription() {
        return releaseDescription
    }

    void setReleaseDescription(final String releaseDescription) {
        this.releaseDescription = releaseDescription
    }

    String getReleaseScriptUrl() {
        return releaseScriptUrl
    }

    void setReleaseScriptUrl(final String releaseScriptUrl) {
        this.releaseScriptUrl = releaseScriptUrl
    }
}
