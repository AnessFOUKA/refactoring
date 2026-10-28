package fr.efrei.transfret;

import java.util.List;

public class CalculateurPrix {

    public double calculerPrixHT(List<Colis> colis, int distance, String mode, boolean urgent, boolean assurance, String typeClient) {
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
            if (c.getType() == 1) fragile = true;
            if (c.getType() == 2) dangereux = true;
            if (c.getType() == 3) refrigere = true;
        }

        // tarif de base selon le mode
        double prix;
        if (mode.equals("ROUTE")) {
            prix = distance * 1.2 + poids * 0.10;
        } else if (mode.equals("RAIL")) {
            prix = distance * 0.8 + poids * 0.07;
            if (prix < 150) prix = 150;
        } else if (mode.equals("MER")) {
            prix = distance * 0.5 + poids * 0.03;
            if (prix < 500) prix = 500;
        } else if (mode.equals("AIR")) {
            double poidsTaxable = poids;
            if (volume * 167 > poids) {
                poidsTaxable = volume * 167;
            }
            prix = distance * 0.6 + poidsTaxable * 4.5;
        } else {
            throw new IllegalArgumentException("Mode inconnu : " + mode);
        }

        // suppléments
        if (fragile) prix = prix * 1.15;
        if (dangereux) prix = prix * 1.30;
        if (refrigere) prix = prix * 1.20;
        if (urgent) prix = prix * 1.5;

        // remise client
        if (typeClient.equals("ENTREPRISE")) {
            if (prix > 5000) {
                prix = prix * 0.85;
            } else {
                prix = prix * 0.90;
            }
        } else if (typeClient.equals("PREMIUM")) {
            prix = prix * 0.80;
        }

        // assurance
        if (assurance) {
            double montantAssurance = valeur * 0.02;
            if (montantAssurance < 15) montantAssurance = 15;
            prix = prix + montantAssurance;
        }
        return Math.round(prix * 100.0) / 100.0;
    }
}
