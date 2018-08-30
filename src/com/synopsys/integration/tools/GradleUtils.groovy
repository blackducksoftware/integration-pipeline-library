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
        def fileText = script.readFile file: "build.gradle"
        def splitLines = fileText.split('\n')
        int versionLineIndex = 0
        for (int i = 0; i < splitLines.size(); i++) {
            def trimmedLine = splitLines[i].trim()
            if (trimmedLine.startsWith('version ')) {
                versionLineIndex = i
                versionLine = trimmedLine
                def version = versionLine.substring(versionLine.indexOf('=') + 1).replace("'", '').trim()
                modifiedVersion = version.replace('-SNAPSHOT', '')
                versionLine = versionLine.replace(version, modifiedVersion)
                break
            }
        }
        splitLines[versionLineIndex] = versionLine

        def finalFileText = splitLines.join('\n')
        script.writeFile file: "build.gradle", text: "${finalFileText}"
        return modifiedVersion
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
        def fileText = script.readFile file: "build.gradle"
        def splitLines = fileText.split('\n')
        def versionLineIndex = 0
        for (int i = 0; i < splitLines.size(); i++) {
            def trimmedLine = splitLines[i].trim()
            if (trimmedLine.startsWith('version ')) {
                versionLineIndex = i
                versionLine = trimmedLine
                def version = versionLine.substring(versionLine.indexOf('=') + 1).replace("'", '').trim()
                int finalVersionPieceIndex = version.lastIndexOf('.') + 1
                def finalVersionPiece = version.substring(finalVersionPieceIndex)
                modifiedVersion = version.substring(0, finalVersionPieceIndex)
                modifiedVersion = "${modifiedVersion}${Integer.valueOf(finalVersionPiece) + 1}-SNAPSHOT"
                versionLine = versionLine.replace(version, modifiedVersion)
                break
            }
        }
        splitLines[versionLineIndex] = versionLine

        def finalFileText = splitLines.join('\n')
        script.writeFile file: "build.gradle", text: "${finalFileText}"
        return modifiedVersion
    }
}
