package tnt.blockychef.common.block.entity;

public interface SelectableRecipeHolder extends SynchronizableBlockEntity {

    void setProcessing(boolean data);

    void changeRecipe(int direction);
}
