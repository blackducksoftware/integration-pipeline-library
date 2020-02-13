package com.synopsys.integration.pipeline.jenkins

import hudson.AbortException
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

class BuildWrapperImpl implements BuildWrapper {
    public final RunWrapper runWrapper

    BuildWrapperImpl(RunWrapper runWrapper) {
        this.runWrapper = runWrapper
    }

    @Override
    void setResult(String result) throws AbortException {
        runWrapper.setResult(result)
    }

    @Override
    int getNumber() throws AbortException {
        return runWrapper.getNumber()
    }

    @Override
    String getResult() throws AbortException {
        return runWrapper.getResult()
    }

    @Override
    String getCurrentResult() throws AbortException {
        return runWrapper.getCurrentResult()
    }

    @Override
    boolean resultIsBetterOrEqualTo(String other) throws AbortException {
        return runWrapper.resultIsBetterOrEqualTo(other)
    }

    @Override
    boolean resultIsWorseOrEqualTo(String other) throws AbortException {
        return runWrapper.resultIsWorseOrEqualTo(other)
    }

    @Override
    long getTimeInMillis() throws AbortException {
        return runWrapper.getTimeInMillis()
    }

    @Override
    long getStartTimeInMillis() throws AbortException {
        return runWrapper.getStartTimeInMillis()
    }

    @Override
    long getDuration() throws AbortException {
        return runWrapper.getDuration()
    }

    @Override
    String getDurationString() throws AbortException {
        return runWrapper.getDurationString()
    }

    @Override
    String getDescription() throws AbortException {
        return runWrapper.getDescription()
    }

    @Override
    String getDisplayName() throws AbortException {
        return runWrapper.getDisplayName()
    }

    @Override
    String getFullDisplayName() throws AbortException {
        return runWrapper.getFullDisplayName()
    }

    @Override
    String getProjectName() throws AbortException {
        return runWrapper.getProjectName()
    }

    @Override
    String getFullProjectName() throws AbortException {
        return runWrapper.getFullProjectName()
    }

    @Override
    Optional<BuildWrapper> getPreviousBuild() throws AbortException {
        return Optional.ofNullable(getRunWrapper().getPreviousBuild())
                .map({ previousBuild -> new BuildWrapperImpl(previousBuild) })
    }

    @Override
    Optional<BuildWrapper> getNextBuild() throws AbortException {
        return Optional.ofNullable(getRunWrapper().getNextBuild())
                .map({ nextBuild -> new BuildWrapperImpl(nextBuild) })
    }

    @Override
    String getId() throws AbortException {
        return runWrapper.getId()
    }

    @Override
    Map<String, String> getBuildVariables() throws AbortException {
        return runWrapper.getBuildVariables()
    }

    @Override
    List<BuildWrapper> getUpstreamBuilds() throws AbortException {
        return runWrapper.getUpstreamBuilds()
    }

    @Override
    String getAbsoluteUrl() throws AbortException {
        return runWrapper.getAbsoluteUrl()
    }

    RunWrapper getRunWrapper() {
        return runWrapper
    }
}
