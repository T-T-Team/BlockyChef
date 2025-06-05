package tnt.blockychef.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.food.mastery.CookingMasteryManager;
import tnt.blockychef.common.food.mastery.PlayerMasteryDataProvider;

import java.util.stream.Stream;

public final class MasteryCommand {

    private static final SuggestionProvider<CommandSourceStack> MASTERY_SUGGESTIONS = (context, builder) -> {
        CookingMasteryManager manager = BlockyChef.MASTERY_MANAGER;
        Stream<ResourceLocation> mappedIds = manager.getFullMasteryList().stream()
                .map(mastery -> ForgeRegistries.ITEMS.getKey(mastery.item()));
        return SharedSuggestionProvider.suggestResource(mappedIds, builder);
    };
    private static final DynamicCommandExceptionType UNKNOWN_MASTERY_ITEM = new DynamicCommandExceptionType(obj -> Component.literal("Unknown item: " + obj));
    private static final DynamicCommandExceptionType UNKNOWN_MASTERY = new DynamicCommandExceptionType(obj -> Component.literal("Unknown mastery: " + obj));

    public static void create(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("mastery")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.literal("set")
                                        .then(
                                                Commands.argument("target", EntityArgument.player())
                                                        .then(
                                                                Commands.argument("mastery", ResourceLocationArgument.id())
                                                                        .suggests(MASTERY_SUGGESTIONS)
                                                                        .then(
                                                                                Commands.argument("value", IntegerArgumentType.integer(0))
                                                                                        .executes(MasteryCommand::setMastery)
                                                                        )
                                                        )
                                        )

                        )
        );
    }

    private static int setMastery(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation masteryId = ResourceLocationArgument.getId(context, "mastery");
        int level = IntegerArgumentType.getInteger(context, "value");
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        if (!ForgeRegistries.ITEMS.containsKey(masteryId)) {
            throw UNKNOWN_MASTERY_ITEM.create(masteryId);
        }
        Item item = ForgeRegistries.ITEMS.getValue(masteryId);
        CookingMastery mastery = BlockyChef.MASTERY_MANAGER.getMastery(item).orElse(null);
        if (mastery == null) {
            throw UNKNOWN_MASTERY.create(masteryId);
        }
        PlayerMasteryDataProvider.getMasteryData(player).ifPresent(data -> {
            data.setCookedCount(item, level);
            data.sendClientData();
        });
        return 0;
    }
}
