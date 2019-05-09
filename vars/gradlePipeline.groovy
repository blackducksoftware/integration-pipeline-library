#!/usr/bin/groovy
import com.synopsys.integration.ConfigUtils
import com.synopsys.integration.tools.GradleUtils

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String buildCommandVar = config.buildCommand
    String gradleExeVar = config.gradleExe

    Closure userPreBuildBody = config.preBuild

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

    GradleUtils gradleUtils = new GradleUtils(this, exe)
    Closure fullPreBuild = {
        if (!dontChangeCGP) {
            gradleUtils.updateCommonGradlePluginVersion(runReleaseVar)
        }
        if (null != userPreBuildBody) {
            userPreBuildBody()
        }
    }

    def additionalParameters = config.get('additionalParameters', null)
    def params = new ArrayList()
    if (additionalParameters) {
        params = new ArrayList(additionalParameters)
    }
    params.add(booleanParam(defaultValue: false, description: 'If you do NOT want the build to change the version of the common-gradle-plugin, set this to true', name: 'DO_NOT_CHANGE_CGP'))

    config.additionalParameters = params
    config.preBuild = fullPreBuild

    boolean checkAllDependenciesVar = config.checkAllDependencies

    return integrationPipeline('gradle', gradleExeVar, {
        gradleStage {
            buildCommand = buildCommandVar
        }

    }, body, config)
}
