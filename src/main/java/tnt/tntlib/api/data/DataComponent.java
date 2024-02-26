package tnt.tntlib.api.data;

public interface DataComponent<SRC> {

    boolean isRemovable();

    DataComponent<SRC> copy();

    UIFactory<SRC> getUi();
}
