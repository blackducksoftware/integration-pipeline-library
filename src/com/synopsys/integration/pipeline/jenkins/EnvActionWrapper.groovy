package com.synopsys.integration.pipeline.jenkins

interface EnvActionWrapper extends Serializable {

    public String getProperty(String propertyName)

    public void setProperty(String propertyName, Object newValue)

    public String getDisplayName()
}
