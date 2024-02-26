package tnt.blockychef.common.block.entity;

import javax.annotation.Nullable;

public interface IndexedColorHolder {

    @Nullable
    Integer getColor(int index);

    void setColor(int index, @Nullable Integer color);
}
