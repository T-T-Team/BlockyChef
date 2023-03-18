package tnt.blockychef.common.food;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class RecipeProcessingType {

    private final ResourceLocation location;
    private final Component component;

    RecipeProcessingType(ResourceLocation location) {
        this.location = location;
        this.component = Component.translatable("recipe.process." + location.toLanguageKey());
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public Component getTranslatedComponent() {
        return component;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeProcessingType type = (RecipeProcessingType) o;
        return location.equals(type.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    @Override
    public String toString() {
        return "RecipeProcessingType{" +
                "id=" + location +
                '}';
    }
}
