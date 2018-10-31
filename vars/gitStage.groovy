#!/usr/bin/groovy

def call(String stageName = 'Git', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String url = config.url
    String branch = config.branch

    String gitTool = config.get('git', 'Default')
    boolean changelog = config.get('changelog', false)
    boolean poll = config.get('poll', false)
    String relativeTargetDir = config.relativeTargetDir
    if (null == relativeTargetDir || relativeTargetDir.trim().length() == 0) {
        relativeTargetDir = branch
    }


    def directoryToRunIn = "${WORKSPACE}/${relativeTargetDir}"

    stage(stageName) {
        dir("${WORKSPACE}") {
            checkout changelog: changelog, poll: poll,
                    scm: [$class    : 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false,
                          extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: relativeTargetDir]], gitTool: gitTool, submoduleCfg: [], userRemoteConfigs: [[url: url]]]
            dir(directoryToRunIn) {
                // Need to do this because Jenkins checks out a detached HEAD
                sh "git checkout ${branch}"
                // Do a hard reset in order to clear out any local changes/commits
                sh "git reset --hard ${branch}"
            }
        }
    }
    return directoryToRunIn
}
