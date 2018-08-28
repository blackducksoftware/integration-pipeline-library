#!/usr/bin/groovy

def call(String stageName = 'Git', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def url = config.url
    def branch = config.get('branch', "${BRANCH}")
    if (null == branch || branch.trim().length() == 0) {
        branch = 'master'
    }

    def gitTool = config.get('git', 'Default')
    def changelog = config.get('changelog', false)
    def poll = config.get('poll', false)
    def relativeTargetDir = config.get('relativeTargetDir', './')

    stage(stageName) {
        checkout changelog: changelog, poll: poll,
                scm: [$class    : 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false,
                      extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: relativeTargetDir]], gitTool: gitTool, submoduleCfg: [], userRemoteConfigs: [[url: url]]]
        sh "git checkout ${branch}"
    }
}
