#!/usr/bin/groovy

def call(String stageName = 'Git', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def gitTool = config.get('git', 'Default')
    def changelog = config.get('changelog', false)
    def poll = config.get('poll', false)
    def relativeTargetDir = config.get('relativeTargetDir', './')

    stage(stageName) {
        checkout changelog: changelog, poll: poll,
                scm: [$class    : 'GitSCM', branches: [[name: config.branch]], doGenerateSubmoduleConfigurations: false,
                      extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: relativeTargetDir]], gitTool: gitTool, submoduleCfg: [], userRemoteConfigs: [[url: config.url]]]

    }
}
