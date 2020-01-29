package com.synopsys.integration.pipeline.utilities

import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger

public class GradleUtils implements com.synopsys.integration.pipeline.utilities.ToolUtils, Serializable {
    private final PipelineLogger logger
    private final JenkinsScriptWrapper jenkinsScriptWrapper

    private String exe

    public GradleUtils(PipelineLogger logger, JenkinsScriptWrapper jenkinsScriptWrapper, String exe) {
        this.logger = logger
        this.jenkinsScriptWrapper = jenkinsScriptWrapper
        this.exe = exe
    }

    @Override
    public void initialize() {
        if (null == exe || exe.trim().length() > 0) {
            this.exe = './gradlew'
        } else {
            this.exe = exe
        }
    }

    @Deprecated
    @Override
    public String getProjectVersionProcess() {
        logger.warn("DO NO USE THE getProjectVersionProcess() method anymore. Please use getProjectVersion() instead.")
        return getProjectVersion()
    }

    @Override
    public String getProjectVersion() {
        try {
            String version = jenkinsScriptWrapper.executeCommand("${exe} properties -q | grep '^version: '", true)
            return version.substring(version.indexOf(':') + 1).trim()
        } catch (Exception e) {
            logger.error("Failed to run the gradle command to get the Project version ${e.getMessage()}")
        }
        return null
    }

    @Override
    public String updateVersionForRelease(boolean runRelease, boolean runQARelease) {
        if (runRelease) {
            jenkinsScriptWrapper.executeCommand("${exe} jaloja", true)
        } else if (runQARelease) {
            jenkinsScriptWrapper.executeCommand("${exe} qaJaloja", true)
        }
        return getProjectVersion()
    }

    @Override
    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        String command = "${exe} dependencies -q"
        if (!checkAllDependencies) {
            command = "${command} --configuration compile"
        }
        String dependencyText = jenkinsScriptWrapper.executeCommand(command, true)
        logger.info("Gradle dependencies")
        logger.info("${dependencyText}")
        return dependencyText.contains('-SNAPSHOT')
    }

    @Override
    public String increaseSemver(boolean runRelease, boolean runQARelease) {
        jenkinsScriptWrapper.executeCommand("${exe} snapshotJaloja", true)
        return getProjectVersion()
    }
}
