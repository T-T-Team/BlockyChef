package tnt.blockychef.common.heat;

public interface RegulatedHeatSource extends HeatSource {

    boolean canToggle();

    boolean canRegulateAmount();

    void adjustAmount(float amount);
}
