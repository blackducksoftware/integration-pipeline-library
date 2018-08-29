package com.synopsys.integration.tools

public interface ToolUtils {
    public String getProjectVersionProcess()

    public String removeSnapshotFromProjectVersion()

    public boolean checkForSnapshotDependencies(boolean checkAllDependencies)

    public String increaseSemver()

    public void initialize()
}
