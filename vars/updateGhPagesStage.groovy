#!/usr/bin/groovy

def call(String stageName = 'Update gh-pages', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String publishGitUrlVar = config.gitUrl.trim().replace("https://", "https://${GIT_USERNAME}:${GIT_PASSWORD}@")

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

    String ghPagesDirectory = "${WORKSPACE}/${ghPageTargetDir}/"

    List<String> filesToUpdate = config.filesToUpdate

    Map<String, String> mappedFilesToUpdate = config.mappedFilesToUpdate

    Map<String, String> fileToNewPath = [:]

    String directory = "${WORKSPACE}/${targetDir}"
    dir(directory) {
        if (filesToUpdate != null && !filesToUpdate.isEmpty()) {
            for (String fileToUpdate : filesToUpdate) {
                String file = "${directory}/${fileToUpdate}"
                fileToNewPath.put(file, ghPagesDirectory)
                println "Moving file '${file}' to '${ghPagesDirectory}'"
            }
        }
        if (mappedFilesToUpdate != null && !mappedFilesToUpdate.isEmpty()) {
            for (Map.Entry<String, String> entry : mappedFilesToUpdate.entrySet()) {
                String file = "${directory}/${entry.getKey()}"
                String finalLocation = "${ghPagesDirectory}${entry.getValue()}"
                fileToNewPath.put(file, finalLocation)
                println "Moving file '${file}' to '${finalLocation}'"
            }
        }
    }
    stage(stageName) {
        if (fileToNewPath.isEmpty()) {
            println "THERE ARE NO FILES TO BE UPDATED. Please check that you have provided 'filesToUpdate' or 'mappedFilesToUpdate'."
        } else {
            // add the latest commit id to gh-pages to indicate a functionally new build (the next shell script will commit it)
            sh 'git rev-parse HEAD > ../latest-commit-id.txt'
            dir(ghPagesDirectory) {
                String checkedInCommitId = readFile file: "latest-commit-id.txt"
                String currentCommitId = readFile file: "../latest-commit-id.txt"
                println "Checked in commit Id ${checkedInCommitId}"
                println "Current commit Id ${currentCommitId}"
                if (checkedInCommitId == currentCommitId) {
                    println "No commits since last build so no need to make any further changes."
                } else {
                    sh 'cp ../latest-commit-id.txt .'
                    for (Map.Entry<String, String> entry : fileToNewPath.entrySet()) {
                        sh "cp ${entry.getKey()} ${entry.getValue()}"
                    }
                    sh 'git add --all'
                    sh 'git commit -am "Committing the latest update-site contents to gh-pages branch."'
                    sh "git push ${publishGitUrlVar} ${branch}"
                }
            }
        }
    }
}
