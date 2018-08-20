#!/usr/bin/groovy

def call(String stageName = 'Archive artifacts', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def artifactPatterns = config.get('pattern', '**/*.jar')
    stage(stageName) {
        archiveArtifacts "${artifactPatterns}"
    }
}
