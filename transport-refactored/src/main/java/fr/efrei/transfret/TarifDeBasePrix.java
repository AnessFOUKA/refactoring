package fr.efrei.transfret;

public class TarifDeBasePrix implements PrixCalculable {

    private final StrategiesTarification strategies;

    public TarifDeBasePrix(StrategiesTarification strategies) {
        this.strategies = strategies;
    }

    @Override
    public double calculer(ContexteTarification contexte) {
        return strategies.tarifPour(contexte.mode()).calculer(
                contexte.distanceKm(),
                contexte.poidsTotalKg(),
                contexte.volumeTotalMetresCubes()
        );
    }
}
