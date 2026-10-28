package fr.efrei.transfret;

import java.util.List;

public class CalculateurPrix {

    private static final double TARIF_ROUTE_PAR_KM = 1.20;
    private static final double TARIF_ROUTE_PAR_KG = 0.10;
    private static final double TARIF_RAIL_PAR_KM = 0.80;
    private static final double TARIF_RAIL_PAR_KG = 0.07;
    private static final double MINIMUM_RAIL = 150;
    private static final double TARIF_MER_PAR_KM = 0.50;
    private static final double TARIF_MER_PAR_KG = 0.03;
    private static final double MINIMUM_MER = 500;
    private static final double TARIF_AIR_PAR_KM = 0.60;
    private static final double TARIF_AIR_PAR_KG_TAXABLE = 4.50;
    private static final double POIDS_VOLUMETRIQUE_PAR_METRE_CUBE = 167;
    private static final double COEFFICIENT_FRAGILE = 1.15;
    private static final double COEFFICIENT_DANGEREUX = 1.30;
    private static final double COEFFICIENT_REFRIGERE = 1.20;
    private static final double COEFFICIENT_URGENCE = 1.50;
    private static final double SEUIL_REMISE_ENTREPRISE = 5_000;
    private static final double COEFFICIENT_REMISE_ENTREPRISE_SUPERIEURE = 0.85;
    private static final double COEFFICIENT_REMISE_ENTREPRISE = 0.90;
    private static final double COEFFICIENT_REMISE_PREMIUM = 0.80;
    private static final double TAUX_ASSURANCE = 0.02;
    private static final double ASSURANCE_MINIMALE = 15;

    public double calculerPrixHT(List<Colis> colis, int distance, ModeTransport mode, boolean urgent,
                                 boolean assurance, TypeClient typeClient) {
        double poids = 0;
        double volume = 0;
        double valeur = 0;
        boolean fragile = false;
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            poids = poids + c.getPoids();
            volume = volume + c.getVolume();
            valeur = valeur + c.getValeurDeclaree();
            if (c.getType() == TypeMarchandise.FRAGILE) fragile = true;
            if (c.getType() == TypeMarchandise.DANGEREUX) dangereux = true;
            if (c.getType() == TypeMarchandise.REFRIGERE) refrigere = true;
        }

        // tarif de base selon le mode
        double prix;
        switch (mode) {
            case ROUTE -> prix = distance * TARIF_ROUTE_PAR_KM + poids * TARIF_ROUTE_PAR_KG;
            case RAIL -> prix = Math.max(distance * TARIF_RAIL_PAR_KM + poids * TARIF_RAIL_PAR_KG, MINIMUM_RAIL);
            case MER -> prix = Math.max(distance * TARIF_MER_PAR_KM + poids * TARIF_MER_PAR_KG, MINIMUM_MER);
            case AIR -> {
                double poidsTaxable = Math.max(poids, volume * POIDS_VOLUMETRIQUE_PAR_METRE_CUBE);
                prix = distance * TARIF_AIR_PAR_KM + poidsTaxable * TARIF_AIR_PAR_KG_TAXABLE;
            }
            default -> throw new IllegalArgumentException("Mode inconnu : " + mode);
        }

        // suppléments
        if (fragile) prix = prix * COEFFICIENT_FRAGILE;
        if (dangereux) prix = prix * COEFFICIENT_DANGEREUX;
        if (refrigere) prix = prix * COEFFICIENT_REFRIGERE;
        if (urgent) prix = prix * COEFFICIENT_URGENCE;

        // remise client
        if (typeClient == TypeClient.ENTREPRISE) {
            if (prix > SEUIL_REMISE_ENTREPRISE) {
                prix = prix * COEFFICIENT_REMISE_ENTREPRISE_SUPERIEURE;
            } else {
                prix = prix * COEFFICIENT_REMISE_ENTREPRISE;
            }
        } else if (typeClient == TypeClient.PREMIUM) {
            prix = prix * COEFFICIENT_REMISE_PREMIUM;
        }

        // assurance
        if (assurance) {
            double montantAssurance = valeur * TAUX_ASSURANCE;
            if (montantAssurance < ASSURANCE_MINIMALE) montantAssurance = ASSURANCE_MINIMALE;
            prix = prix + montantAssurance;
        }
        return Utils.arrondir(prix);
    }
}
