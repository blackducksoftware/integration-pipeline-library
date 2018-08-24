#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def emailList = config.emailList
    def gitUrl = config.gitUrl

    def mavenToolName = config.get('toolName', 'maven-3')
    def mavenCommand = config.buildCommand

    def detectCommand = config.detectCommand

    def runGitHubRelease = config.get('runGitHubRelease', true)
    def mavenExe = config.mavenExe
    def releaseVersion = config.releaseVersion
    def owner = config.owner
    def artifactFile = config.artifactFile
    def artifactType = config.artifactType
    def artifactDirectory = config.artifactDirectory
    def project = config.project
    def releaseDescription = config.releaseDescription

    def runArchive = config.get('runArchive', true)
    def archivePattern = config.archivePattern

    def runJunit = config.get('runJunit', true)
    def junitXmlPattern = config.junitXmlPattern

    def runJacoco = config.get('runJacoco', true)

    integrationNode {
        def mvnHome = tool "${mavenToolName}"
        if (null == mavenExe || mavenExe.trim().length() == 0) {
            mavenExe = "${mvnHome}/bin/mvn"
        }

        emailWrapper(emailList) {
            setupStage {
                setJdk {}
            }
            gitStage {
                url = gitUrl
            }
            mavenStage {
                toolName = mavenToolName
                buildCommand = mavenCommand
            }
            detectStage {
                detectCommand = detectCommand
            }
            if (runGitHubRelease) {
                newGarStage {
                    buildTool = 'maven'
                    exe = mavenExe
                    releaseVersion = releaseVersion
                    owner = owner
                    artifactFile = artifactFile
                    artifactType = artifactType
                    artifactDirectory = artifactDirectory
                    project = project
                    releaseDescription = releaseDescription
                }
            }
            if (runArchive) {
                archiveStage {
                    patterns = archivePattern
                }
            }
            if (runJunit) {
                junitStage {
                    xmlPattern = junitXmlPattern
                }
            }
            if (runJacoco) {
                jacocoStage {}
            }
        }
    }
}
