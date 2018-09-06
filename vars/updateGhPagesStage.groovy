#!/usr/bin/groovy

def call(String stageName = 'Update gh-pages', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String ghPageTargetDir = config.ghPageTargetDir ?: 'gh-pages'

    String directoryToRunIn = "${WORKSPACE}/${ghPageTargetDir}"

    List<String> filesToUpdate = config.filesToUpdate

    List<String> filePathsToUpdate = []
    dir("${WORKSPACE}") {
        for (String fileToUpdate : filesToUpdate) {
            filePathsToUpdate.add("${WORKSPACE}/${filesToUpdate}")
            println "File to update = ${WORKSPACE}/${filesToUpdate}"
        }
    }


    stage(stageName) {
        // add the latest commit id to gh-pages to indicate a functionally new build (the next shell script will commit it)
        sh 'git rev-parse HEAD > ../latest-commit-id.txt'
        dir(directoryToRunIn) {
            String checkedInCommitId = readFile file: "latest-commit-id.txt"
            String currentCommitId = readFile file: "../latest-commit-id.txt"
            println "Checked in commit Id ${checkedInCommitId}"
            println "Current commit Id ${currentCommitId}"
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
