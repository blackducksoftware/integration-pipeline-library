package com.synopsys.integration

import com.synopsys.integration.ToolUtils
import groovy.xml.XmlUtil;


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
            def mvnHome = tool "maven-3"
            def mavenExe = "${mvnHome}/bin/mvn"
            if (null != exe && exe.trim().length() > 0) {
                mavenExe = exe
            }
            def version = script.sh(script: "${mavenExe} help:evaluate -Dexpression=project.version | grep -v '\\['", returnStdout: true)
            return version
        } catch (Exception e) {
            println "Failed to run the mvn command to get the Project version ${e.getMessage()}"
        }
        return null
    }

    @Override
    public String getProjectVersionParse() {
        def fileText = script.readFile file: "${script.env.WORKSPACE}/pom.xml"
        def project = new XmlSlurper().parseText(fileText)
        return project.version.text()
    }

    @Override
    public String removeSnapshotFromProjectVersion() {
        def fileText = script.readFile file: "${script.env.WORKSPACE}/pom.xml"
        def project = new XmlSlurper().parseText(fileText)
        def version = project.version.text()
        project.version = version.replace('-SNAPSHOT', '')
        def xmlString = XmlUtil.serialize(project)
        script.writeFile file: "${script.env.WORKSPACE}/pom.xml", text: "${xmlString}"
        return project.version.text()
    }

    @Override
    public boolean checkForSnapshotDependencies() {
        def mvnHome = script.tool "maven-3"
        def mavenExe = "${mvnHome}/bin/mvn"
        if (null != exe && exe.trim().length() > 0) {
            mavenExe = exe
        }
        script.sh "${mavenExe} dependency:tree -DoutputFile=${script.env.WORKSPACE}/dependency.txt"
        def fileText = script.readFile file: "${script.env.WORKSPACE}/dependency.txt"
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
