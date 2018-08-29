#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String emailListVar = config.emailList
    String gitUrlVar = config.gitUrl

    String mavenToolNameVar = config.toolName ?: 'maven-3'
    String mavenCommandVar = config.buildCommand

    String detectCommandVar = config.detectCommand

    boolean runGitHubReleaseVar = config.get('runGitHubRelease', true)
    String mavenExeVar = config.mavenExe
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

    integrationNode {
        String mvnHome = tool "${mavenToolNameVar}"
        if (null == mavenExeVar || mavenExeVar.trim().length() == 0) {
            mavenExeVar = "${mvnHome}/bin/mvn"
        }

        emailWrapper(emailListVar) {
            setupStage {
                setJdk {}
            }
            gitStage {
                url = gitUrlVar
            }
            mavenStage {
                toolName = mavenToolNameVar
                buildCommand = mavenCommandVar
            }
            detectStage {
                detectCommand = detectCommandVar
            }
            if (runGitHubReleaseVar) {
                newGarStage {
                    buildTool = 'maven'
                    exe = mavenExeVar
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
