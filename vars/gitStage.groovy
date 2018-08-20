#!/usr/bin/groovy

def call(String stageName = 'Git', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def gitURL = config.url
    def gitBranch = config.branch
    def changelog = config.get('changelog', false)
    def poll = config.get('poll', false)

    stage(stageName) {
        git branch: "${gitBranch}", changelog: changelog, poll: poll, url: "${gitURL}"
    }
}
