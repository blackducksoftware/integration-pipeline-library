package com.synopsys.integration.pipeline.jenkins

class ScriptWrapper implements Serializable {
    final Object script

    ScriptWrapper(final Object script) {
        this.script = script
    }

    Object sh(String command) {
        return script.sh(command)
    }

    void stage(String stageName, Closure closure) {
        script.stage(stageName, closure)
    }

    void println(String message) {
        script.println message
    }

    /**
     * org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper
     **/
    Object currentBuild() {
        return script.currentBuild
    }

    /**
     * org.jenkinsci.plugins.workflow.cps.EnvActionImpl
     **/
    Object env() {
        return script.env
    }

    Object emailext(String content, String subjectLine, String recipientList) {
        return script.emailext(body: content, subject: subjectLine, to: recipientList)
    }

    String tool(String toolName) {
        return script.tool(toolName)
    }

    /**
     * WorkflowScript
     **/
    Object script() {
        return script
    }

}
