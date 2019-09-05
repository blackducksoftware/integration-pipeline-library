package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.buildTool.gradle.GradleStage
import com.synopsys.integration.pipeline.buildTool.maven.MavenStage
import com.synopsys.integration.pipeline.email.EmailPipelineWrapper
import com.synopsys.integration.pipeline.jenkins.ScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger

class PipelineFactory implements Serializable {
    private final PipelineLogger pipelineLogger
    private final ScriptWrapper scriptWrapper

    public PipelineFactory(Object script) {
        scriptWrapper = new ScriptWrapper(script)
        this.pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
    }

    public EmailPipelineWrapper createEmailPipelineWrapper(String recipientList) {
        return new EmailPipelineWrapper(pipelineLogger, scriptWrapper, recipientList, scriptWrapper.env().JOB_NAME, scriptWrapper.env().BUILD_NUMBER, scriptWrapper.env().BUILD_URL)
    }

    public EmailPipelineWrapper createEmailPipelineWrapper(String wrapperName, String recipientList) {
        return new EmailPipelineWrapper(pipelineLogger, scriptWrapper, wrapperName, recipientList, scriptWrapper.env().JOB_NAME, scriptWrapper.env().BUILD_NUMBER, scriptWrapper.env().BUILD_URL)
    }

    public GradleStage createGradleStage(String gradleExe, String gradleOptions) {
        return new GradleStage(scriptWrapper, gradleExe, gradleOptions)
    }

    public MavenStage createMavenStage(String mavenExe, String mavenOptions) {
        return new MavenStage(scriptWrapper, script.tool, mavenExe, mavenOptions)
    }

}
