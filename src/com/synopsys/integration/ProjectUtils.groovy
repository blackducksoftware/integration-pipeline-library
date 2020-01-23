package com.synopsys.integration

import com.synopsys.integration.pipeline.SimplePipeline
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapperImpl
import com.synopsys.integration.pipeline.logging.DefaultPipelineLogger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.utilities.GradleUtils
import com.synopsys.integration.pipeline.utilities.MavenUtils
import com.synopsys.integration.pipeline.utilities.ToolUtils


public class ProjectUtils {
    private ToolUtils toolUtils = null

    public ProjectUtils() {}

    public void initialize(script, String tool, String exe) {
        script.println "Using tool ${tool}"
        JenkinsScriptWrapper jenkinsScriptWrapper = new JenkinsScriptWrapperImpl(script)
        PipelineLogger pipelineLogger = new DefaultPipelineLogger(jenkinsScriptWrapper)

        if (tool.equalsIgnoreCase(SimplePipeline.MAVEN_BUILD_TOOL)) {
            toolUtils = new MavenUtils(pipelineLogger, jenkinsScriptWrapper, exe)
        } else if (tool.equalsIgnoreCase(SimplePipeline.GRADLE_BUILD_TOOL)) {
            toolUtils = new GradleUtils(pipelineLogger, jenkinsScriptWrapper, exe)
        }
        if (null != toolUtils) {
            script.println "Initializing tool ${tool}"
            toolUtils.initialize()
        }
    }

    public String getProjectVersion() {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.getProjectVersionProcess()
        }
        return version
    }

    public boolean checkForSnapshotDependencies(boolean checkAllDependencies) {
        if (null != toolUtils) {
            return toolUtils.checkForSnapshotDependencies(checkAllDependencies)
        }
        return false
    }

    public String removeSnapshotFromProjectVersion() {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.removeSnapshotFromProjectVersion()
        }
        return version
    }

    public String increaseSemver() {
        def version = ""
        if (null != toolUtils) {
            version = toolUtils.increaseSemver()
        }
        return version
    }
}
