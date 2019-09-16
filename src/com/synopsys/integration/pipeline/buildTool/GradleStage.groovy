package com.synopsys.integration.pipeline.buildTool

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage

class GradleStage extends Stage {
    public static final String DEFAULT_GRADLE_EXE = './gradlew'
    public static final String DEFAULT_GRADLE_OPTIONS = 'clean build'

    private final PipelineLogger pipelineLogger
    private String gradleExe = DEFAULT_GRADLE_EXE
    private String gradleOptions = DEFAULT_GRADLE_OPTIONS


    GradleStage(String stageName, PipelineLogger pipelineLogger) {
        super(stageName)
        this.pipelineLogger = pipelineLogger
    }


    @Override
    void stageExecution() throws PipelineException, Exception {
        pipelineLogger.info("running gradle ${gradleExe} ${gradleOptions}")
        getScriptWrapper().executeCommandWithException("${gradleExe} ${gradleOptions}")
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
