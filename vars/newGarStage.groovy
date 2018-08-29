#!/usr/bin/groovy

import com.synopsys.integration.ProjectUtils

def call(String stageName = 'GitHub auto release', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String buildTool = config.get('buildTool', 'gradle')
    String releaseVersion = config.releaseVersion
    String owner = config.get('owner', 'blackducksoftware')
    String artifactFile = config.artifactFile
    String artifactPattern = config.artifactPattern
    String artifactDirectory = config.artifactDirectory
    String project = config.project
    String releaseDescription = config.get('releaseDescription', "${COMMIT_MESSAGE}")
    if (null == releaseDescription || releaseDescription.trim().length() == 0) {
        releaseDescription = 'Auto Release'
    }
    if (null == releaseVersion || releaseVersion.trim().length() == 0) {
        def exe = config.exe
        ProjectUtils projectUtils = new ProjectUtils()
        projectUtils.initialize(this, buildTool, exe)
        releaseVersion = projectUtils.getProjectVersion()
    }

    String shellURL = config.get('shellURL', 'https://github.com/blackducksoftware/github-auto-release/releases/download/1.1.0/github_auto_release.sh')

    def options = []
    options.add('-o')
    options.add(owner)
    options.add('-v')
    options.add(releaseVersion)
    if (null != artifactFile && artifactFile.trim().length() > 0) {
        options.add('-f')
        options.add(artifactFile)
    }
    if (null != artifactPattern && artifactPattern.trim().length() > 0) {
        options.add('-t')
        options.add(artifactPattern)
    }
    if (null != artifactDirectory && artifactDirectory.trim().length() > 0) {
        options.add('-d')
        options.add(artifactDirectory)
    }
    if (null != project && project.trim().length() > 0) {
        options.add('-p')
        options.add(project)
    }
    options.add('-m')
    options.add("\"${releaseDescription}\"")

    String commandOptions = options.join(' ')

    println "GitHub Auto Release options ${commandOptions}"

    def commandLines = []
    commandLines.add("#!/bin/bash")
    commandLines.add("wget -N \"${shellURL}\"")
    commandLines.add("chmod 777 github_auto_release.sh")
    commandLines.add("./github_auto_release.sh ${commandOptions}")

    stage(stageName) {
        try {
            sh commandLines.join(" \n")
        } catch (Exception e) {
            println "Failed to run the GitHub auto release ${e.getMessage()}"
        }
    }
}
