#!/usr/bin/groovy

def call(String stageName = 'Gradle Build', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String gradleExe = config.get('gradleExe', './gradlew')
    String gradleBuildCommand = config.get('buildCommand', 'clean build --refresh-dependencies')

    stage(stageName) {
        sh "${gradleExe} ${gradleBuildCommand}"
    }
}
