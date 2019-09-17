package com.synopsys.integration.pipeline.jenkins


import org.jenkinsci.plugins.workflow.cps.EnvActionImpl

class EnvActionWrapperDryRun extends EnvActionWrapperImpl {
    public final DryRunPipelineBuilder dryRunPipelineBuilder

    EnvActionWrapperDryRun(EnvActionImpl envAction, DryRunPipelineBuilder dryRunPipelineBuilder) {
        super(envAction)
        this.dryRunPipelineBuilder = dryRunPipelineBuilder
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        dryRunPipelineBuilder.addPipelineLine("setProperty(propertName:${propertyName} value:${newValue})")
    }

}
