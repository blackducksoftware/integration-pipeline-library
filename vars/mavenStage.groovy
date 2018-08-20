#!/usr/bin/groovy

def call(String stageName = 'Maven Build', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def mavenToolName = config.get('toolName', 'maven-3')
    def mavenBuildCommand = config.get('buildCommand', '-U clean package deploy')

    def mvnHome = tool "${mavenToolName}"

    stage(stageName) {
        sh "${mvnHome}/bin/mvn ${mavenBuildCommand}"
    }
}
