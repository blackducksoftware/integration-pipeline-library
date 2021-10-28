package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.exception.PrepareForReleaseException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.pipeline.utilities.ProjectUtils
import com.synopsys.integration.utilities.GithubBranchParser

abstract class SnapshotStage extends Stage {
    public final boolean runRelease
    public final boolean runQARelease
    public final String buildTool
    public final String exe
    public final String branch
    public final String url
    public final String githubCredentialsId

    private String loggingFlag
    private String version
    private String newVersion
    private String gitToolName = GitStage.DEFAULT_GIT_TOOL
    private boolean shouldCheckDependencies = false
    private boolean checkAllDependencies = false

    SnapshotStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, boolean runQARelease, String buildTool, String exe, String branch, String url, String githubCredentialsId, String loggingFlag, boolean shouldCheckDependencies) {
        super(pipelineConfiguration, stageName)
        this.runRelease = runRelease
        this.runQARelease = runQARelease
        this.buildTool = buildTool
        this.exe = exe
        this.branch = branch
        this.url = url
        this.githubCredentialsId = githubCredentialsId
        this.loggingFlag = loggingFlag
        this.shouldCheckDependencies = shouldCheckDependencies
    }

    abstract void generateAndSetNewVersion(ProjectUtils projectUtils)

    abstract String getCommitMessage()

    @Override
    void stageExecution() throws PipelineException, Exception {
        if (!runRelease && !runQARelease) {
            getPipelineConfiguration().getLogger().info("Skipping the ${this.getClass().getSimpleName()} because this is not a release.")
            return
        }

        ProjectUtils projectUtils = new ProjectUtils(getPipelineConfiguration().getLogger(), getPipelineConfiguration().getScriptWrapper())
        projectUtils.initialize(buildTool, exe)

//        if (shouldCheckDependencies) {
//            boolean hasSnapshotDependencies = projectUtils.checkForSnapshotDependencies(checkAllDependencies)
//            if (hasSnapshotDependencies) {
//                String errorMessage = "Failing release preparation because of ${buildTool} SNAPSHOT dependencies"
//                throw new PrepareForReleaseException(errorMessage)
//            }
//        }

        version = projectUtils.getProjectVersion()
        getPipelineConfiguration().getLogger().info("${loggingFlag}:: updating the Project version '${version}'. Release: ${runRelease}, QA release: ${runQARelease}")
        generateAndSetNewVersion(projectUtils)

        if (!newVersion.equals(version)) {
            commitAndPushToRepo()
        }
    }

    void checkSnapshotDependencies(ProjectUtils projectUtils) {
        if (getShouldCheckDependencies()) {
            boolean hasSnapshotDependencies = projectUtils.checkForSnapshotDependencies(checkAllDependencies)
            if (hasSnapshotDependencies) {
                String errorMessage = "Failing release preparation because of ${buildTool} SNAPSHOT dependencies"
                throw new PrepareForReleaseException(errorMessage)
            }
        }
    }

    void commitAndPushToRepo() {
        getPipelineConfiguration().getLogger().info("${loggingFlag}:: Committing new version: ${newVersion}")
        String gitPath = getPipelineConfiguration().getScriptWrapper().tool(gitToolName)
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${gitPath} commit -a -m \"${getCommitMessage()}\"")

        getPipelineConfiguration().getLogger().debug("${loggingFlag}:: Pushing new version to branch ${branch}")
        if (url.startsWith(GitStage.GITHUB_HTTPS)) {
            getPipelineConfiguration().getScriptWrapper().executeGitPushToGithub(pipelineConfiguration, url, githubCredentialsId, gitPath)
        } else {
            GithubBranchParser githubBranchParser = new GithubBranchParser()
            GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)
            getPipelineConfiguration().getScriptWrapper().executeCommand("${gitPath} push ${githubBranchModel.getRemote()} ${githubBranchModel.getBranchName()}")
        }
    }

    String getBuildTool() {
        return buildTool
    }

    String getExe() {
        return exe
    }

    String getBranch() {
        return branch
    }

    String getUrl() {
        return url
    }

    String getGithubCredentialsId() {
        return githubCredentialsId
    }

    String setLoggingFlag(String loggingFlag) {
        this.loggingFlag = loggingFlag
    }

    String getLoggingFlag() {
        return loggingFlag
    }

    void setNewVersion(String newVersion) {
        this.newVersion = newVersion
    }

    String getNewVersion() {
        return newVersion
    }

    String getGitToolName() {
        return gitToolName
    }

    void setGitToolName(String gitToolName) {
        this.gitToolName = gitToolName
    }

    boolean getShouldCheckDependencies() {
        return shouldCheckDependencies
    }

    void setShouldCheckDependencies(boolean shouldCheckDependencies) {
        this.shouldCheckDependencies = shouldCheckDependencies
    }

    boolean getCheckAllDependencies() {
        return checkAllDependencies
    }

    void setCheckAllDependencies(boolean checkAllDependencies) {
        this.checkAllDependencies = checkAllDependencies
    }

}
