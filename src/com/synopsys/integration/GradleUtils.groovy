package com.synopsys.integration

import com.synopsys.integration.ToolUtils

public class GradleUtils implements ToolUtils {
    private final String exe

    public GradleUtils(String exe) {
        this.exe = exe
    }

    @Override
    public String getProjectVersionProcess() {
        try {
            def gradleExe = './gradlew'
            if (null != exe && exe.trim().length() > 0) {
                gradleExe = exe
            }
            def version = sh(script: "${gradleExe} properties -q | grep 'version:'", returnStdout: true)
            return version.substring(version.indexOf(':') + 1).trim()
        } catch (Exception e) {
            println "Failed to run the gradle command to get the Project version ${e.getMessage()}"
        }
        return null
    }

    @Override
    public String getProjectVersionParse() {
        def versionLine = ''
        def fileText = readFile file: "${WORKSPACE}/build.gradle"
        for (String line : fileText.split('\n')) {
            def trimmedLine = line.trim()
            if (trimmedLine.startsWith('version')) {
                versionLine = trimmedLine
                break
            }
        }
        return versionLine.substring(versionLine.indexOf('=') + 1).trim()
    }

    @Override
    public String removeSnapshotFromProjectVersion() {
        def versionLine = ''
        def modifiedVersion = ''
        def fileText = readFile file: "${WORKSPACE}/build.gradle"
        def splitLines = fileText.split('\n')
        def versionLineIndex = 0
        for (int i = 0; i < splitLines.size(); i++) {
            def trimmedLine = line[i].trim()
            if (trimmedLine.startsWith('version ')) {
                versionLineIndex = i
                versionLine = trimmedLine
                def version = versionLine.substring(versionLine.indexOf('=') + 1).trim()
                modifiedVersion = version.replace('-SNAPSHOT', '')
                versionLine = versionLine.replace(version, modifiedVersion)
                break
            }
        }
        splitLines[versionLineIndex] = versionLine

        def finalFileText = splitLines.join('\n')
        writeFile file: "${WORKSPACE}/build.gradle", text: "${finalFileText}"
        return modifiedVersion
    }

    @Override
    public boolean checkForSnapshotDependencies() {
        def gradleExe = './gradlew'
        if (null != exe && exe.trim().length() > 0) {
            gradleExe = exe
        }
        def dependencyText = sh(script: "${gradleExe} dependencies -q", returnStdout: true)
        return dependencyText.contains('-SNAPSHOT')
    }

    @Override
    public String increaseSemver() {
        def versionLine = ''
        def modifiedVersion = ''
        def fileText = readFile file: "${WORKSPACE}/build.gradle"
        def splitLines = fileText.split('\n')
        def versionLineIndex = 0
        for (int i = 0; i < splitLines.size(); i++) {
            def trimmedLine = line[i].trim()
            if (trimmedLine.startsWith('version ')) {
                versionLineIndex = i
                versionLine = trimmedLine
                def version = versionLine.substring(versionLine.indexOf('=') + 1).trim()
                int finalVersionPieceIndex = version.lastIndexOf('.')
                def finalVersionPiece = version.substring(finalVersionPieceIndex + 1)
                modifiedVersion = version.substring(0, finalVersionPieceIndex)
                modifiedVersion = "${modifiedVersion}${Integer.valueOf(finalVersionPiece) + 1}-SNAPSHOT"
                versionLine = versionLine.replace(version, modifiedVersion)
                break
            }
        }
        splitLines[versionLineIndex] = versionLine

        def finalFileText = splitLines.join('\n')
        writeFile file: "${WORKSPACE}/build.gradle", text: "${finalFileText}"
        return modifiedVersion
    }
}
