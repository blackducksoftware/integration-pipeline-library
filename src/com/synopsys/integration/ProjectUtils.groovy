package com.synopsys.integration

import com.synopsys.integration.tools.GradleUtils
import com.synopsys.integration.tools.MavenUtils
import com.synopsys.integration.tools.ToolUtils

public class ProjectUtils {
    private ToolUtils toolUtils = null

    public ProjectUtils() {}

    public void initialize(script, String tool, String exe) {
        script.println "Using tool ${tool}"
        if (tool.equalsIgnoreCase('maven')) {
            toolUtils = new MavenUtils(script, exe)
        } else if (tool.equalsIgnoreCase('gradle')) {
            toolUtils = new GradleUtils(script, exe)
        }
        if (null != toolUtils) {
            script.println "Initializing tool ${tool}"
            toolUtils.initialize()
        }
    }

    public String getProjectVersion() {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.getProjectVersionProcess()
        }
        return version
    }

    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        if (null != toolUtils) {
            return toolUtils.checkForSnapshotDependencies(checkAllDependencies)
        }
        return false
    }

    public String removeSnapshotFromProjectVersion() {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.removeSnapshotFromProjectVersion()
        }
        return version
    }

    public String increaseSemver() {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.increaseSemver()
        }
        return version
    }
}
