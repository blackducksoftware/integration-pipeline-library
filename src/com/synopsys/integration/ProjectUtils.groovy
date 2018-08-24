package com.synopsys.integration;

public String getMavenProjectVersion(String exe){
    def mavenVersion = ''
    try {
        def mavenExe = 'mvn'
        if(exe) {
            mavenExe = exe
        }
        def mavenProcess = "${mavenExe} help:evaluate -Dexpression=project.version | grep -v '\\['".execute().waitFor()
        mavenVersion = mavenProcess.getText()
    } catch (Exception e) {
        println "Failed to run the mvn command to get the Project version ${e.getMessage()}"
    }
    if (!mavenVersion){
         def fileText =  new File('pom.xml').text
         def project = new XmlSlurper().parseText(fileText)
         mavenVersion = project.version.text()
    }

    return mavenVersion
}

public String getMavenProjectVersionProcess(String exe){
    def mavenExe = 'mvn'
    if(exe) {
        mavenExe = exe
    }
    def mavenProcess = "${mavenExe} help:evaluate -Dexpression=project.version | grep -v '\\['".execute().waitFor()
    return mavenProcess.getText()
}

public String getMavenProjectVersionParse(){
    def fileText =  new File('pom.xml').text
    def project = new XmlSlurper().parseText(fileText)
    return project.version.text()
}

public String getGradleProjectVersion(String exe){
    def gradleVersion = ''
    try {
        def gradleExe = './gradlew'
        if(exe) {
            gradleExe = exe
        }
        def gradleProcess = "${gradleExe} properties -q | grep 'version:'".execute().waitFor()
        gradleVersion = gradleProcess.getText()
        gradleVersion = gradleVersion.substring(gradleVersion.indexOf(':') + 1).trim()
    } catch (Exception e) {
        println "Failed to run the gradle command to get the Project version ${e.getMessage()}"
    }
    if (!gradleVersion){
        def versionLine = ''
        new File('build.gradle').eachLine { line ->
            def trimmedLine = line.trim()
            if (trimmedLine.startsWith('version')) {
                versionLine = trimmedLine;
                break;
            }
        }
        gradleVersion = versionLine.substring(versionLine.indexOf('=') + 1).trim()
    }
    return gradleVersion
}

public String getGradleProjectVersionProcess(String exe){
   def gradleExe = './gradlew'
   if(exe) {
        gradleExe = exe
   }
   def gradleProcess = "${gradleExe} properties -q | grep 'version:'".execute().waitFor()
   gradleVersion = gradleProcess.getText()
   return gradleVersion.substring(gradleVersion.indexOf(':') + 1).trim()
}

public String getGradleProjectVersionParse(){
   def versionLine = ''
   new File('build.gradle').eachLine { line ->
       def trimmedLine = line.trim()
       if (trimmedLine.startsWith('version')) {
           versionLine = trimmedLine;
           break;
       }
   }
   return versionLine.substring(versionLine.indexOf('=') + 1).trim()
}
