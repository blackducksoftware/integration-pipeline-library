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
    String gradleCommandVar = config.buildCommand
    Closure postBuildBody = config.postBuild

    String detectCommandVar = config.detectCommand

    boolean runGitHubReleaseVar = config.get('runGitHubRelease', true)
    String gradleExeVar = config.gradleExe
    String releaseVersionVar = config.releaseVersion
    String ownerVar = config.owner
    String artifactFileVar = config.artifactFile
    String artifactPatternVar = config.artifactPattern
    String artifactDirectoryVar = config.artifactDirectory
    String projectVar = config.project
    String releaseDescriptionVar = config.releaseDescription

    boolean runArchiveVar = config.get('runArchive', true)
    String archivePatternVar = config.archivePattern

    boolean runJunitVar = config.get('runJunit', true)
    String junitXmlPatternVar = config.junitXmlPattern

    boolean runJacocoVar = config.get('runJacoco', true)

    pipeline {
        agent none
        parameters {
            booleanParam(defaultValue: false, description: 'If you want to release the project, set this to true', name: 'RUN_RELEASE')
            string(defaultValue: 'Auto Release', description: 'The release note that you want the Auto Release tool to display.', name: 'COMMIT_MESSAGE')
            string(defaultValue: 'master', description: 'The branch you want to build', name: 'BRANCH')
        }
        stages {
            stage('Example') {
                steps {
                    script {
                        integrationNode(nodeNameVar) {
                            emailWrapper(emailListVar) {
                                setupStage {
                                    setJdk {}
                                }
                                def directoryToRunIn = gitStage {
                                    url = gitUrlVar
                                    gitRelativeTargetDir = gitRelativeTargetDirVar
                                }
                                dir(directoryToRunIn) {
                                    if (null != preBuildBody) {
                                        stage('Pre Build') {
                                            preBuildBody()
                                        }
                                    }
                                    gradleStage {
                                        buildCommand = gradleCommandVar
                                    }
                                    if (null != postBuildBody) {
                                        stage('Post Build') {
                                            postBuildBody()
                                        }
                                    }
                                    if (runGitHubReleaseVar) {
                                        newGarStage {
                                            buildTool = 'gradle'
                                            exe = gradleExeVar
                                            releaseVersion = releaseVersionVar
                                            owner = ownerVar
                                            artifactFile = artifactFileVar
                                            artifactPattern = artifactPatternVar
                                            artifactDirectory = artifactDirectoryVar
                                            project = projectVar
                                            releaseDescription = releaseDescriptionVar
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
                                    detectStage {
                                        detectCommand = detectCommandVar
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
