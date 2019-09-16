package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper

abstract class Wrapper implements Serializable {
    // Fields here must be public or they can't be accessed (in Jenkins at runtime) in sub classes
    public final String name
    public String relativeDirectory = '.'
    public JenkinsScriptWrapper scriptWrapper

    Wrapper(String name) {
        this.name = name
    }

    abstract void start()

    Optional<String> startMessage() {
        return Optional.empty()
    }

    abstract void handleException(Exception e)

    Optional<String> exceptionMessage() {
        return Optional.empty()
    }

    abstract void end()

    Optional<String> endMessage() {
        return Optional.empty()
    }

    String getName() {
        return name
    }

    String getRelativeDirectory() {
        return relativeDirectory
    }

    void setRelativeDirectory(final String relativeDirectory) {
        this.relativeDirectory = relativeDirectory
    }

    public JenkinsScriptWrapper getScriptWrapper() {
        return scriptWrapper
    }

    public void setScriptWrapper(final JenkinsScriptWrapper scriptWrapper) {
        this.scriptWrapper = scriptWrapper
    }
}
