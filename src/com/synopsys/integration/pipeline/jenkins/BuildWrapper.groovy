package com.synopsys.integration.pipeline.jenkins

import hudson.AbortException

interface BuildWrapper extends Serializable {

    public void setResult(String result) throws AbortException

    public int getNumber() throws AbortException

    public String getResult() throws AbortException

    public String getCurrentResult() throws AbortException

    public boolean resultIsBetterOrEqualTo(String other) throws AbortException

    public boolean resultIsWorseOrEqualTo(String other) throws AbortException

    public long getTimeInMillis() throws AbortException

    public long getStartTimeInMillis() throws AbortException

    public long getDuration() throws AbortException

    public String getDurationString() throws AbortException

    public String getDescription() throws AbortException

    public String getDisplayName() throws AbortException

    public String getFullDisplayName() throws AbortException

    public String getProjectName() throws AbortException

    public String getFullProjectName() throws AbortException

    public Optional<BuildWrapper> getPreviousBuild() throws AbortException

    public Optional<BuildWrapper> getNextBuild() throws AbortException

    public String getId() throws AbortException

    public Map<String, String> getBuildVariables() throws AbortException

    public List<BuildWrapper> getUpstreamBuilds() throws AbortException

    public String getAbsoluteUrl() throws AbortException

}
