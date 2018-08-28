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
        def version = toolUtils.getProjectVersionProcess()
        if (null == version || version.trim().length() == 0) {
            version = toolUtils.getProjectVersionParse()
        }
        script.println version
        return version

    }

    public String getProjectVersion(String tool) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script, null)
        } else {
            toolUtils = new GradleUtils(script, null)
        }
        def version = toolUtils.getMavenProjectVersionParse()
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
        return toolUtils.checkForSnapshotDependencies(checkAllDependencies)
    }


    public String removeSnapshotFromProjectVersion(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script, exe)
        } else {
            toolUtils = new GradleUtils(script, exe)
        }
        return toolUtils.removeSnapshotFromProjectVersion()
    }

    public void increaseSemver(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script, exe)
        } else {
            toolUtils = new GradleUtils(script, exe)
        }
        toolUtils.increaseSemver()
    }
}
