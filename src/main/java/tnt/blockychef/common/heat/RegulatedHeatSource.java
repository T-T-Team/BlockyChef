package tnt.blockychef.common.heat;

public interface RegulatedHeatSource extends HeatSource {

    RegulationHandler getRegulationHandler();

    boolean isProducingHeat();
}
