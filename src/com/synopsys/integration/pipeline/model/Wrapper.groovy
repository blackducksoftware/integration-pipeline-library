package com.synopsys.integration.pipeline.model

abstract class Wrapper {
    private final String name

    public Wrapper(String name) {
        this.name = name
    }

    public abstract void start()

    public abstract void handleException(Exception e)

    public abstract void end()

    String getName() {
        return name
    }
}
