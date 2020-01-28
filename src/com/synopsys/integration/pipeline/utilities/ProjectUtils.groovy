package com.synopsys.integration.pipeline.utilities

import com.synopsys.integration.pipeline.SimplePipeline
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger

public class ProjectUtils {
    private final PipelineLogger logger
    private final JenkinsScriptWrapper jenkinsScriptWrapper
    private ToolUtils toolUtils = null

    public ProjectUtils(PipelineLogger logger, JenkinsScriptWrapper jenkinsScriptWrapper) {
        this.logger = logger;
        this.jenkinsScriptWrapper = jenkinsScriptWrapper
    }

    public void initialize(String tool, String exe) {
        logger.info("Using tool ${tool}")
        if (tool.equalsIgnoreCase(SimplePipeline.MAVEN_BUILD_TOOL)) {
            toolUtils = new MavenUtils(logger, jenkinsScriptWrapper, exe)
        } else if (tool.equalsIgnoreCase(SimplePipeline.GRADLE_BUILD_TOOL)) {
            toolUtils = new GradleUtils(logger, jenkinsScriptWrapper, exe)
        }
        if (null != toolUtils) {
            logger.info("Initializing tool ${tool}")
            toolUtils.initialize()
        }
    }

    @Deprecated
    public String getProjectVersionProcess() {
        logger.warn("DO NO USE THE getProjectVersionProcess() method anymore. Please use getProjectVersion() instead.")
        return getProjectVersion()
    }

    public String getProjectVersion() {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.getProjectVersion()
        }
        return version
    }

    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        if (null != toolUtils) {
            return toolUtils.checkForSnapshotDependencies(checkAllDependencies)
        }
        return false
    }

    public String updateVersionForRelease(boolean runRelease, boolean runQARelease) {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.updateVersionForRelease(runRelease, runQARelease)
        }
        return version
    }

    public String increaseSemver(boolean runRelease, boolean runQARelease) {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.increaseSemver(runRelease, runQARelease)
        }
        return version
    }
}
