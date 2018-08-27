package com.synopsys.integration

import com.synopsys.integration.ToolUtils
import groovy.xml.XmlUtil;


public class MavenUtils implements ToolUtils, Serializable {
    def environment
    private final String exe

    public MavenUtils(environment, String exe) {
        this.environment = environment
        this.exe = exe
    }

    @Override
    public String getProjectVersionProcess() {
        try {
            def mvnHome = tool "maven-3"
            def mavenExe = "${mvnHome}/bin/mvn"
            if (null != exe && exe.trim().length() > 0) {
                mavenExe = exe
            }
            def version = sh(script: "${mavenExe} help:evaluate -Dexpression=project.version | grep -v '\\['", returnStdout: true)
            return version
        } catch (Exception e) {
            println "Failed to run the mvn command to get the Project version ${e.getMessage()}"
        }
        return null
    }

    @Override
    public String getProjectVersionParse() {
        def fileText = readFile file: "${environment.WORKSPACE}/pom.xml"
        def project = new XmlSlurper().parseText(fileText)
        return project.version.text()
    }

    @Override
    public String removeSnapshotFromProjectVersion() {
        def fileText = readFile file: "${environment.WORKSPACE}/pom.xml"
        def project = new XmlSlurper().parseText(fileText)
        def version = project.version.text()
        project.version = version.replace('-SNAPSHOT', '')
        def xmlString = XmlUtil.serialize(project)
        writeFile file: "${environment.WORKSPACE}/pom.xml", text: "${xmlString}"
        return project.version.text()
    }

    @Override
    public boolean checkForSnapshotDependencies() {
        def mvnHome = tool "maven-3"
        def mavenExe = "${mvnHome}/bin/mvn"
        if (null != exe && exe.trim().length() > 0) {
            mavenExe = exe
        }
        sh "${mavenExe} dependency:tree -DoutputFile=${environment.WORKSPACE}/dependency.txt"
        def fileText = readFile file: "${environment.WORKSPACE}/dependency.txt"
        def containsSnapshot = fileText.contains('-SNAPSHOT')
        sh "rm ${environment.WORKSPACE}/dependency.txt"
        return containsSnapshot
    }

    @Override
    public String increaseSemver() {
        def fileText = readFile file: "${environment.WORKSPACE}/pom.xml"
        def project = new XmlSlurper().parseText(fileText)
        def version = project.version.text()

        int finalVersionPieceIndex = version.lastIndexOf('.')
        def finalVersionPiece = version.substring(finalVersionPieceIndex + 1)
        def modifiedVersion = version.substring(0, finalVersionPieceIndex)
        modifiedVersion = "${modifiedVersion}${Integer.valueOf(finalVersionPiece) + 1}-SNAPSHOT"

        project.version = modifiedVersion
        def xmlString = XmlUtil.serialize(project)
        writeFile file: "${environment.WORKSPACE}/pom.xml", text: "${xmlString}"
        return project.version.text()
    }
}
