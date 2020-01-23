#!/usr/bin/groovy
import com.synopsys.integration.ConfigUtils
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapperImpl
import com.synopsys.integration.pipeline.logging.DefaultPipelineLogger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.utilities.GradleUtils

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String buildCommandVar = config.buildCommand
    String gradleExeVar = config.gradleExe

    String exe = config.exe

    ConfigUtils configUtils = new ConfigUtils(config)
    boolean runReleaseVar
    try {
        runReleaseVar = configUtils.get('runRelease', Boolean.valueOf("${RUN_RELEASE}"))
    } catch (MissingPropertyException e) {
        runReleaseVar = false
    }

    boolean dontChangeCGP
    try {
        dontChangeCGP = Boolean.valueOf("${DO_NOT_CHANGE_CGP}")
    } catch (MissingPropertyException e) {
        dontChangeCGP = false
    }

    JenkinsScriptWrapper jenkinsScriptWrapper = new JenkinsScriptWrapperImpl(this)
    PipelineLogger pipelineLogger = new DefaultPipelineLogger(jenkinsScriptWrapper)

    GradleUtils gradleUtils = new GradleUtils(pipelineLogger, jenkinsScriptWrapper, exe)
    Closure initialBody = {
        if (!dontChangeCGP) {
            gradleUtils.updateCommonGradlePluginVersion(runReleaseVar)
        }
    }

    def additionalParameters = config.get('additionalParameters', null)
    def params = new ArrayList()
    if (additionalParameters) {
        params = new ArrayList(additionalParameters)
    }
    params.add(booleanParam(defaultValue: false, description: 'If you do NOT want the build to change the version of the common-gradle-plugin, set this to true', name: 'DO_NOT_CHANGE_CGP'))

    config.additionalParameters = params
    config.initialStage = initialBody

    return integrationPipeline('gradle', gradleExeVar, {
        gradleStage {
            buildCommand = buildCommandVar
        }

    }, config)
}
