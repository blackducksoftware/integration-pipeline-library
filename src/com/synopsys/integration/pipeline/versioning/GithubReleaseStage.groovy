package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.GitHubReleaseException
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.exception.PrepareForReleaseException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.utilities.GithubBranchParser
import org.apache.commons.lang3.StringUtils

class GithubReleaseStage extends Stage {
    public static final String DEFAULT_GITHUB_OWNER = 'blackducksoftware'
    public static final String DEFAULT_RELEASE_MESSAGE = 'Auto Release'
    public static final String DEFAULT_SCRIPT_URL = 'https://github.com/blackducksoftware/github-auto-release/releases/download/2.1.0/github_auto_release.sh'
    public static final String GITHUB_RELEASE_VERSION = 'GITHUB_RELEASE_VERSION'

    private final boolean runRelease
    private final String artifactFile
    private final String artifactPattern
    private final String artifactDirectory
    private final String branch

    private String gitToolName = GitStage.DEFAULT_GIT_TOOL
    private String owner = DEFAULT_GITHUB_OWNER
    private String releaseDescription = DEFAULT_RELEASE_MESSAGE
    private String releaseScriptUrl = DEFAULT_SCRIPT_URL
    private String project = null

    GithubReleaseStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, String branch) {
        super(pipelineConfiguration, stageName)
        this.runRelease = runRelease
        this.artifactFile = null
        this.artifactPattern = null
        this.artifactDirectory = null
        this.branch = branch
    }

    GithubReleaseStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, String artifactFile,
                       String branch) {
        super(pipelineConfiguration, stageName)
        this.runRelease = runRelease
        this.artifactFile = artifactFile
        this.artifactPattern = null
        this.artifactDirectory = null
        this.branch = branch
    }

    GithubReleaseStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, String artifactPattern,
                       String artifactDirectory, String branch) {
        super(pipelineConfiguration, stageName)
        this.runRelease = runRelease
        this.artifactFile = null
        this.artifactPattern = artifactPattern
        this.artifactDirectory = artifactDirectory
        this.branch = branch
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        if (!runRelease) {
            getPipelineConfiguration().getLogger().info("Skipping the ${this.getClass().getSimpleName()} because this is not a release.")
            return
        }
        String version = getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(GITHUB_RELEASE_VERSION)
        if (StringUtils.isBlank(version)) {
            throw new PrepareForReleaseException("Could not find the \"${GITHUB_RELEASE_VERSION}\" environment variable. Will not perform the GitHub release.")
        }
        List<String> options = []
        options.add('-o')
        options.add(owner)
        options.add('-v')
        options.add(version)
        if (StringUtils.isNotBlank(artifactFile)) {
            options.add('-f')
            options.add(artifactFile)
        }
        if (StringUtils.isNotBlank(artifactPattern)) {
            options.add('-t')
            options.add(artifactPattern)
        }
        if (StringUtils.isNotBlank(artifactDirectory)) {
            options.add('-d')
            options.add(artifactDirectory)
        }
        if (StringUtils.isNotBlank(project)) {
            options.add('-p')
            options.add(project)
        }
        options.add('-m')
        options.add("\"${releaseDescription}\"")

        GithubBranchParser githubBranchParser = new GithubBranchParser()
        GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

        options.add('-br')
        options.add(githubBranchModel.getBranchName())

        String commandOptions = options.join(' ')

        getPipelineConfiguration().getLogger().info("GitHub Auto Release options ${commandOptions}")

        List<String> commandLines = []
        commandLines.add("#!/bin/bash")
        commandLines.add("wget -N \"${releaseScriptUrl}\"")
        commandLines.add("chmod 777 github_auto_release.sh")
        commandLines.add("./github_auto_release.sh ${commandOptions}")
        try {
            getPipelineConfiguration().getScriptWrapper().executeCommandWithException(commandLines.join(" \n"))
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

    String getProject() {
        return project
    }

    void setProject(final String project) {
        this.project = project
    }
}
