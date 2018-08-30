#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String buildCommandVar = config.buildCommand

    String gradleExeVar = config.gradleExe

    boolean checkAllDependenciesVar = config.checkAllDependencies

    integrationPipeline('gradle', gradleExeVar, {
        gradleStage {
            buildCommand = buildCommandVar
        }
    }, body)
}
