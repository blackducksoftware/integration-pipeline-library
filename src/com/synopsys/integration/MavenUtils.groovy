package com.synopsys.integration

import com.cloudbees.groovy.cps.NonCPS
import com.synopsys.integration.ToolUtils
import groovy.xml.XmlUtil

public class MavenUtils implements ToolUtils, Serializable {
    def script
    private final String exe

    public MavenUtils(script, String exe) {
        this.script = script
        this.exe = exe
    }

    @Override
    public String getProjectVersionProcess() {
        try {
            def mvnHome = script.tool 'maven-3'
            def mavenExe = "${mvnHome}/bin/mvn"
            if (null != exe && exe.trim().length() > 0) {
                mavenExe = exe
            }
            def version = script.sh(script: "${mavenExe} help:evaluate -Dexpression=project.version | grep -v '\\['", returnStdout: true)
            return version
        } catch (Exception e) {
            script.println "Failed to run the mvn command to get the Project version ${e.getMessage()}"
        }
        return null
    }

    @Override
    public String getProjectVersionParse() {
        def fileText = script.readFile file: "${script.env.WORKSPACE}/pom.xml"
        def project = new XmlSlurper().parseText(fileText)
        return project.version.text()
    }

    //https://stackoverflow.com/questions/41171550/jenkins-java-io-notserializableexception-groovy-util-slurpersupport-nodechild
    @NonCPS
    @Override
    public String removeSnapshotFromProjectVersion() {
        def fileText = script.readFile file: "${script.env.WORKSPACE}/pom.xml"
        def pom = new XmlSlurper().parseText(fileText)
        script.println "MAVEN POM ${pom.text()}"
        def version = pom['version'].text().trim()
        script.println "MAVEN VERSION ${version}"
        def modifiedVersion = version.replace('-SNAPSHOT', '')
        script.println "MAVEN UPDATED VERSION ${version}"
        pom['version'] = modifiedVersion
        def xmlString = XmlUtil.serialize(pom)
        script.println "MAVEN UPDATED POM ${xmlString}"
        script.writeFile file: "${script.env.WORKSPACE}/pom.xml", text: "${xmlString}"
        return modifiedVersion
    }

    @Override
    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        def mvnHome = script.tool 'maven-3'
        def mavenExe = "${mvnHome}/bin/mvn"
        if (null != exe && exe.trim().length() > 0) {
            mavenExe = exe
        }
        def command = "${mavenExe} dependency:tree -DoutputFile=${script.env.WORKSPACE}/dependency.txt"
        if (!checkAllDependencies) {
            command = "${command} -Dscope=compile"
        }

        script.sh "${command}"
        def fileText = script.readFile file: "${script.env.WORKSPACE}/dependency.txt"
        script.println "Maven dependencies"
        script.println "${fileText}"
        List<String> splitLines = fileText.split('\n')
        // need to remove the first line, since that is the GAV of the current project
        splitLines.remove(0)
        fileText = splitLines.join('\n')
        def containsSnapshot = fileText.contains('-SNAPSHOT')
        script.sh "rm ${script.env.WORKSPACE}/dependency.txt"
        return containsSnapshot
    }

    @Override
    public String increaseSemver() {
        def fileText = script.readFile file: "${script.env.WORKSPACE}/pom.xml"
        def project = new XmlSlurper().parseText(fileText)
        def version = project.version.text()

        int finalVersionPieceIndex = version.lastIndexOf('.')
        def finalVersionPiece = version.substring(finalVersionPieceIndex + 1)
        def modifiedVersion = version.substring(0, finalVersionPieceIndex)
        modifiedVersion = "${modifiedVersion}${Integer.valueOf(finalVersionPiece) + 1}-SNAPSHOT"

        project.version = modifiedVersion
        def xmlString = XmlUtil.serialize(project)
        script.writeFile file: "${script.env.WORKSPACE}/pom.xml", text: "${xmlString}"
        return project.version.text()
    }
}
