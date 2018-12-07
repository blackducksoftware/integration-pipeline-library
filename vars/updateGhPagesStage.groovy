#!/usr/bin/groovy

def call(String stageName = 'Update gh-pages', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String originalBranch = config.originalBranch ?: "${BRANCH}"
    if (null == originalBranch || originalBranch.trim().length() == 0) {
        originalBranch = 'master'
    } else if (originalBranch.contains('/')) {
        originalBranch = originalBranch.substring(originalBranch.lastIndexOf('/') + 1).trim()
    }

    String targetDir = config.targetDir
    if (null == targetDir || targetDir.trim().length() == 0) {
        targetDir = originalBranch
    }

    String branch = config.branch ?: 'gh-pages'
    String ghPageTargetDir = config.ghPageTargetDir
    if (null == ghPageTargetDir || ghPageTargetDir.trim().length() == 0) {
        if (branch.contains('/')) {
            ghPageTargetDir = branch.substring(branch.lastIndexOf('/') + 1)
        } else {
            ghPageTargetDir = branch
        }
    }

    String directoryToRunIn = "${WORKSPACE}/${ghPageTargetDir}"

    List<String> filesToUpdate = config.filesToUpdate

    List<String> filePathsToUpdate = []

    String directory = "${WORKSPACE}/${targetDir}"
    dir(directory) {
        for (String fileToUpdate : filesToUpdate) {
            filePathsToUpdate.add("${directory}/${fileToUpdate}")
            println "File to update = ${directory}/${fileToUpdate}"
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
                sh 'git add --all'
                sh 'git commit -am "Committing the latest update-site contents to gh-pages branch."'
                sh "git push origin ${branch}"
            }
        }
    }
}
