package com.synopsys.integration;


public String getProjectVersion(String tool, String exe) {
    ToolUtils toolUtils;
    if (tool.equalsIgnoreCase('maven')) {
        toolUtils = new MavenUtils()
    } else {
        toolUtils = new GradleUtils()
    }
    def version = toolUtils.getProjectVersionProcess(exe)
    if (null == version || version.trim().length() == 0) {
        version = toolUtils.getProjectVersionParse()
    }
    println version
    return version

}

public String getProjectVersion(String tool) {
    ToolUtils toolUtils;
    if (tool.equalsIgnoreCase('maven')) {
        toolUtils = new MavenUtils()
    } else {
        toolUtils = new GradleUtils()
    }
    def version = toolUtils.getMavenProjectVersionParse()
    println version
    return version
}
