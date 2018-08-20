#!/usr/bin/groovy

def call(String stageName = 'Git', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def changelog = config.get('changelog', false)
    def poll = config.get('poll', false)

    stage(stageName) {
        git branch: config.branch, changelog: changelog, poll: poll, url: config.url
    }
}
