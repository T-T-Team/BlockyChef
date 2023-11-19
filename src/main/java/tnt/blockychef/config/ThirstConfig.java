package tnt.blockychef.config;

import tnt.tntlib.api.module.configuration.config.Configurable;

public final class ThirstConfig {

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("Allows you to completely disable all thirst logic")
    public boolean thirstDisabled = false;
}
