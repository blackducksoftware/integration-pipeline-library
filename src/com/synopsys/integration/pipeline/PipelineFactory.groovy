package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.buildTool.gradle.GradleStage
import com.synopsys.integration.pipeline.buildTool.maven.MavenStage
import com.synopsys.integration.pipeline.email.EmailPipelineWrapper
import com.synopsys.integration.pipeline.jenkins.ScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger

class PipelineFactory {
    private final PipelineLogger pipelineLogger
    private final ScriptWrapper scriptWrapper

    public PipelineFactory(Object script) {
        scriptWrapper = new ScriptWrapper(script)

        script.println "script ${script.getClass()}"
        script.println "env ${script.env.getClass()}"
        script.println "current build ${script.currentBuild.getClass()}"
        script.println "emailext ${script.emailext.getClass()}"
        script.println "sh ${script.sh.getClass()}"
        script.println "tool ${script.tool.getClass()}"

        this.pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
    }

    public EmailPipelineWrapper createEmailPipelineWrapper(String recipientList) {
        return new EmailPipelineWrapper(recipientList, scriptWrapper.currentBuild, scriptWrapper.emailext, scriptWrapper.env.JOB_NAME, scriptWrapper.env.BUILD_NUMBER, scriptWrapper.env.BUILD_URL)
    }

    public EmailPipelineWrapper createEmailPipelineWrapper(String wrapperName, String recipientList) {
        return new EmailPipelineWrapper(wrapperName, recipientList, scriptWrapper.currentBuild, scriptWrapper.emailext, scriptWrapper.env.JOB_NAME, scriptWrapper.env.BUILD_NUMBER, scriptWrapper.env.BUILD_URL)
    }

    public GradleStage createGradleStage(String gradleExe, String gradleOptions) {
        return new GradleStage(script.sh, gradleExe, gradleOptions)
    }

    public MavenStage createMavenStage(String mavenExe, String mavenOptions) {
        return new MavenStage(script.sh, script.tool, mavenExe, mavenOptions)
    }

}
