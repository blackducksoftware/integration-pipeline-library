#!/usr/bin/groovy

def call(String stageName = 'GitHub auto release', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String shellURL = config.shellURL ?: 'https://github.com/blackducksoftware/github-auto-release/releases/download/1.0.0/github_auto_release.sh'
    String commitMessage = config.commitMessage ?: "${COMMIT_MESSAGE}"
    if (null == commitMessage || commitMessage.trim().length() == 0) {
        commitMessage = 'Auto Release'
    }
    String options = config.options

    def commandLines = []
    commandLines.add("#!/bin/bash")
    commandLines.add("wget -N \"${shellURL}\"")
    commandLines.add("chmod 777 github_auto_release.sh")
    commandLines.add("./github_auto_release.sh ${options} -m \"${commitMessage}\"")

    stage(stageName) {
        sh commandLines.join(" \n")

    }
}
