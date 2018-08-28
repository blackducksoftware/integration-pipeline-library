package com.synopsys.integration;

public class ProjectUtils {
    def script

    public ProjectUtils(script) {
        this.script = script
    }


    //TODO make the tool and exe fields

    public String getProjectVersion(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script, exe)
        } else {
            toolUtils = new GradleUtils(script, exe)
        }
        toolUtils.initialize()
        def version = toolUtils.getProjectVersionProcess()
        script.println version
        return version

    }

    public boolean checkForSnapshotDependencies(String tool, String exe, boolean checkAllDependencies) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script, exe)
        } else {
            toolUtils = new GradleUtils(script, exe)
        }
        toolUtils.initialize()
        return toolUtils.checkForSnapshotDependencies(checkAllDependencies)
    }


    public String removeSnapshotFromProjectVersion(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script, exe)
        } else {
            toolUtils = new GradleUtils(script, exe)
        }
        toolUtils.initialize()
        return toolUtils.removeSnapshotFromProjectVersion()
    }

    public void increaseSemver(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script, exe)
        } else {
            toolUtils = new GradleUtils(script, exe)
        }
        toolUtils.initialize()
        toolUtils.increaseSemver()
    }
}
