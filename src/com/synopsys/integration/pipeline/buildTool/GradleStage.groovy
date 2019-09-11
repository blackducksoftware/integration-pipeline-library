package com.synopsys.integration.pipeline.buildTool

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage

class GradleStage extends Stage {
    public static final String DEFAULT_GRADLE_EXE = './gradlew'
    public static final String DEFAULT_GRADLE_OPTIONS = 'clean build'

    private final JenkinsScriptWrapper scriptWrapper
    private String gradleExe = DEFAULT_GRADLE_EXE
    private String gradleOptions = DEFAULT_GRADLE_OPTIONS


    GradleStage(JenkinsScriptWrapper scriptWrapper, String stageName) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
    }


    @Override
    void stageExecution() throws PipelineException, Exception {
        PipelineLogger pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
        pipelineLogger.info("running gradle ${gradleExe} ${gradleOptions}")
        scriptWrapper.executeCommandWithException("${gradleExe} ${gradleOptions}")
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
