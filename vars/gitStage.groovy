#!/usr/bin/groovy

def call(String stageName = 'Git', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String url = config.url
    String branch = config.get('branch', "${BRANCH}")
    if (null == branch || branch.trim().length() == 0) {
        branch = 'master'
    }

    String gitTool = config.get('git', 'Default')
    boolean changelog = config.get('changelog', false)
    boolean poll = config.get('poll', false)
    String relativeTargetDir = config.get('relativeTargetDir', './')

    stage(stageName) {
        checkout changelog: changelog, poll: poll,
                scm: [$class    : 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false,
                      extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: relativeTargetDir]], gitTool: gitTool, submoduleCfg: [], userRemoteConfigs: [[url: url]]]
        sh "git checkout ${branch}"
        sh "git reset --hard origin/${branch}"
    }
}
