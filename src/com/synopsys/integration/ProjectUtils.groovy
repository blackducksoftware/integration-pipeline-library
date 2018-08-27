package com.synopsys.integration;

public class ProjectUtils {

    //TODO make the tool and exe fields

    public String getProjectVersion(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(exe)
        } else {
            toolUtils = new GradleUtils(exe)
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
            toolUtils = new MavenUtils(null)
        } else {
            toolUtils = new GradleUtils(null)
        }
        def version = toolUtils.getMavenProjectVersionParse()
        println version
        return version
    }

    public boolean checkForSnapshotDependencies(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(exe)
        } else {
            toolUtils = new GradleUtils(exe)
        }
        return toolUtils.checkForSnapshotDependencies()
    }


    public String removeSnapshotFromProjectVersion(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(exe)
        } else {
            toolUtils = new GradleUtils(exe)
        }
        return toolUtils.removeSnapshotFromProjectVersion()
    }

    public void increaseSemver(String tool, String exe) {
        ToolUtils toolUtils;
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(exe)
        } else {
            toolUtils = new GradleUtils(exe)
        }
        toolUtils.increaseSemver()
    }
}
