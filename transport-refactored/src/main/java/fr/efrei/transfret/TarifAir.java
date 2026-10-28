package fr.efrei.transfret;

public class TarifAir implements TarifTransport {

    private static final double TARIF_PAR_KM = 0.60;
    private static final double TARIF_PAR_KG_TAXABLE = 4.50;
    private static final double POIDS_VOLUMETRIQUE_PAR_METRE_CUBE = 167;

    @Override
    public double calculer(int distanceKm, double poidsKg, double volumeMetresCubes) {
        double poidsTaxable = Math.max(poidsKg, volumeMetresCubes * POIDS_VOLUMETRIQUE_PAR_METRE_CUBE);
        return distanceKm * TARIF_PAR_KM + poidsTaxable * TARIF_PAR_KG_TAXABLE;
    }
}
