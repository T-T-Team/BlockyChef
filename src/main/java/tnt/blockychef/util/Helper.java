package tnt.blockychef.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Helper {

    public static <T> Optional<T> find(Collection<T> collection, Predicate<T> filter) {
        for (T t : collection) {
            if (filter.test(t)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public static <C extends Container, R extends Recipe<C>> List<R> getAllValidRecipes(Level level, RecipeType<R> recipeType, C container) {
        RecipeManager manager = level.getRecipeManager();
        return manager.getAllRecipesFor(recipeType).stream()
                .filter(recipe -> recipe.matches(container, level))
                .sorted(Comparator.comparing(Recipe::getId))
                .collect(Collectors.toList());
    }

    public static <C extends Container, R extends Recipe<C>> Optional<R> findRecipeFor(RecipeManager manager, RecipeType<R> type, Predicate<R> recipeTest) {
        return manager.getAllRecipesFor(type).stream()
                .filter(recipeTest)
                .findFirst();
    }

    public static <C extends Container, R extends Recipe<C>> Optional<R> findRecipeByIdFor(RecipeManager manager, RecipeType<R> type, ResourceLocation recipeId) {
        return manager.getAllRecipesFor(type).stream()
                .filter(r -> r.getId().equals(recipeId))
                .findFirst();
    }

    public static float pulse(long total, long period) {
        long l = total % period;
        return pulse(l / (float) period);
    }

    public static float pulse(float f) {
        return f > 0.5F ? 1.0F - ((f - 0.5F) / 0.5F) : f / 0.5F;
    }

    public static <T> boolean contains(T[] array, T element) {
        for (T t : array) {
            if (t.equals(element)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean containsRef(T[] array, T element) {
        for (T t : array) {
            if (t == element) {
                return true;
            }
        }
        return false;
    }

    @SafeVarargs
    public static <E extends Enum<E>> int getEnumFlags(E... values) {
        int result = 0;
        for (E e : values) {
            result |= 1 << e.ordinal();
        }
        return result;
    }

    public static int sum(int value, boolean... values) {
        int sum = 0;
        for (boolean b : values) {
            if (b) {
                sum += value;
            }
        }
        return sum;
    }
}
