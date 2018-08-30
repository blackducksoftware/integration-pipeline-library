package com.synopsys.integration


public class ConfigUtils {
    private final Map config

    public ConfigUtils(Map config) {
        this.config = config
    }

    public Object get(String key, Object defaultValue) {
        Object value = config.get(key)
        if (value == null) {
            value = defaultValue
        }
        return value
    }
}
