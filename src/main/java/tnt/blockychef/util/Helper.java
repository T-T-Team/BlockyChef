package tnt.blockychef.util;

import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tnt.blockychef.common.block.entity.SynchronizableBlockEntity;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.packet.S2C_SendBlockEntityData;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Helper {

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(BlockEntityType<A> typeToTest, BlockEntityType<E> wantedType, BlockEntityTicker<? super E> ticker) {
        return typeToTest == wantedType ? (BlockEntityTicker<A>) ticker : null;
    }

    public static void giveItem(Player player, ItemStack stack) {
        if (player.level.isClientSide)
            return;
        Inventory inventory = player.getInventory();
        if (!inventory.add(stack)) {
            Containers.dropItemStack(player.level, player.getX(), player.getY(), player.getZ(), stack);
        }
    }

    public static <B extends BlockEntity & SynchronizableBlockEntity> void sendBlockEntityClientData(B blockEntity) {
        Level level = Objects.requireNonNull(blockEntity, "blockEntity cannot be null").getLevel();
        if (level == null || level.isClientSide)
            return;
        NetworkManager.dispatchClientLevelPacket(level, S2C_SendBlockEntityData.createUpdatePacket(blockEntity));
    }

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
}
