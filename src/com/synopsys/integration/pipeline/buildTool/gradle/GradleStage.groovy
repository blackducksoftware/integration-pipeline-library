package com.synopsys.integration.pipeline.buildTool.gradle

import com.synopsys.integration.pipeline.jenkins.ScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage

class GradleStage extends Stage {
    public static final String DEFAULT_GRADLE_EXE = './gradlew'
    public static final String DEFAULT_GRADLE_OPTIONS = 'clean build --refresh-dependencies'

    private final ScriptWrapper scriptWrapper
    private String gradleExe
    private String gradleOptions


    public GradleStage(ScriptWrapper scriptWrapper, String gradleExe, String gradleOptions) {
        this(scriptWrapper, "Gradle Stage", gradleExe, gradleOptions)
    }

    public GradleStage(ScriptWrapper scriptWrapper, String stageName, String gradleExe, String gradleOptions) {
        super(stageName, scriptWrapper)
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
        scriptWrapper.sh("${gradleExe} ${gradleOptions}")
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
