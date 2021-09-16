package com.synopsys.integration.pipeline.utilities

import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger
import org.apache.commons.lang3.StringUtils

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
        if (StringUtils.isBlank(exe)) {
            this.exe = './gradlew'
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
        String version = jenkinsScriptWrapper.executeCommand("${exe} properties -q | grep '^version: '", true)
        return version.substring(version.indexOf(':') + 1).trim()
    }

    @Override
    public String updateVersionForRelease(boolean runRelease, boolean runQARelease) {
        if (runRelease) {
            jenkinsScriptWrapper.executeCommandWithException("${exe} jaloja ")
        } else if (runQARelease) {
            jenkinsScriptWrapper.executeCommandWithException("${exe} qaJaloja ")
        }
        return getProjectVersion()
    }

    @Override
    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        String command = "${exe} dependencies -q"
        if (!checkAllDependencies) {
            command = "${command} --configuration compileClassPath"
        }
        String dependencyText = jenkinsScriptWrapper.executeCommand(command, true)
        logger.info("Gradle dependencies")
        logger.info("${dependencyText}")
        return dependencyText.contains('-SNAPSHOT')
    }

    @Override
    public String increaseSemver(boolean runRelease, boolean runQARelease) {
        jenkinsScriptWrapper.executeCommandWithException("${exe} snapshotJaloja")
        return getProjectVersion()
    }
}
