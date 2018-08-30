#!/usr/bin/groovy

import com.synopsys.integration.ConfigUtils

def call(String buildToolVar, String exeVar, Closure buildBody, Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    ConfigUtils configUtils = new ConfigUtils(config)

    String nodeName = config.nodeName
    String emailListVar = config.emailList
    String gitUrlVar = config.gitUrl
    String gitRelativeTargetDirVar = config.gitRelativeTargetDir

    Closure preBuildBody = config.preBuild
    Closure postBuildBody = config.postBuild

    String detectCommandVar = config.detectCommand

    boolean runGitHubReleaseVar = configUtils.get('runGitHubRelease', true)

    String releaseVersionVar = config.releaseVersion
    String ownerVar = config.owner
    String artifactFileVar = config.artifactFile
    String artifactPatternVar = config.artifactPattern
    String artifactDirectoryVar = config.artifactDirectory
    String projectVar = config.project
    String releaseDescriptionVar = config.releaseDescription

    boolean runArchiveVar = configUtils.get('runArchive', true)
    String archivePatternVar = config.archivePattern

    boolean runJunitVar = configUtils.get('runJunit', true)
    String junitXmlPatternVar = config.junitXmlPattern

    boolean runJacocoVar = configUtils.get('runJacoco', true)

    boolean runReleaseVar
    try {
        runReleaseVar = configUtils.get('runRelease', Boolean.valueOf("${RUN_RELEASE}"))
    } catch (MissingPropertyException e) {
        runReleaseVar = false
    }
    boolean checkAllDependenciesVar = configUtils.get('checkAllDependencies', false)
    println "Going to run the Release ${runReleaseVar}"

    integrationNode(nodeName) {
        emailWrapper(emailListVar) {
            setupStage {
                setJdk {}
            }
            def directoryToRunIn = gitStage {
                url = gitUrlVar
                relativeTargetDir = gitRelativeTargetDirVar
            }
            dir(directoryToRunIn) {
                if (runReleaseVar) {
                    preReleaseStage {
                        buildTool = buildToolVar
                        exe = exeVar
                        checkAllDependencies = checkAllDependenciesVar
                    }
                }
                if (null != preBuildBody) {
                    stage('Pre Build') {
                        preBuildBody()
                    }
                }
                buildBody()
                if (null != postBuildBody) {
                    stage('Post Build') {
                        postBuildBody()
                    }
                }
                detectStage {
                    detectCommand = detectCommandVar
                }
                if (runGitHubReleaseVar) {
                    newGarStage {
                        buildTool = buildToolVar
                        exe = exeVar
                        releaseVersion = releaseVersionVar
                        owner = ownerVar
                        artifactFile = artifactFileVar
                        artifactPattern = artifactPatternVar
                        artifactDirectory = artifactDirectoryVar
                        project = projectVar
                        releaseDescription = releaseDescriptionVar
                    }
                }
                if (runReleaseVar) {
                    postReleaseStage {
                        buildTool = buildToolVar
                        exe = exeVar
                    }
                }
                if (runArchiveVar) {
                    archiveStage {
                        patterns = archivePatternVar
                    }
                }
                if (runJunitVar) {
                    junitStage {
                        xmlPattern = junitXmlPatternVar
                    }
                }
                if (runJacocoVar) {
                    jacocoStage {}
                }
            }
        }
    }
}
