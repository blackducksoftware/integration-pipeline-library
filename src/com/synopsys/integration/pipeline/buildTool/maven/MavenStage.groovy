package com.synopsys.integration.pipeline.buildTool.maven

import com.synopsys.integration.pipeline.jenkins.ScriptWrapper
import com.synopsys.integration.pipeline.model.Stage

class MavenStage extends Stage {
    public static final String DEFAULT_MAVEN_TOOL_NAME = 'maven-3'
    public static final String DEFAULT_MAVEN_OPTIONS = '-U clean package deploy'

    private final ScriptWrapper scriptWrapper
    private String mavenToolName
    private String mavenOptions

    public MavenStage(ScriptWrapper scriptWrapper, String mavenToolName, String mavenOptions) {
        this(scriptWrapper, "Maven Stage", mavenToolName, mavenOptions)
    }

    public MavenStage(ScriptWrapper scriptWrapper, String stageName, String mavenToolName, String mavenOptions) {
        super(stageName, scriptWrapper)
        this.scriptWrapper = scriptWrapper

        if (null != mavenToolName && mavenToolName.trim().length() > 0) {
            this.mavenToolName = mavenToolName
        } else {
            this.mavenToolName = DEFAULT_MAVEN_TOOL_NAME
        }

        if (null != mavenOptions && mavenOptions.trim().length() > 0) {
            this.mavenOptions = mavenOptions
        } else {
            this.mavenOptions = DEFAULT_MAVEN_OPTIONS
        }
    }


    @Override
    void stageExecution() {
        String mvnHome = scriptWrapper.tool("${mavenToolName}")

        scriptWrapper.sh("${mvnHome}/bin/mvn ${mavenOptions}")
    }

    String getMavenToolName() {
        return mavenToolName
    }

    void setMavenToolName(final String mavenToolName) {
        this.mavenToolName = mavenToolName
    }

    String getMavenOptions() {
        return mavenOptions
    }

    void setMavenOptions(final String mavenOptions) {
        this.mavenOptions = mavenOptions
    }

}
