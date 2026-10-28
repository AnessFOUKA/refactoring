package fr.efrei.transfret;

public class TarifRoute implements TarifTransport {

    private static final double TARIF_PAR_KM = 1.20;
    private static final double TARIF_PAR_KG = 0.10;

    @Override
    public double calculer(int distanceKm, double poidsKg, double volumeMetresCubes) {
        return distanceKm * TARIF_PAR_KM + poidsKg * TARIF_PAR_KG;
    }
}
