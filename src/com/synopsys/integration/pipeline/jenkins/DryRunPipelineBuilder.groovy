package com.synopsys.integration.pipeline.jenkins

class DryRunPipelineBuilder {
    private static final String INDENT = "  "
    private final List<String> scriptSteps = new ArrayList<>()
    private int indent = 0

    public DryRunPipelineBuilder() {
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
        int count = 0
        while (count < currentIndent) {
            indentString += INDENT
        }
        return indentString
    }

}
