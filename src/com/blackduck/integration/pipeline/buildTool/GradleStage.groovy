package com.blackduck.integration.pipeline.buildTool


import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.Stage

class GradleStage extends Stage {
    public static final String DEFAULT_GRADLE_EXE = './gradlew'
    public static final String DEFAULT_GRADLE_OPTIONS = 'clean build'

    private String gradleExe = DEFAULT_GRADLE_EXE
    private String gradleOptions = DEFAULT_GRADLE_OPTIONS

    GradleStage(PipelineConfiguration pipelineConfiguration, String stageName) {
        super(pipelineConfiguration, stageName)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getPipelineConfiguration().getLogger().info("running gradle ${gradleExe} ${gradleOptions}")
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${gradleExe} ${gradleOptions}")
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
