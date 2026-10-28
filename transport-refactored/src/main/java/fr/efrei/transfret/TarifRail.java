package fr.efrei.transfret;

public class TarifRail implements TarifTransport {

    private static final double TARIF_PAR_KM = 0.80;
    private static final double TARIF_PAR_KG = 0.07;
    private static final double MINIMUM_FACTURATION = 150;

    @Override
    public double calculer(int distanceKm, double poidsKg, double volumeMetresCubes) {
        return Math.max(distanceKm * TARIF_PAR_KM + poidsKg * TARIF_PAR_KG, MINIMUM_FACTURATION);
    }
}
