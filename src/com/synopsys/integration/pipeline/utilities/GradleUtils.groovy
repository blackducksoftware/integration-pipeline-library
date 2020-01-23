package com.synopsys.integration.pipeline.utilities

import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger

public class GradleUtils implements com.synopsys.integration.pipeline.utilities.ToolUtils, Serializable {
    private final PipelineLogger logger
    private final JenkinsScriptWrapper jenkinsScriptWrapper

    private String exe

    public GradleUtils(PipelineLogger logger, JenkinsScriptWrapper jenkinsScriptWrapper, String exe) {
        this.logger = logger
        this.jenkinsScriptWrapper = jenkinsScriptWrapper
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
            String version = jenkinsScriptWrapper.executeCommand("${exe} properties -q | grep '^version: '", true)
            return version.substring(version.indexOf(':') + 1).trim()
        } catch (Exception e) {
            logger.error("Failed to run the gradle command to get the Project version ${e.getMessage()}")
        }
        return null
    }

    public void updateCommonGradlePluginVersion(boolean isRelease) {
        String commonGradlePluginLine = ''
        String fileText = jenkinsScriptWrapper.readFile("build.gradle")
        def splitLines = fileText.split('\n')
        int commonGradlePluginLineIndex = -1
        for (int i = 0; i < splitLines.size(); i++) {
            String line = splitLines[i]
            String trimmedLine = line.trim()
            if (commonGradlePluginLine.length() == 0 && isRelease && trimmedLine.contains('common-gradle-plugin:')) {
                commonGradlePluginLineIndex = i
                String latestVersion = getLatestCommonGradlePluginVersion()
                String currentVersion = parseLineForCGPVersion(trimmedLine)
                if (!currentVersion.equals(latestVersion)) {
                    logger.debug("Changing the common-gradle-plugin version to '${latestVersion}'")
                    commonGradlePluginLine = line.replace(currentVersion, latestVersion)
                }
                break
            } else if (commonGradlePluginLine.length() == 0 && !isRelease && trimmedLine.contains('common-gradle-plugin:') && !trimmedLine.contains('common-gradle-plugin:0.0.+')) {
                commonGradlePluginLineIndex = i
                String currentVersion = parseLineForCGPVersion(trimmedLine)
                logger.debug("Changing the common-gradle-plugin version back to '0.0.+'")
                commonGradlePluginLine = line.replace(currentVersion, '0.0.+')
                break
            }
        }
        if (commonGradlePluginLine.length() != 0) {
            splitLines[commonGradlePluginLineIndex] = commonGradlePluginLine
        }

        def finalFileText = splitLines.join('\n')
        jenkinsScriptWrapper.writeFile("build.gradle", finalFileText)
    }

    @Override
    public String removeSnapshotFromProjectVersion() {
        String versionLine = ''
        String modifiedVersion = ''
        String fileText = jenkinsScriptWrapper.readFile("build.gradle")
        def splitLines = fileText.split('\n')
        int versionLineIndex = 0
        for (int i = 0; i < splitLines.size(); i++) {
            def line = splitLines[i]
            def trimmedLine = line.trim()
            if (versionLine.length() == 0 && trimmedLine.startsWith('version ')) {
                versionLineIndex = i
                versionLine = trimmedLine
                def version = versionLine.substring(versionLine.indexOf('=') + 1).replace("'", '').replace('"', '').trim()
                modifiedVersion = version.substring(0, versionLine.indexOf('-SNAPSHOT'))
                versionLine = versionLine.replace(version, modifiedVersion)
                break
            }
        }
        splitLines[versionLineIndex] = versionLine

        def finalFileText = splitLines.join('\n')
        jenkinsScriptWrapper.writeFile("build.gradle", finalFileText)
        return modifiedVersion
    }

    private String getLatestCommonGradlePluginVersion() {
        URL url = new URL("https://repo1.maven.org/maven2/com/blackducksoftware/integration/common-gradle-plugin/maven-metadata.xml")
        String returnMessage = url.getText()
        def rootNode = new XmlSlurper().parseText(returnMessage)
        return rootNode.versioning.latest.text()
    }

    @Override
    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        String command = "${exe} dependencies -q"
        if (!checkAllDependencies) {
            command = "${command} --configuration compile"
        }
        String dependencyText = jenkinsScriptWrapper.executeCommand(command, true)
        logger.info("Gradle dependencies")
        logger.info("${dependencyText}")
        return dependencyText.contains('-SNAPSHOT')
    }

    @Override
    public String increaseSemver() {
        String versionLine = ''
        String modifiedVersion = ''
        String commonGradlePluginLine = ''
        String fileText = jenkinsScriptWrapper.readFile("build.gradle")
        def splitLines = fileText.split('\n')
        def versionLineIndex = 0
        int commonGradlePluginLineIndex = -1
        for (int i = 0; i < splitLines.size(); i++) {
            String line = splitLines[i]
            String trimmedLine = line.trim()
            if (versionLine.length() == 0 && trimmedLine.startsWith('version ')) {
                versionLineIndex = i
                versionLine = trimmedLine
                String version = versionLine.substring(versionLine.indexOf('=') + 1).replace("'", '').replace('"', '').trim()
                int finalVersionPieceIndex = version.lastIndexOf('.') + 1
                String finalVersionPiece = version.substring(finalVersionPieceIndex)
                modifiedVersion = version.substring(0, finalVersionPieceIndex)
                modifiedVersion = "${modifiedVersion}${Integer.valueOf(finalVersionPiece) + 1}-SNAPSHOT"
                versionLine = versionLine.replace(version, modifiedVersion)
            } else if (commonGradlePluginLine.length() == 0 && trimmedLine.contains('common-gradle-plugin:0')) {
                commonGradlePluginLineIndex = i
                String currentVersion = parseLineForCGPVersion(trimmedLine)
                commonGradlePluginLine = line.replace(currentVersion, '0.0.+')
            }
        }
        splitLines[versionLineIndex] = versionLine
        if (commonGradlePluginLineIndex >= 0) {
            splitLines[commonGradlePluginLineIndex] = commonGradlePluginLine
        }

        String finalFileText = splitLines.join('\n')
        jenkinsScriptWrapper.writeFile("build.gradle", finalFileText)
        return modifiedVersion
    }

    private String parseLineForCGPVersion(String line) {
        String temp = line.substring(line.lastIndexOf(':') + 1)
        if (temp.contains("'")) {
            temp = temp.substring(0, temp.indexOf("'"))
        } else if (temp.contains('"')) {
            temp = temp.substring(0, temp.indexOf('"'))
        }
        return temp
    }
}
