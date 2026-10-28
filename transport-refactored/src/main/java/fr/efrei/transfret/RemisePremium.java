package fr.efrei.transfret;

public class RemisePremium implements PolitiqueRemise {

    private static final double COEFFICIENT_REMISE = 0.80;

    @Override
    public double appliquer(double prix) {
        return prix * COEFFICIENT_REMISE;
    }
}
