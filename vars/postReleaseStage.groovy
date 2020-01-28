#!/usr/bin/groovy
import com.synopsys.integration.ConfigUtils
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapperImpl
import com.synopsys.integration.pipeline.logging.DefaultPipelineLogger
import com.synopsys.integration.pipeline.logging.LogLevel
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.utilities.ProjectUtils


def call(String stageName = 'Post-Release Stage', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String buildTool = config.buildTool
    String exe = config.exe

    String branch = config.branch

    stage(stageName) {
        JenkinsScriptWrapper jenkinsScriptWrapper = new JenkinsScriptWrapperImpl(this)
        PipelineLogger pipelineLogger = new DefaultPipelineLogger(jenkinsScriptWrapper)
        pipelineLogger.setLogLevel(LogLevel.DEBUG)

        ConfigUtils configUtils = new ConfigUtils(config)
        boolean runReleaseVar
        try {
            runReleaseVar = configUtils.get('runRelease', Boolean.valueOf("${RUN_RELEASE}"))
        } catch (MissingPropertyException e) {
            runReleaseVar = false
        }

        boolean runQABuildVar
        try {
            runQABuildVar = configUtils.get('runQABuild', Boolean.valueOf("${RUN_QA_BUILD}"))
        } catch (MissingPropertyException e) {
            runQABuildVar = false
        }


        ProjectUtils projectUtils = new ProjectUtils(pipelineLogger, jenkinsScriptWrapper)
        projectUtils.initialize(buildTool, exe)
        def newVersion = projectUtils.increaseSemver(runReleaseVar, runQABuildVar)
        if (newVersion.contains('-SNAPSHOT')) {
            println "Using the next snapshot post release. ${newVersion}"
            def commitMessage = "Using the next snapshot post release ${newVersion}"
            sh "git commit -a -m \"${commitMessage}\""
            sh "git push origin ${branch}"
        }
    }
}
