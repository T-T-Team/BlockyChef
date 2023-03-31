package tnt.blockychef.common.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;
import tnt.blockychef.common.menu.CuttingBoardMenu;
import tnt.blockychef.common.menu.ToasterMenu;

public final class BlockychefMenuTypes {

    @ObjectHolder(value = "blockychef:cutting_board", registryName = "menu")
    public static final MenuType<CuttingBoardMenu> CUTTING_BOARD = null;
    @ObjectHolder(value = "blockychef:toaster", registryName = "menu")
    public static final MenuType<ToasterMenu> TOASTER = null;
}
