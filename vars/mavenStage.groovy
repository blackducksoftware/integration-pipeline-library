#!/usr/bin/groovy

def call(String stageName = 'Maven Build', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String mavenToolName = config.get('toolName', 'maven-3')
    String mavenBuildCommand = config.get('buildCommand', '-U clean package deploy')

    String mvnHome = tool "${mavenToolName}"
    stage(stageName) {
        sh "${mvnHome}/bin/mvn ${mavenBuildCommand}"
    }
}
