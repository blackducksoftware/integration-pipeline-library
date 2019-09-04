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

    public Object currentBuild() {
        return script.currentBuild
    }

    public Object env() {
        return script.env
    }

    public Object emailext(String content, String subjectLine, String recipientList) {
        return script.emailext(body: "content", subject: "subjectLine", to: "recipientList")
    }

    public String tool(String toolName) {
        return script.tool(toolName)
    }

    public Object script() {
        return script
    }

}
