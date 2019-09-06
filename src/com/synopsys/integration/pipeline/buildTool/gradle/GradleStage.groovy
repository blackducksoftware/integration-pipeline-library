package com.synopsys.integration.pipeline.buildTool.gradle

import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage

class GradleStage extends Stage {
    public static final String DEFAULT_GRADLE_EXE = './gradlew'
    public static final String DEFAULT_GRADLE_OPTIONS = 'clean build --refresh-dependencies'

    private final JenkinsScriptWrapper scriptWrapper
    private String gradleExe
    private String gradleOptions


    public GradleStage(JenkinsScriptWrapper scriptWrapper, String stageName, String gradleExe, String gradleOptions) {
        super(stageName)
        this.scriptWrapper = scriptWrapper

        if (null != gradleExe && gradleExe.trim().length() > 0) {
            this.gradleExe = gradleExe
        } else {
            this.gradleExe = DEFAULT_GRADLE_EXE
        }

        if (null != gradleOptions && gradleOptions.trim().length() > 0) {
            this.gradleOptions = gradleOptions
        } else {
            this.gradleOptions = DEFAULT_GRADLE_OPTIONS
        }
    }


    @Override
    void stageExecution() {
        PipelineLogger pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
        pipelineLogger.info("running gradle ${gradleExe} ${gradleOptions}")
        try {
            Object result = scriptWrapper.executeCommand("${gradleExe} ${gradleOptions}")
            pipelineLogger.info("gradle result ${result}")
        } catch (Exception e) {
            pipelineLogger.error("gradle exception ${e.getMessage()}")
        }
    }

    String getGradleExe() {
        return gradleExe
    }

    void setGradleExe(final String gradleExe) {
        this.gradleExe = gradleExe
    }

    String getGradleOptions() {
        return gradleOptions
    }

    void setGradleOptions(final String gradleOptions) {
        this.gradleOptions = gradleOptions
    }
}
