package com.synopsys.integration

import com.synopsys.integration.ToolUtils

public class GradleUtils implements ToolUtils {
    @Override
    public String getProjectVersionProcess(String exe) {
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
    public void removeSnapshotFromProjectVersion() {
        def versionLine = ''
        def fileText = readFile file: "${WORKSPACE}/build.gradle"
        def splitLines = fileText.split('\n')
        def versionLineIndex = 0
        for (int i = 0; i < splitLines.size(); i++) {
            def trimmedLine = line[i].trim()
            if (trimmedLine.startsWith('version')) {
                versionLineIndex = i
                versionLine = trimmedLine
                def version = versionLine.substring(versionLine.indexOf('=') + 1).trim()
                def modifiedVersion = version.replace('-SNAPSHOT', '')
                versionLine = versionLine.replace(version, modifiedVersion)
                break
            }
        }
        splitLines[versionLineIndex] = versionLine

        def finalFileText = splitLines.join('\n')
        writeFile file: "${WORKSPACE}/build.gradle", text: "${finalFileText}"

    }

    @Override
    public boolean checkForSnapshotDependencies() {

    }
}
