package com.synopsys.integration;

public class ProjectUtils {
    private ToolUtils toolUtils

    public ProjectUtils() {}

    public void initialize(script, String tool, String exe) {
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script, exe)
        } else {
            toolUtils = new GradleUtils(script, exe)
        }
        toolUtils.initialize()
    }

    public String getProjectVersion() {
        def version = toolUtils.getProjectVersionProcess()
        return version
    }

    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        return toolUtils.checkForSnapshotDependencies(checkAllDependencies)
    }

    public String removeSnapshotFromProjectVersion() {
        return toolUtils.removeSnapshotFromProjectVersion()
    }

    public void increaseSemver() {
        toolUtils.increaseSemver()
    }
}
