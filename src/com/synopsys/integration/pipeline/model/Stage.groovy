package com.synopsys.integration.pipeline.model

abstract class Stage implements Serializable {
    // Fields here must be public or they can't be accessed (in Jenkins at runtime) in sub classes
    public final String name
    public String relativeDirectory = '.'

    final List<StageWrapper> wrappers = new LinkedList<>()

    Stage(String name) { this.name = name }

    void addStageWrapper(StageWrapper wrapper) {
        wrappers.add(wrapper)
    }

    void run() {
        wrappers.each { wrapper -> wrapper.start() }
        try {
            stageExecution()
        } catch (Exception e) {
            wrappers.each { wrapper -> wrapper.handleException(e) }
        } finally {
            wrappers.each { wrapper -> wrapper.end() }
        }
    }

    abstract void stageExecution()

    String getName() {
        return name
    }

    String getRelativeDirectory() {
        return relativeDirectory
    }

    void setRelativeDirectory(final String relativeDirectory) {
        this.relativeDirectory = relativeDirectory
    }
}
