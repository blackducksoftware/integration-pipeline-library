package com.synopsys.integration;

public String getMavenProjectVersion(){
    def mavenVersion = ''
    try {
        def mavenProcess = "mvn help:evaluate -Dexpression=project.version | grep -v '\\['".execute().waitFor()
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

public String getMavenProjectVersionProcess(){
    def mavenProcess = "mvn help:evaluate -Dexpression=project.version | grep -v '\['".execute().waitFor()
    return mavenProcess.getText()
}

public String getMavenProjectVersionParse(){
    def fileText =  new File('pom.xml').text
    def project = new XmlSlurper().parseText(fileText)
    return project.version.text()
}

public String getGradleProjectVersion(){
    def gradleVersion = ''
    try {
        def gradleProcess = "./gradlew properties -q | grep 'version:'".execute().waitFor()
        gradleVersion = gradleProcess.getText()
        gradleVersion = gradleVersion.substring(gradleVersion.indexOf(':') + 1).trim()
    } catch (Exception e) {
        println "Failed to run the gradle command to get the Project version ${e.getMessage()}"
    }
    if (!gradleVersion){
        def versionLine = ''
        new File('build.gradle').eachLine { line ->
            def trimmedLine = line.trim()
            if (trimmedLine.startsWith('version') {
                versionLine = trimmedLine;
                break;
            }
        }
        gradleVersion = versionLine.substring(versionLine.indexOf('=') + 1).trim()
    }
    return gradleVersion
}

public String getGradleProjectVersionProcess(){
   def gradleProcess = "./gradlew properties -q | grep 'version:'".execute().waitFor()
   gradleVersion = gradleProcess.getText()
   return gradleVersion.substring(gradleVersion.indexOf(':') + 1).trim()
}

public String getGradleProjectVersionParse(){
   def versionLine = ''
   new File('build.gradle').eachLine { line ->
       def trimmedLine = line.trim()
       if (trimmedLine.startsWith('version') {
           versionLine = trimmedLine;
           break;
       }
   }
   return versionLine.substring(versionLine.indexOf('=') + 1).trim()
}
