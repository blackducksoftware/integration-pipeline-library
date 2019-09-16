package com.synopsys.integration.pipeline.jenkins

interface EnvActionWrapper {

    public String getProperty(String propertyName)

    public void setProperty(String propertyName, Object newValue)

    public String getDisplayName()
}
