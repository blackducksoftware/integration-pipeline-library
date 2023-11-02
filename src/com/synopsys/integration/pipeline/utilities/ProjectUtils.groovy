package com.synopsys.integration.pipeline.utilities


import com.synopsys.integration.pipeline.SimplePipeline
import com.synopsys.integration.pipeline.exception.PipelineException
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
        Objects.requireNonNull(tool, "You must provide a build tool. tool = '${tool}'")
        logger.info("Using tool ${tool}")

        ToolUtils newToolUtils = null
        if (tool.equalsIgnoreCase(SimplePipeline.MAVEN_BUILD_TOOL)) {
            newToolUtils = new MavenUtils(logger, jenkinsScriptWrapper, exe)
        } else if (tool.equalsIgnoreCase(SimplePipeline.GRADLE_BUILD_TOOL)) {
            newToolUtils = new GradleUtils(logger, jenkinsScriptWrapper, exe)
        }
        if (null == newToolUtils) {
            throw new PipelineException("Did not recognize the tool '${tool}'")
        }
        toolUtils = newToolUtils
        logger.info("Initializing tool ${tool}")
        toolUtils.initialize()
    }

    @Deprecated
    public String getProjectVersionProcess() {
        logger.warn("DO NO USE THE getProjectVersionProcess() method anymore. Please use getProjectVersion() instead.")
        return getProjectVersion()
    }

    public String getProjectVersion() {
        String version = ""
        if (null != toolUtils) {
            version = toolUtils.getProjectVersion()
        }
        return version
    }

    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        boolean hasSnapshotDependencies = false
        if (null != toolUtils) {
            hasSnapshotDependencies = toolUtils.checkForSnapshotDependencies(checkAllDependencies)
        }
        return hasSnapshotDependencies
    }

    public String updateVersionForRelease(boolean runRelease, boolean runQARelease) {
        String version = ""
        if (null != toolUtils) {
            version = toolUtils.updateVersionForRelease(runRelease, runQARelease)
        }
        return version
    }

    public String updateVersionForRelease(boolean runRelease, boolean runQARelease, String versionUpdateCommand) {
        String version = ""
        if (null != toolUtils) {
            version = toolUtils.updateVersionForRelease(runRelease, runQARelease, versionUpdateCommand)
        }
        return version
    }

    public String increaseSemver(boolean runRelease, boolean runQARelease) {
        String version = ""
        if (null != toolUtils) {
            version = toolUtils.increaseSemver(runRelease, runQARelease)
        }
        return version
    }
}
