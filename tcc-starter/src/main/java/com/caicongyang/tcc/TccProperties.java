package com.caicongyang.tcc;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the TCC starter.
 */
@ConfigurationProperties(prefix = "tcc")
public class TccProperties {

    /** Whether TCC transaction support is enabled. */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
