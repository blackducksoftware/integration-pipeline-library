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
            toolUtils = new MavenUtils(script.env, exe)
        } else {
            toolUtils = new GradleUtils(script.env, exe)
        }
        def version = toolUtils.getProjectVersionProcess()
        if (null == version || version.trim().length() == 0) {
            version = toolUtils.getProjectVersionParse()
        }
        println version
        return version

    }

    public String getProjectVersion(String tool) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script.env, null)
        } else {
            toolUtils = new GradleUtils(script.env, null)
        }
        def version = toolUtils.getMavenProjectVersionParse()
        println version
        return version
    }

    public boolean checkForSnapshotDependencies(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script.env, exe)
        } else {
            toolUtils = new GradleUtils(script.env, exe)
        }
        return toolUtils.checkForSnapshotDependencies()
    }


    public String removeSnapshotFromProjectVersion(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script.env, exe)
        } else {
            toolUtils = new GradleUtils(script.env, exe)
        }
        return toolUtils.removeSnapshotFromProjectVersion()
    }

    public void increaseSemver(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script.env, exe)
        } else {
            toolUtils = new GradleUtils(script.env, exe)
        }
        toolUtils.increaseSemver()
    }
}
