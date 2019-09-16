package com.synopsys.integration.pipeline.jenkins

import org.jenkinsci.plugins.workflow.cps.EnvActionImpl

class EnvActionWrapperImpl implements EnvActionWrapper {
    public final EnvActionImpl envAction

    EnvActionWrapperImpl(EnvActionImpl envAction) {
        this.envAction = envAction
    }

    @Override
    public String getProperty(String propertyName) {
        return envAction.getProperty(propertyName)
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        envAction.setProperty(propertyName, newValue)
    }

    @Override
    public String getDisplayName() {
        return envAction.getDisplayName()
    }

    EnvActionImpl getEnvAction() {
        return envAction
    }
}
