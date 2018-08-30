#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String nodeNameVar = config.nodeName
    String mavenExeVar = config.mavenExe

    String buildCommandVar = config.buildCommand

    String mavenToolNameVar = config.toolName ?: 'maven-3'
    integrationNode(nodeNameVar) {
        String mvnHome = tool "${mavenToolNameVar}"
        if (null == mavenExeVar || mavenExeVar.trim().length() == 0) {
            mavenExeVar = "${mvnHome}/bin/mvn"
        }
    }

    integrationPipeline('maven', mavenExeVar, {
        mavenStage {
            toolName = mavenToolNameVar
            buildCommand = buildCommandVar
        }
    }, body)
}
