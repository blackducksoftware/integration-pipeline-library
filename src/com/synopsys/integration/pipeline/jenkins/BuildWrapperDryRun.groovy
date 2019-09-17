package com.synopsys.integration.pipeline.jenkins


import hudson.AbortException
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

class BuildWrapperDryRun extends BuildWrapperImpl {
    public final DryRunPipelineBuilder dryRunPipelineBuilder

    BuildWrapperDryRun(RunWrapper runWrapper, DryRunPipelineBuilder dryRunPipelineBuilder) {
        super(runWrapper)
        this.dryRunPipelineBuilder = dryRunPipelineBuilder
    }

    @Override
    void setResult(String result) throws AbortException {
        dryRunPipelineBuilder.addPipelineLine("setResult ${result}")
    }

    @Override
    BuildWrapper getPreviousBuild() throws AbortException {
        return new BuildWrapperDryRun(getRunWrapper().getPreviousBuild(), dryRunPipelineBuilder)
    }

    @Override
    BuildWrapper getNextBuild() throws AbortException {
        return new BuildWrapperDryRun(getRunWrapper().getNextBuild(), dryRunPipelineBuilder)
    }


    @Override
    Map<String, String> getBuildVariables() throws AbortException {
        Map<String, String> copyMap = new HashMap<>()
        copyMap.putAll(getRunWrapper().getBuildVariables())
        return copyMap
    }

    @Override
    List<BuildWrapper> getUpstreamBuilds() throws AbortException {
        List<BuildWrapper> builds = new ArrayList<>()
        builds.addAll(getRunWrapper().getUpstreamBuilds())
        return builds
    }

}
