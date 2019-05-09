package com.synopsys.integration.tools

import com.synopsys.integration.tools.ToolUtils

public class GradleUtils implements ToolUtils, Serializable {
    def script
    private String exe

    public GradleUtils(script, String exe) {
        this.script = script
        this.exe = exe
    }

    @Override
    public void initialize() {
        if (null == exe || exe.trim().length() > 0) {
            this.exe = './gradlew'
        } else {
            this.exe = exe
        }
    }

    @Override
    public String getProjectVersionProcess() {
        try {
            def version = script.sh(script: "${exe} properties -q | grep 'version:'", returnStdout: true)
            return version.substring(version.indexOf(':') + 1).trim()
        } catch (Exception e) {
            script.println "Failed to run the gradle command to get the Project version ${e.getMessage()}"
        }
        return null
    }

    public void updateCommonGradlePluginVersion(boolean isRelease) {
        String commonGradlePluginLine = ''
        def fileText = script.readFile file: "build.gradle"
        def splitLines = fileText.split('\n')
        int commonGradlePluginLineIndex = -1
        for (int i = 0; i < splitLines.size(); i++) {
            def line = splitLines[i]
            def trimmedLine = line.trim()
            println("${trimmedLine}")
            if (commonGradlePluginLine.length() == 0 && isRelease && trimmedLine.contains('common-gradle-plugin:0.0.+')) {
                script.println "Updating the CGP to a fixed version"
                commonGradlePluginLineIndex = i
                String latestVersion = getLatestCommonGradlePluginVersion()
                commonGradlePluginLine = line.replace('0.0.+', latestVersion)
                script.println "Updated the CGP to the fixed version ${latestVersion}"
                break
            } else if (commonGradlePluginLine.length() == 0 && !isRelease && trimmedLine.contains('common-gradle-plugin:') && !trimmedLine.contains('common-gradle-plugin:0.0.+')) {
                script.println "Updating the CGP to a range version"
                commonGradlePluginLineIndex = i
                String temp = trimmedLine.substring(trimmedLine.lastIndexOf(':') + 1)
                if (temp.contains("'")) {
                    temp = temp.substring(0, temp.indexOf("'"))
                } else if (temp.contains('"')) {
                    temp = temp.substring(0, temp.indexOf('"'))
                }
                commonGradlePluginLine = line.replace(temp, '0.0.+')
                script.println "Updated the CGP to the range version 0.0.+"
                break
            }
        }
        if (commonGradlePluginLine.length() != 0) {
            splitLines[commonGradlePluginLineIndex] = commonGradlePluginLine
        }

        def finalFileText = splitLines.join('\n')
        script.writeFile file: "build.gradle", text: "${finalFileText}"
    }

    @Override
    public String removeSnapshotFromProjectVersion() {
        String versionLine = ''
        String modifiedVersion = ''
        def fileText = script.readFile file: "build.gradle"
        def splitLines = fileText.split('\n')
        int versionLineIndex = 0
        for (int i = 0; i < splitLines.size(); i++) {
            def line = splitLines[i]
            def trimmedLine = line.trim()
            if (versionLine.length() == 0 && trimmedLine.startsWith('version ')) {
                versionLineIndex = i
                versionLine = trimmedLine
                def version = versionLine.substring(versionLine.indexOf('=') + 1).replace("'", '').trim()
                modifiedVersion = version.replace('-SNAPSHOT', '')
                versionLine = versionLine.replace(version, modifiedVersion)
                break;
            }
        }
        splitLines[versionLineIndex] = versionLine

        def finalFileText = splitLines.join('\n')
        script.writeFile file: "build.gradle", text: "${finalFileText}"
        return modifiedVersion
    }

    private String getLatestCommonGradlePluginVersion() {
        URL url = new URL("https://repo1.maven.org/maven2/com/blackducksoftware/integration/common-gradle-plugin/maven-metadata.xml")
        def returnMessage = url.getText()
        def rootNode = new XmlSlurper().parseText(returnMessage)
        return rootNode.versioning.latest.text()
    }

    @Override
    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        def command = "${exe} dependencies -q"
        if (!checkAllDependencies) {
            command = "${command} --configuration compile"
        }
        def dependencyText = script.sh(script: "${command}", returnStdout: true)
        script.println "Gradle dependencies"
        script.println "${dependencyText}"
        return dependencyText.contains('-SNAPSHOT')
    }

    @Override
    public String increaseSemver() {
        def versionLine = ''
        def modifiedVersion = ''
        String commonGradlePluginLine = ''
        def fileText = script.readFile file: "build.gradle"
        def splitLines = fileText.split('\n')
        def versionLineIndex = 0
        int commonGradlePluginLineIndex = -1
        for (int i = 0; i < splitLines.size(); i++) {
            def line = splitLines[i]
            def trimmedLine = line.trim()
            if (versionLine.length() == 0 && trimmedLine.startsWith('version ')) {
                versionLineIndex = i
                versionLine = trimmedLine
                def version = versionLine.substring(versionLine.indexOf('=') + 1).replace("'", '').trim()
                int finalVersionPieceIndex = version.lastIndexOf('.') + 1
                def finalVersionPiece = version.substring(finalVersionPieceIndex)
                modifiedVersion = version.substring(0, finalVersionPieceIndex)
                modifiedVersion = "${modifiedVersion}${Integer.valueOf(finalVersionPiece) + 1}-SNAPSHOT"
                versionLine = versionLine.replace(version, modifiedVersion)
            } else if (commonGradlePluginLine.length() == 0 && trimmedLine.contains('common-gradle-plugin:')) {
                commonGradlePluginLineIndex = i
                String temp = trimmedLine.substring(trimmedLine.lastIndexOf(':') + 1)
                if (temp.contains("'")) {
                    temp = temp.substring(0, temp.indexOf("'"))
                } else if (temp.contains('"')) {
                    temp = temp.substring(0, temp.indexOf('"'))
                }
                commonGradlePluginLine = line.replace(temp, '0.0.+')
            }
        }
        splitLines[versionLineIndex] = versionLine
        if (commonGradlePluginLineIndex >= 0) {
            splitLines[commonGradlePluginLineIndex] = commonGradlePluginLine
        }

        def finalFileText = splitLines.join('\n')
        script.writeFile file: "build.gradle", text: "${finalFileText}"
        return modifiedVersion
    }
}
