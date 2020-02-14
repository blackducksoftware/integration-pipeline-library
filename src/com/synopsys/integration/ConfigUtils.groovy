package com.synopsys.integration


public class ConfigUtils {
    private final Map config

    public ConfigUtils(Map config) {
        this.config = config
    }

    public Object get(String key, Object defaultValue) {
        String result = defaultValue
        if (config.containsKey(key)) {
            result = config.get(key)
            if (null == result) {
                result = defaultValue
            }
        }
        return result
    }
}
