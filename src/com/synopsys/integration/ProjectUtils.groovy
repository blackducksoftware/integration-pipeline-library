package com.synopsys.integration;


public String getProjectVersion(String tool, String exe){
    if(tool.equalsIgnoreCase('maven')){
        def version = getMavenProjectVersionProcess(exe)
        if (null == version || version.trim().length() == 0){
            version = getMavenProjectVersionParse()
        }
        println version
        return version
    } else {
        def version = getGradleProjectVersionProcess(exe)
        if (null == version || version.trim().length() == 0){
            version = getGradleProjectVersionParse()
        }
        println version
        return version
    }
}

public String getProjectVersion(String tool){
    if(tool.equalsIgnoreCase('maven')){
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
        if(null != exe && exe.trim().length() > 0) {
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
       if(null != exe && exe.trim().length() > 0) {
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
       if ((null == versionLine || versionLine.trim().length() == 0) && trimmedLine.startsWith('version')) {
           versionLine = trimmedLine;
       }
   }
   return versionLine.substring(versionLine.indexOf('=') + 1).trim()
}
