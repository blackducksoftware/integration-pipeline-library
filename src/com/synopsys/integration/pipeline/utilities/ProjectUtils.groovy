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

    public String getProjectVersion() {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.getProjectVersionProcess()
        }
        return version
    }

    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        if (null != toolUtils) {
            return toolUtils.checkForSnapshotDependencies(checkAllDependencies)
        }
        return false
    }

    public String removeSnapshotFromProjectVersion() {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.removeSnapshotFromProjectVersion()
        }
        return version
    }

    public String increaseSemver() {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.increaseSemver()
        }
        return version
    }
}
