#!/usr/bin/groovy

def call(String url) {
    def branch = params.BRANCH
    def localBranch = branch.replace("origin/", "")

    def gitEnvironment = checkout([$class                           : 'GitSCM',
                                   branches                         : [[name: params.BRANCH]],
                                   doGenerateSubmoduleConfigurations: false,
                                   extensions                       : [[$class: 'WipeWorkspace'], [$class: 'LocalBranch', localBranch: localBranch]],
                                   gitTool                          : 'Default',
                                   submoduleCfg                     : [],
                                   userRemoteConfigs                : [[credentialsId: GitStage.DEFAULT_CREDENTIALS_ID, url: url]]])

    gitEnvironment.each { key, value -> env."$key" = value }
}