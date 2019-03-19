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

    @Override
    public String removeSnapshotFromProjectVersion() {
        String versionLine = ''
        String modifiedVersion = ''
        String commonGradlePluginLine = ''
        def fileText = script.readFile file: "build.gradle"
        def splitLines = fileText.split('\n')
        int versionLineIndex = 0
        int commonGradlePluginLineIndex = -1
        for (int i = 0; i < splitLines.size(); i++) {
            def trimmedLine = splitLines[i].trim()
            if (versionLine.length() == 0 && trimmedLine.startsWith('version ')) {
                versionLineIndex = i
                versionLine = trimmedLine
                def version = versionLine.substring(versionLine.indexOf('=') + 1).replace("'", '').trim()
                modifiedVersion = version.replace('-SNAPSHOT', '')
                versionLine = versionLine.replace(version, modifiedVersion)
            } else if (commonGradlePluginLine.length() == 0 && trimmedLine.contains('common-gradle-plugin:0.0.+')) {
                commonGradlePluginLineIndex = i
                String latestVersion = getLatestCommonGradlePluginVersion()
                commonGradlePluginLine = splitLines[i].replace('0.0.+', latestVersion)
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
            def trimmedLine = splitLines[i].trim()
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
                String temp = trimmedLine.substring(trimmedLine.lastIndexOf(':'))
                if (temp.contains("'")) {
                    temp = temp.substring(0, temp.indexOf("'"))
                } else if (temp.contains('"')) {
                    temp = temp.substring(0, temp.indexOf('"'))
                }
                commonGradlePluginLine = splitLines[i].replace(temp, '0.0.+')
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
