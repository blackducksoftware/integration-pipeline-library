package com.synopsys.integration

import com.synopsys.integration.ToolUtils
import groovy.xml.XmlUtil;


public class MavenUtils implements ToolUtils {
    @Override
    public String getProjectVersionProcess(String exe) {
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
        def fileText = readFile file: "${WORKSPACE}/pom.xml"
        def project = new XmlSlurper().parseText(fileText)
        return project.version.text()
    }

    @Override
    public void removeSnapshotFromProjectVersion() {
        def fileText = readFile file: "${WORKSPACE}/pom.xml"
        def project = new XmlSlurper().parseText(fileText)
        def version = project.version.text()
        project.version = version.replace('-SNAPSHOT', '')
        def xmlString = XmlUtil.serialize(project)
        writeFile file: "${WORKSPACE}/pom.xml", text: "${xmlString}"
    }

    @Override
    public boolean checkForSnapshotDependencies() {

    }
}
