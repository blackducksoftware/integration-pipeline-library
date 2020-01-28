package com.synopsys.integration.pipeline.utilities

public interface ToolUtils {
    public String getProjectVersion()

    public String updateVersionForRelease(boolean runRelease, boolean runQARelease)

    public boolean checkForSnapshotDependencies(boolean checkAllDependencies)

    public String increaseSemver(boolean runRelease, boolean runQARelease)

    public void initialize()
}
