package com.synopsys.integration.pipeline.versioning.utilities

import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger

public class MavenUtils implements com.synopsys.integration.pipeline.versioning.utilities.ToolUtils, Serializable {
    private final PipelineLogger logger
    private final JenkinsScriptWrapper jenkinsScriptWrapper
    private String exe

    public MavenUtils(PipelineLogger logger, JenkinsScriptWrapper jenkinsScriptWrapper, String exe) {
        this.logger = logger;
        this.jenkinsScriptWrapper = jenkinsScriptWrapper
        this.exe = exe

    }

    @Override
    public void initialize() {
        if (null == exe || exe.trim().length() > 0) {
            String mvnHome = jenkinsScriptWrapper.tool 'maven-3'
            String mavenExe = "${mvnHome}/bin/mvn"
            this.exe = mavenExe
        } else {
            this.exe = exe
        }
    }

    @Override
    public String getProjectVersionProcess() {
        try {
            jenkinsScriptWrapper.sh "${exe} help:evaluate -Dexpression=project.version -Doutput=version.txt"
            String versionText = jenkinsScriptWrapper.readFile("version.txt")
            jenkinsScriptWrapper.sh "rm version.txt"
            return versionText.trim()
        } catch (Exception e) {
            logger.error("Failed to run the mvn command to get the Project version ${e.getMessage()}")
        }
        return null
    }

    @Override
    public String removeSnapshotFromProjectVersion() {
        String version = getProjectVersionProcess()
        jenkinsScriptWrapper.println "Maven version ${version}"
        String modifiedVersion = version.replace('-SNAPSHOT', '')
        logger.info("Maven updated version ${modifiedVersion}")

        jenkinsScriptWrapper.executeCommand("${exe} versions:set -DgenerateBackupPoms=false -DnewVersion=${modifiedVersion}", false)
        logger.info("Maven pom updated with version ${modifiedVersion}")
        return modifiedVersion
    }


    @Override
    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        String command = "${exe} dependency:tree -DoutputFile=dependency.txt"
        if (!checkAllDependencies) {
            command = "${command} -Dscope=compile"
        }

        jenkinsScriptWrapper.sh "${command}"
        String fileText = jenkinsScriptWrapper.readFile("dependency.txt")
        logger.info("Maven dependencies")
        logger.info("${fileText}")
        List<String> splitLines = fileText.split('\n')
        // need to remove the first line, since that is the GAV of the current project
        splitLines.remove(0)
        fileText = splitLines.join('\n')
        boolean containsSnapshot = fileText.contains('-SNAPSHOT')
        jenkinsScriptWrapper.sh "rm dependency.txt"
        return containsSnapshot
    }

    @Override
    public String increaseSemver() {
        String version = getProjectVersionProcess()
        logger.info("Maven version ${version}")

        int finalVersionPieceIndex = version.lastIndexOf('.') + 1
        String finalVersionPiece = version.substring(finalVersionPieceIndex)
        String modifiedVersion = version.substring(0, finalVersionPieceIndex)
        logger.info("FINAL VERSION PIECE ${finalVersionPiece}")
        Integer incrementedPiece = Integer.valueOf(finalVersionPiece) + 1
        modifiedVersion = "${modifiedVersion}${incrementedPiece}-SNAPSHOT"

        jenkinsScriptWrapper.executeCommand("${exe} versions:set -DgenerateBackupPoms=false -DnewVersion=${modifiedVersion}", false)
        logger.info("Maven pom updated with version ${modifiedVersion}")
        return modifiedVersion
    }
}
