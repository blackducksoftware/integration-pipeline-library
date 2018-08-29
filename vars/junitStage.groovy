#!/usr/bin/groovy

def call(String stageName = 'Publish Junit test report', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String xmlPattern = config.xmlPattern ?: 'build/**/*.xml'

    stage(stageName) {
        step([$class: 'JUnitResultArchiver', testResults: "${xmlPattern}"])
    }
}
