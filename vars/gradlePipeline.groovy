#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String emailListVar = config.emailList
    String gitUrlVar = config.gitUrl
    String gradleCommandVar = config.buildCommand

    String detectCommandVar = config.detectCommand

    boolean runGitHubReleaseVar = config.runGitHubRelease ?: true
    String gradleExeVar = config.gradleExe
    String releaseVersionVar = config.releaseVersion
    String ownerVar = config.owner
    String artifactFileVar = config.artifactFile
    String artifactPatternVar = config.artifactPattern
    String artifactDirectoryVar = config.artifactDirectory
    String projectVar = config.project
    String releaseDescriptionVar = config.releaseDescription

    boolean runArchiveVar = config.runArchive ?: true
    String archivePatternVar = config.archivePattern

    boolean runJunitVar = config.runJunit ?: true
    String junitXmlPatternVar = config.junitXmlPattern

    boolean runJacocoVar = config.runJacoco ?: true

    integrationNode {
        emailWrapper(emailListVar) {
            setupStage {
                setJdk {}
            }
            gitStage {
                url = gitUrlVar
            }
            gradleStage {
                buildCommand = gradleCommandVar
            }
            detectStage {
                detectCommand = detectCommandVar
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
        }
    }
}
