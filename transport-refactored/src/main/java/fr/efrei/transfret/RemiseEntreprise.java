package fr.efrei.transfret;

public class RemiseEntreprise implements PolitiqueRemise {

    private static final double SEUIL_REMISE_SUPERIEURE = 5_000;
    private static final double COEFFICIENT_REMISE_STANDARD = 0.90;
    private static final double COEFFICIENT_REMISE_SUPERIEURE = 0.85;

    @Override
    public double appliquer(double prix) {
        double coefficient = prix > SEUIL_REMISE_SUPERIEURE
                ? COEFFICIENT_REMISE_SUPERIEURE
                : COEFFICIENT_REMISE_STANDARD;
        return prix * coefficient;
    }
}
