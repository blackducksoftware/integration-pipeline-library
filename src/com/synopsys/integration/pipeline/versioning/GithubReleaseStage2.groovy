package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.GitHubReleaseException
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.exception.PrepareForReleaseException
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.utilities.GithubBranchParser
import org.apache.commons.lang3.StringUtils
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class GithubReleaseStage2 extends Stage{
    public static final String accept = 'application/vnd.github.v3+json'
    public static final String DEFAULT_GITHUB_OWNER = 'blackducksoftware'
    public static final String DEFAULT_RELEASE_MESSAGE = 'Auto Release'
    public static final String DEFAULT_SCRIPT_URL = 'https://github.com/blackducksoftware/github-auto-release/releases/download/2.1.0/github_auto_release.sh'

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

    private String owner
    private String repo
    private String tag_name
    private String target_commitish
    private String name
    private String body

    GithubReleaseStage2 (PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease) {
        super(pipelineConfiguration, stageName)
        this.runRelease = runRelease
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        setOwner("github848")
        setRepo("REPO")
        settagname("v1.0.5")
        setName("Bob")
        setBody("Testing from repository")

        try {
            println("Hello")
            //getPipelineConfiguration().getScriptWrapper().executeCommandWithException(commandLines.join(" \n"))
        } catch (Exception e) {
            throw new GitHubReleaseException("Failed to run the GitHub auto release ${e.getMessage()}")
        }
    }

    String getOwner() {
        return owner
    }

    void setOwner(String own) {
        owner = own
    }

    String getRepo() {
        return repo
    }

    void setRepo(String rep) {
        repo = rep
    }

    String gettagname() {
        return tag_name
    }

    void settagname(String tag) {
        tag_name = tag
    }

    String gettarget() {
        return target_commitish
    }

    void settarget(String targ) {
        target_commitish = targ
    }

    String getName() {
        return name
    }

    void setName(String nam) {
        name = nam
    }

    String getBody() {
        return body
    }

    void setBody(String bod) {
        body = bod
    }
}

