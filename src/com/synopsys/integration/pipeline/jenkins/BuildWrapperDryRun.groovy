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
    Optional<BuildWrapper> getPreviousBuild() throws AbortException {
        return Optional.ofNullable(getRunWrapper().getPreviousBuild())
                .map({ previousBuild -> new BuildWrapperDryRun(previousBuild, this.dryRunPipelineBuilder) })
    }

    @Override
    Optional<BuildWrapper> getNextBuild() throws AbortException {
        return Optional.ofNullable(getRunWrapper().getNextBuild())
                .map({ nextBuild -> new BuildWrapperDryRun(nextBuild, this.dryRunPipelineBuilder) })
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
