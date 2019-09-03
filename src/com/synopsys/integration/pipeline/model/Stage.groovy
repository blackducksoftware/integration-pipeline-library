package com.synopsys.integration.pipeline.model

abstract class Stage {
    private final String name

    public Stage(String name) {
        this.name = name
    }

    public final List<StageWrapper> wrappers = new LinkedList<>()

    public void addStageWrapper(StageWrapper wrapper) {
        wrappers.add(wrapper)
    }

    public void run() {
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

    public String getName() {
        return name
    }

}
