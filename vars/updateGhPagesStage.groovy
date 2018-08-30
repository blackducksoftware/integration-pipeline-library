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

    String originalDirectory = sh(script: "pwd", returnStdout: true)

    List<String> filesToUpdate = config.filesToUpdate

    List<String> filePathsToUpdate = []
    for (String fileToUpdate : filesToUpdate) {
        filePathsToUpdate.add(originalDirectory + '/' + filesToUpdate)
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
                // sh "git push origin ${branch}"
            }
        }
    }
}


//            // add the latest commit id to gh-pages to indicate a functionally new build (the next shell script will commit it)
//            git rev-parse HEAD > ../latest-commit-id.txt
//            cd ../gh-pages
//
//            // Must do that due to Jenkins being weird during checkout.
//                    git checkout gh-pages
//            git pull origin gh-pages
//
//            if diff latest-commit-id.txt ../latest-commit-id.txt >/dev/null ; then
//            echo "No commits since last build so no need to make any further changes."
//            else
//            cp ../latest-commit-id.txt .
//            cp ../master/hub-detect/build/hub-detect.sh .
//            cp ../master/hub-detect/build/hub-detect.ps1 .
//
//            git add --all && git commit -am "Committing the latest update-site contents to gh-pages branch."
//            git push origin gh-pages
//            fi
