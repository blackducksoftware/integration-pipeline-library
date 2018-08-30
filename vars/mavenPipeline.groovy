#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String nodeNameVar = config.nodeName
    String emailListVar = config.emailList
    String gitUrlVar = config.gitUrl
    String gitRelativeTargetDirVar = config.gitRelativeTargetDir

    Closure preBuildBody = config.preBuild
    String buildCommandVar = config.buildCommand
    Closure postBuildBody = config.postBuild

    String detectCommandVar = config.detectCommand

    boolean runGitHubReleaseVar = config.runGitHubRelease
    String mavenExeVar = config.mavenExe
    String releaseVersionVar = config.releaseVersion
    String ownerVar = config.owner
    String artifactFileVar = config.artifactFile
    String artifactPatternVar = config.artifactPattern
    String artifactDirectoryVar = config.artifactDirectory
    String projectVar = config.project
    String releaseDescriptionVar = config.releaseDescription

    boolean runArchiveVar = config.runArchive
    String archivePatternVar = config.archivePattern

    boolean runJunitVar = config.runJunit
    String junitXmlPatternVar = config.junitXmlPattern

    boolean runJacocoVar = config.runJacoco

    boolean runReleaseVar = config.runRelease
    boolean checkAllDependenciesVar = config.checkAllDependencies

    String mavenToolNameVar = config.toolName ?: 'maven-3'
    integrationNode(nodeNameVar) {
        String mvnHome = tool "${mavenToolNameVar}"
        if (null == mavenExeVar || mavenExeVar.trim().length() == 0) {
            mavenExeVar = "${mvnHome}/bin/mvn"
        }
    }

    integrationPipeline {
        nodeName = nodeNameVar
        emailList = emailListVar
        gitUrl = gitUrlVar
        gitRelativeTargetDir = gitRelativeTargetDirVar

        buildTool = 'maven'

        preBuild = preBuildBody
        buildBody = {
            mavenStage {
                toolName = mavenToolNameVar
                buildCommand = buildCommandVar
            }
        }
        postBuild = postBuildBody

        detectCommand = detectCommandVar

        runGitHubRelease = runGitHubReleaseVar

        exe = mavenExeVar
        releaseVersion = releaseVersionVar
        owner = ownerVar
        artifactFile = artifactFileVar
        artifactPattern = artifactPatternVar
        artifactDirectory = artifactDirectoryVar
        project = projectVar
        releaseDescription = releaseDescriptionVar

        runArchive = runArchiveVar
        archivePattern = archivePatternVar

        runJunit = runJunitVar
        junitXmlPattern = junitXmlPatternVar

        runJacoco = runJacocoVar

        runRelease = runReleaseVar
        checkAllDependencies = checkAllDependenciesVar

    }
}
