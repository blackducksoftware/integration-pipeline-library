package com.synopsys.integration.pipeline.jenkins


import org.jenkinsci.plugins.workflow.cps.EnvActionImpl

class EnvActionWrapperDryRun extends EnvActionWrapperImpl {
    public final DryRunPipelineBuilder dryRunBuilder

    EnvActionWrapperDryRun(EnvActionImpl envAction, DryRunPipelineBuilder dryRunPipelineBuilder) {
        super(envAction)
        this.dryRunBuilder = dryRunPipelineBuilder
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        getDryRunBuilder().addPipelineLine("setProperty(propertName:${propertyName} value:${newValue})")
    }

    public DryRunPipelineBuilder getDryRunBuilder() {
        return dryRunBuilder
    }
}
