package tnt.blockychef.common.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;
import tnt.blockychef.common.menu.*;

public final class BlockychefMenuTypes {

    private static final String KEY = "menu";

    @ObjectHolder(value = "blockychef:cutting_board", registryName = KEY)
    public static final MenuType<CuttingBoardMenu> CUTTING_BOARD = null;
    @ObjectHolder(value = "blockychef:toaster", registryName = KEY)
    public static final MenuType<ToasterMenu> TOASTER = null;
    @ObjectHolder(value = "blockychef:mortar_and_pestle", registryName = KEY)
    public static final MenuType<MortarAndPestleMenu> MORTAR_AND_PESTLE = null;
    @ObjectHolder(value = "blockychef:mixing_bowl", registryName = KEY)
    public static final MenuType<MixingBowlMenu> MIXING_BOWL = null;
    @ObjectHolder(value = "blockychef:dough_maker", registryName = KEY)
    public static final MenuType<DoughMakerMenu> DOUGH_MAKER = null;
}
