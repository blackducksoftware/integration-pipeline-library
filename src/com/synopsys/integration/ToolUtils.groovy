package com.synopsys.integration

public interface ToolUtils {
    public String getProjectVersionProcess(String exe);

    public String getProjectVersionParse();

    public void removeSnapshotFromProjectVersion();

    public boolean checkForSnapshotDependencies();
}
