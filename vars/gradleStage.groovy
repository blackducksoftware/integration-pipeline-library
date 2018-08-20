#!/usr/bin/groovy

def call(String stageName = 'Gradle Build', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def gradleExe = config.get('gradleExe', './gradlew')
    def gradleBuildCommand = config.get('buildCommand', 'clean build')

    stage(stageName) {
        sh "${gradleExe} ${gradleBuildCommand}"
    }
}
