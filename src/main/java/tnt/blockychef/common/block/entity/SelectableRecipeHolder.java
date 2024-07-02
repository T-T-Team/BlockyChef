package tnt.blockychef.common.block.entity;

import tnt.tntlib.api.blockentity.Synchronizable;

public interface SelectableRecipeHolder extends Synchronizable {

    void setProcessing(boolean data);

    void changeRecipe(int direction);
}
