package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.logging.PipelineLogger
import org.jenkinsci.plugins.workflow.cps.EnvActionImpl

class EnvActionWrapperDryRun extends EnvActionWrapperImpl {
    private final PipelineLogger logger

    EnvActionWrapperDryRun(EnvActionImpl envAction, PipelineLogger logger) {
        super(envAction)
        this.logger = logger
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        logger.alwaysLog("setProperty propertName:${propertyName} value:${newValue}")
    }

}
