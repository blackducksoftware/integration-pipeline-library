#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String buildCommandVar = config.buildCommand
    String gradleExeVar = config.gradleExe

    def additionalParameters = config.get('additionalParameters', null)
    def params = new ArrayList()
    if (additionalParameters) {
        params = new ArrayList(additionalParameters)
    }
    params.add(booleanParam(defaultValue: false, description: 'If you do NOT want the build to change the version of the common-gradle-plugin, set this to true', name: 'DO_NOT_CHANGE_CGP'))

    config.additionalParameters = params
    config.initialStage = {}

    return integrationPipeline('gradle', gradleExeVar, {
        gradleStage {
            buildCommand = buildCommandVar
        }

    }, config)
}
