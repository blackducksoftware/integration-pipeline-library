package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.logging.PipelineLogger
import hudson.AbortException
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

class BuildWrapperDryRun extends BuildWrapperImpl {
    private final PipelineLogger logger

    BuildWrapperDryRun(RunWrapper runWrapper, PipelineLogger logger) {
        super(runWrapper)
        this.logger = logger
    }

    @Override
    void setResult(String result) throws AbortException {
        logger.info("setResult ${result}")
    }

    @Override
    BuildWrapper getPreviousBuild() throws AbortException {
        return new BuildWrapperDryRun(getRunWrapper().getPreviousBuild(), logger)
    }

    @Override
    BuildWrapper getNextBuild() throws AbortException {
        return new BuildWrapperDryRun(getRunWrapper().getNextBuild(), logger)
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
