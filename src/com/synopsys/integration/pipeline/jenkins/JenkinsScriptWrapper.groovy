package com.synopsys.integration.pipeline.jenkins

import org.jenkinsci.plugins.workflow.cps.EnvActionImpl
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

class JenkinsScriptWrapper implements Serializable {
    final Object script

    JenkinsScriptWrapper(final Object script) {
        this.script = script
    }

    Object sh(String command) {
        return script.sh(command)
    }

    void checkout(String url, String branch, String gitToolName, boolean changelog, boolean poll) {
        script.checkout changelog: changelog, poll: poll, scm: [$class: 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false, gitTool: gitToolName, submoduleCfg: [], userRemoteConfigs: [[url: url]]]
    }

    void stage(String stageName, Closure closure) {
        script.stage(stageName, closure)
    }

    void dir(String relativeDirectory, Closure closure) {
        script.dir(relativeDirectory, closure)
    }

    void println(String message) {
        script.println message
    }

    /**
     * org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper
     **/
    RunWrapper currentBuild() {
        return script.currentBuild
    }

    /**
     * org.jenkinsci.plugins.workflow.cps.EnvActionImpl
     **/
    EnvActionImpl env() {
        return script.env
    }

    void emailext(String content, String subjectLine, String recipientList) {
        script.emailext(body: content, subject: subjectLine, to: recipientList)
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
