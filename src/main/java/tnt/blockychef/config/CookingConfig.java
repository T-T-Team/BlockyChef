package tnt.blockychef.config;

import dev.toma.configuration.config.Configurable;

public final class CookingConfig {

    @Configurable
    @Configurable.Comment({"Setting this to true will prevent removing of item which is actively being cooked", "This does not affect items which are being overcooked"})
    @Configurable.Synchronized
    public boolean lockCookingSlots = true;
}
