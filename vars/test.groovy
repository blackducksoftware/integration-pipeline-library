#!/usr/bin/groovy

def call(String stageName = 'Setup', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def tool = config.tool
    def exe = config.exe

    if(tool.equals('maven')){
        sh "echo ${getMavenProjectVersionProcess(exe)}"
        sh "echo ${getMavenProjectVersionParse()}"
    } else {
        sh "echo ${getGradleProjectVersionProcess(exe)}"
        sh "echo ${getGradleProjectVersionParse()}"
    }
}


public String getMavenProjectVersionProcess(String exe){
    def mvnHome = tool "maven-3"
    def mavenExe = "${mvnHome}/bin/mvn"
    if(exe) {
        mavenExe = exe
    }
    def version = sh(script: "${mavenExe} help:evaluate -Dexpression=project.version | grep -v '\\['", returnStdout: true)
    return version
}

public String getMavenProjectVersionParse(){
    def fileText = readFile file: "${WORKSPACE}/pom.xml"
    def project = new XmlSlurper().parseText(fileText)
    return project.version.text()
}


public String getGradleProjectVersionProcess(String exe){
   def gradleExe = './gradlew'
   if(exe) {
        gradleExe = exe
   }
   def version = sh(script: "${gradleExe} properties -q | grep 'version:'", returnStdout: true)
   return version.substring(version.indexOf(':') + 1).trim()
}

public String getGradleProjectVersionParse(){
   def versionLine = ''
   def fileText = readFile file: "${WORKSPACE}/build.gradle"
   fileText.split('\n').each { line ->
       def trimmedLine = line.trim()
       if (!versionLine && trimmedLine.startsWith('version')) {
           versionLine = trimmedLine;
       }
   }
   return versionLine.substring(versionLine.indexOf('=') + 1).trim()
}
