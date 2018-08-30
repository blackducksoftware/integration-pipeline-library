#!/usr/bin/groovy

def call(String stageName = 'Update gh-pages', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String url = config.url
    String branch = config.branch ?: 'gh-pages'
    String gitTool = config.get('git', 'Default')
    String ghPageTargetDir = config.ghPageTargetDir ?: 'gh-pages'

    String workspace = "${WORKSPACE}"
    String directoryToRunIn = "${WORKSPACE}/${ghPageTargetDir}"

    String originalDirectory = sh(script: "pwd", returnStdout: true).trim()

    List<String> filesToUpdate = config.filesToUpdate

    List<String> filePathsToUpdate = []
    for (String fileToUpdate : filesToUpdate) {
        filePathsToUpdate.add(originalDirectory + '/' + fileToUpdate)
        println "File to update = ${originalDirectory + '/' + fileToUpdate}"
    }


    stage(stageName) {
        dir(workspace) {
            checkout changelog: false, poll: false,
                    scm: [$class    : 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false,
                          extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: ghPageTargetDir]], gitTool: gitTool, submoduleCfg: [], userRemoteConfigs: [[url: url]]]
        }
        dir(directoryToRunIn) {
            // Need to do this because Jenkins checks out a detached HEAD
            sh "git checkout ${branch}"
            // Do a hard reset in order to clear out any local changes/commits
            sh "git reset --hard origin/${branch}"

            // add the latest commit id to gh-pages to indicate a functionally new build (the next shell script will commit it)
            sh 'git rev-parse HEAD > ../latest-commit-id.txt'

            String checkedInCommitId = readFile file: "latest-commit-id.txt"
            String currentCommitId = readFile file: "../latest-commit-id.txt"
            if (checkedInCommitId == currentCommitId) {
                println "No commits since last build so no need to make any further changes."
            } else {
                sh 'cp ../latest-commit-id.txt .'
                for (String fileToUpdate : filePathsToUpdate) {
                    sh "cp ${fileToUpdate} ."
                }
                sh 'git commit -am "Committing the latest update-site contents to gh-pages branch."'
                sh "git push origin ${branch}"
            }
        }
    }
}
