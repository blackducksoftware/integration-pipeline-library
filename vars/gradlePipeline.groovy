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
    ////////// SPECIFIC TO THIS PIPELINE /////////////////////
    String gradleExeVar = config.gradleExe
    //////////////////////////////////////////////////////////

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

    boolean runReleaseVar
    try {
        runReleaseVar = config.runRelease
    } catch (MissingPropertyException e) {
        runReleaseVar = false
    }
    boolean checkAllDependenciesVar = config.checkAllDependencies

    integrationPipeline {
        nodeName = nodeNameVar
        emailList = emailListVar
        gitUrl = gitUrlVar
        gitRelativeTargetDir = gitRelativeTargetDirVar

        preBuild = preBuildBody
        ////////// SPECIFIC TO THIS PIPELINE /////////////////////
        buildTool = 'gradle'
        buildBody = {
            gradleStage {
                buildCommand = buildCommandVar
            }
        }
        //////////////////////////////////////////////////////////
        postBuild = postBuildBody

        detectCommand = detectCommandVar

        runGitHubRelease = runGitHubReleaseVar

        exe = gradleExeVar
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
