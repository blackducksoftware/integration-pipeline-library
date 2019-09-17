package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.logging.PipelineLogger

class DryRunPipelineBuilder implements Serializable {
    private static final String INDENT = "  "
    private final List<String> scriptSteps = new ArrayList<>()
    private int indent = 0

    private final PipelineLogger logger

    public DryRunPipelineBuilder(PipelineLogger logger) {
        this.logger = logger
        scriptSteps = new ArrayList<>()
        indent = 0
    }

    public void initialize() {
        addPipelineLine('{')
        increaseIndent()
    }

    public void addPipelineLine(String line) {
        String currentIndentString = indentString(indent)
        scriptSteps.add("${currentIndentString}${line}")
    }


    public String getPipelineString() {
        decreaseIndent()
        addPipelineLine('}')

        return scriptSteps.join(" \n")
    }

    public void increaseIndent() {
        indent++
    }

    public void decreaseIndent() {
        indent--
    }

    private String indentString(int currentIndent) {
        String indentString = ''

        for (int i = 0; i < currentIndent; i++) {
            indentString += INDENT
        }
        return indentString
    }

}
