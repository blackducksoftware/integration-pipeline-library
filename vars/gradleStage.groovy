#!/usr/bin/groovy

def call(String stageName = 'Gradle Build', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String gradleExe = config.gradleExe ?: './gradlew'
    String gradleBuildCommand = config.buildCommand ?: 'clean build --refresh-dependencies'

    stage(stageName) {
        sh "${gradleExe} ${gradleBuildCommand}"
    }
}
