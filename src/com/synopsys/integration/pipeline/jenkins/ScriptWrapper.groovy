package com.synopsys.integration.pipeline.jenkins

class ScriptWrapper {
    private final Object script

    public ScriptWrapper(final Object script) {
        this.script = script
    }

    public Object sh(String command) {
        return script.sh(command)
    }

    public void println(String message) {
        script.println message
    }

    /**
     * org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper
     **/
    public Object currentBuild() {
        return script.currentBuild
    }

    /**
     * org.jenkinsci.plugins.workflow.cps.EnvActionImpl
     **/
    public Object env() {
        return script.env
    }

    public Object emailext(String content, String subjectLine, String recipientList) {
        return script.emailext(body: "content", subject: "subjectLine", to: "recipientList")
    }

    public String tool(String toolName) {
        return script.tool(toolName)
    }

    /**
     * WorkflowScript
     **/
    public Object script() {
        return script
    }

}
