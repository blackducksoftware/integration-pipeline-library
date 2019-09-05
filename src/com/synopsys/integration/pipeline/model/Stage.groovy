package com.synopsys.integration.pipeline.model

abstract class Stage implements Serializable {
    String name

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

}
