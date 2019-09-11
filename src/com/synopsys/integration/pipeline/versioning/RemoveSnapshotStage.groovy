package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.ProjectUtils
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.exception.PrepareForReleaseException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage

class RemoveSnapshotStage extends Stage {
    private final JenkinsScriptWrapper scriptWrapper
    private final PipelineLogger logger
    private final String buildTool
    private final String exe

    private final String branch

    private boolean checkAllDependencies = false
    private String gitToolName = GitStage.DEFAULT_GIT_TOOL

    RemoveSnapshotStage(JenkinsScriptWrapper scriptWrapper, PipelineLogger logger, String stageName, String buildTool, String exe, String branch) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
        this.logger = logger
        this.buildTool = buildTool
        this.exe = exe
        this.branch = branch
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        ProjectUtils projectUtils = new ProjectUtils()
        projectUtils.initialize(scriptWrapper.getScript(), buildTool, exe)
        boolean hasSnapshotDependencies = projectUtils.checkForSnapshotDependencies(checkAllDependencies)
        if (hasSnapshotDependencies) {
            String errorMessage = "Failing release preparation because of ${buildTool} SNAPSHOT dependencies"
            throw new PrepareForReleaseException(errorMessage)
        }
        def version = projectUtils.getProjectVersion()
        if (version.contains('-SNAPSHOT')) {
            logger.info("Removing SNAPSHOT from the Project Version")
            String newVersion = projectUtils.removeSnapshotFromProjectVersion()
            logger.debug("Commiting the release ${newVersion}")
            String gitPath = scriptWrapper.tool(gitToolName)

            scriptWrapper.executeCommand("${gitPath} commit -am \"Release ${newVersion}\"")

            String remote = 'origin'
            String branchName = branch

            if (branch.contains('/')) {
                String[] pieces = branch.split('/')
                if (pieces.length != 2) {
                    throw new IllegalArgumentException('The branch provided was not in a valid format.')
                }
                remote = pieces[0]
                branchName = pieces[1]
            }

            scriptWrapper.executeCommand("${gitPath} push ${remote} ${branchName}")
            logger.debug("Pushing release to branch ${branch}")
        }
        if (version.contains('-SNAPSHOT')) {
            scriptWrapper.env().GITHUB_RELEASE_VERSION = version
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

    boolean getCheckAllDependencies() {
        return checkAllDependencies
    }

    void setCheckAllDependencies(final boolean checkAllDependencies) {
        this.checkAllDependencies = checkAllDependencies
    }

    String getGitToolName() {
        return gitToolName
    }

    void setGitToolName(final String gitToolName) {
        this.gitToolName = gitToolName
    }

}
