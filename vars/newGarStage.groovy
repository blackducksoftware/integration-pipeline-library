#!/usr/bin/groovy

import com.synopsys.integration.ProjectUtils

def call(String stageName = 'GitHub auto release', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def buildTool = config.get('buildTool', 'gradle')
    def releaseVersion = config.releaseVersion
    def owner = config.get('owner', 'blackducksoftware')
    def artifactFile = config.artifactFile
    def artifactType = config.artifactType
    def artifactDirectory = config.artifactDirectory
    def project = config.project
    def releaseDescription = config.get('releaseDescription', "${COMMIT_MESSAGE}")
    if (null == releaseDescription || releaseDescription.trim().length() == 0) {
        releaseDescription = 'Auto Release'
    }
    if (null == releaseVersion || releaseVersion.trim().length() == 0) {
        ProjectUtils projectUtils = new ProjectUtils()
        def exe = config.exe
        releaseVersion = projectUtils.getProjectVersion(buildTool, exe)
    }

    def options = []
    options.add('-o')
    options.add(owner)
    options.add('-v')
    options.add(releaseVersion)
    if (null == artifactFile || artifactFile.trim().length() == 0) {
        options.add('-f')
        options.add(artifactFile)
    }
    if (null == artifactType || artifactType.trim().length() == 0) {
        options.add('-t')
        options.add(artifactType)
    }
    if (null == artifactDirectory || artifactDirectory.trim().length() == 0) {
        options.add('-d')
        options.add(artifactDirectory)
    }
    if (null == project || project.trim().length() == 0) {
        options.add('-p')
        options.add(project)
    }
    options.add('-m')
    options.add(releaseDescription)

    println "GitHub Auto Release options ${options.join(' ')}"

    def shellURL = config.get('shellURL', 'https://github.com/blackducksoftware/github-auto-release/releases/download/1.1.0/github_auto_release.sh')
    def commitMessage = config.get('commitMessage', "${COMMIT_MESSAGE}")
    if (null == commitMessage || commitMessage.trim().length() == 0) {
        commitMessage = 'Auto Release'
    }

    def commandLines = []
    commandLines.add("#!/bin/bash")
    commandLines.add("wget -N \"${shellURL}\"")
    commandLines.add("chmod 777 github_auto_release.sh")
    commandLines.add("./github_auto_release.sh ${options.join(' ')} -m \"${commitMessage}\"")

    stage(stageName) {
        sh commandLines.join(" \n")

    }
}
