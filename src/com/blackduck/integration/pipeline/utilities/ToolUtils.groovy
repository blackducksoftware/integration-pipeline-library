package com.blackduck.integration.pipeline.utilities

public interface ToolUtils {
    @Deprecated
    public String getProjectVersionProcess()

    public String getProjectVersion()

    public String updateVersionForRelease(boolean runRelease, boolean runQARelease)

    public String updateVersionForRelease(boolean runRelease, boolean runQARelease, String versionUpdateCommand)

    public boolean checkForSnapshotDependencies(boolean checkAllDependencies)

    public String increaseSemver(boolean runRelease, boolean runQARelease)

    public void initialize()
}
