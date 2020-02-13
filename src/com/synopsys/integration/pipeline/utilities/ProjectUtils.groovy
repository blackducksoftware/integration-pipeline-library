package com.synopsys.integration.pipeline.utilities

import com.cloudbees.groovy.cps.NonCPS
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

    @NonCPS
    public void initialize(String tool, String exe) {
        Objects.requireNonNull(tool, "You must provide a build tool. tool = '${tool}'")
        Objects.requireNonNull(exe, "You must provide an exe for the build tool. exe = '${exe}'")
        logger.info("Using tool ${tool}")

        ToolUtils newToolUtils = null
        if (tool.equalsIgnoreCase(SimplePipeline.MAVEN_BUILD_TOOL)) {
            newToolUtils = new MavenUtils(logger, jenkinsScriptWrapper, exe)
        } else if (tool.equalsIgnoreCase(SimplePipeline.GRADLE_BUILD_TOOL)) {
            newToolUtils = new GradleUtils(logger, jenkinsScriptWrapper, exe)
        }
        toolUtils = Optional.ofNullable(newToolUtils).orElseThrow(new PipelineException("Did not recognize the tool '${tool}'"))
        logger.info("Initializing tool ${tool}")
        toolUtils.initialize()
    }

    @Deprecated
    public String getProjectVersionProcess() {
        logger.warn("DO NO USE THE getProjectVersionProcess() method anymore. Please use getProjectVersion() instead.")
        return getProjectVersion()
    }

    @NonCPS
    public String getProjectVersion() {
        return Optional.ofNullable(toolUtils)
                .map({ toolUtils -> toolUtils.getProjectVersion() })
                .orElse("")
    }

    @NonCPS
    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        return Optional.ofNullable(toolUtils)
                .map({ toolUtils -> toolUtils.checkForSnapshotDependencies(checkAllDependencies) })
                .orElse(false)
    }

    @NonCPS
    public String updateVersionForRelease(boolean runRelease, boolean runQARelease) {
        return Optional.ofNullable(toolUtils)
                .map({ toolUtils -> updateVersionForRelease(runRelease, runQARelease) })
                .orElse("")
    }

    @NonCPS
    public String increaseSemver(boolean runRelease, boolean runQARelease) {
        return Optional.ofNullable(toolUtils)
                .map({ toolUtils -> increaseSemver(runRelease, runQARelease) })
                .orElse("")
    }
}
