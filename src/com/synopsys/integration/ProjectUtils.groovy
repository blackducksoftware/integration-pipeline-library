package com.synopsys.integration;


public String getProjectVersion(String tool, String exe){
    if(tool.equals('maven')){
        def version = getMavenProjectVersionProcess(exe)
        if (!version){
            version = getMavenProjectVersionParse()
        }
        println version
        return version
    } else {
        def version = getGradleProjectVersionProcess(exe)
        if (!version){
            version = getGradleProjectVersionParse()
        }
        println version
        return version
    }
}

public String getProjectVersion(String tool){
    if(tool.equals('maven')){
        def version = getMavenProjectVersionParse()
        println version
        return version
    } else {
        def version = getGradleProjectVersionParse()
        println version
        return version
    }
}

public String getMavenProjectVersionProcess(String exe){
    try {
        def mvnHome = tool "maven-3"
        def mavenExe = "${mvnHome}/bin/mvn"
        if(exe) {
            mavenExe = exe
        }
        def version = sh(script: "${mavenExe} help:evaluate -Dexpression=project.version | grep -v '\\['", returnStdout: true)
        return version
    } catch (Exception e){
        println "Failed to run the mvn command to get the Project version ${e.getMessage()}"
    }
    return null
}

public String getMavenProjectVersionParse(){
    def fileText = readFile file: "${WORKSPACE}/pom.xml"
    def project = new XmlSlurper().parseText(fileText)
    return project.version.text()
}


public String getGradleProjectVersionProcess(String exe){
   try {
       def gradleExe = './gradlew'
       if(exe) {
            gradleExe = exe
       }
       def version = sh(script: "${gradleExe} properties -q | grep 'version:'", returnStdout: true)
       return version.substring(version.indexOf(':') + 1).trim()
    } catch (Exception e) {
        println "Failed to run the gradle command to get the Project version ${e.getMessage()}"
    }
    return null
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
