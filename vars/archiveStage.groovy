#!/usr/bin/groovy

def call(String stageName = 'Archive artifacts', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String artifactPatterns = config.get('patterns', '**/*.jar')
    stage(stageName) {
        archiveArtifacts "${artifactPatterns}"
    }
}
