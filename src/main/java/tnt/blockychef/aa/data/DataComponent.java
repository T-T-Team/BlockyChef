package tnt.blockychef.aa.data;

public interface DataComponent<SRC> {

    boolean isRemovable();

    DataComponent<SRC> copy();

    UIFactory<SRC> getUi();
}
