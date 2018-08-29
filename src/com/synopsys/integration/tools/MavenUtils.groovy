package com.synopsys.integration.tools

import com.synopsys.integration.tools.ToolUtils

public class MavenUtils implements ToolUtils, Serializable {
    def script
    private String exe

    public MavenUtils(script, String exe) {
        this.script = script
        this.exe = exe

    }

    @Override
    public void initialize() {
        if (null == exe || exe.trim().length() > 0) {
            def mvnHome = script.tool 'maven-3'
            def mavenExe = "${mvnHome}/bin/mvn"
            this.exe = mavenExe
        } else {
            this.exe = exe
        }
    }

    @Override
    public String getProjectVersionProcess() {
        try {
            def version = script.sh(script: "${exe} help:evaluate -Dexpression=project.version | grep -v '\\['", returnStdout: true)
            return version.trim()
        } catch (Exception e) {
            script.println "Failed to run the mvn command to get the Project version ${e.getMessage()}"
        }
        return null
    }

    @Override
    public String removeSnapshotFromProjectVersion() {
        def version = getProjectVersionProcess()
        script.println "Maven version ${version}"
        String modifiedVersion = version.replace('-SNAPSHOT', '')
        script.println "Maven updated version ${modifiedVersion}"

        script.sh(script: "${exe} versions:set -DgenerateBackupPoms=false -DnewVersion=${modifiedVersion}", returnStdout: false)
        script.println "Maven pom updated with version ${modifiedVersion}"
        return modifiedVersion
    }


    @Override
    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        def command = "${exe} dependency:tree -DoutputFile=${script.env.WORKSPACE}/dependency.txt"
        if (!checkAllDependencies) {
            command = "${command} -Dscope=compile"
        }

        script.sh "${command}"
        def fileText = script.readFile file: "${script.env.WORKSPACE}/dependency.txt"
        script.println "Maven dependencies"
        script.println "${fileText}"
        List<String> splitLines = fileText.split('\n')
        // need to remove the first line, since that is the GAV of the current project
        splitLines.remove(0)
        fileText = splitLines.join('\n')
        def containsSnapshot = fileText.contains('-SNAPSHOT')
        script.sh "rm ${script.env.WORKSPACE}/dependency.txt"
        return containsSnapshot
    }

    @Override
    public String increaseSemver() {
        def version = getProjectVersionProcess()
        script.println "Maven version ${version}"

        int finalVersionPieceIndex = version.lastIndexOf('.') + 1
        def finalVersionPiece = version.substring(finalVersionPieceIndex)
        def modifiedVersion = version.substring(0, finalVersionPieceIndex)
        script.println "FINAL VERSION PIECE ${finalVersionPiece}"
        def incrementedPiece = Integer.valueOf(finalVersionPiece) + 1
        modifiedVersion = "${modifiedVersion}${incrementedPiece}-SNAPSHOT"

        script.sh(script: "${exe} versions:set -DgenerateBackupPoms=false -DnewVersion=${modifiedVersion}", returnStdout: false)
        script.println "Maven pom updated with version ${modifiedVersion}"
        return modifiedVersion
    }
}
