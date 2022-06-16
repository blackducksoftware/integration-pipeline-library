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
    private String owner
    private String repo
    private String tag_name
    private String target_commitish
    private String name2
    private String body

    GithubReleaseStage2 (PipelineConfiguration pipelineConfiguration, String stageName) {
        super(pipelineConfiguration, stageName)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        try {
            setOwner("github848")
            setRepo("REPO")
            settagname("v1.0.5")
            setName("Bob")
            setBody("Testing from repository")
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
        return name2
    }

    void setName(String nam) {
        name2= nam
    }

    String getBody() {
        return body
    }

    void setBody(String bod) {
        body = bod
    }
}

