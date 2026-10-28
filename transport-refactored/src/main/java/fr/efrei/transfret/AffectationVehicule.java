package fr.efrei.transfret;

import java.util.List;

public class AffectationVehicule {

    // retourne le premier véhicule disponible et compatible, null si aucun
    public Vehicule trouverVehicule(List<Colis> colis, ModeTransport mode) {
        double poids = 0;
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            poids = poids + c.getPoids();
            if (c.getType() == TypeMarchandise.DANGEREUX) dangereux = true;
            if (c.getType() == TypeMarchandise.REFRIGERE) refrigere = true;
        }
        for (Vehicule v : Flotte.vehicules) {
            if (!v.estDisponible()) continue;
            if (v.getCapaciteMax() - v.getChargeActuelle() < poids) continue;
            if (mode == ModeTransport.ROUTE) {
                if (v instanceof Camion) {
                    if (refrigere) {
                        if (v instanceof CamionFrigorifique) {
                            return v;
                        }
                    } else if (v instanceof CamionFrigorifique) {
                        // ne pas prendre le frigo pour autre chose, sinon ça plante au chargement
                        continue;
                    } else if (dangereux) {
                        if (v.peutTransporterMatieresDangereuses()) {
                            return v;
                        }
                    } else {
                        return v;
                    }
                }
            } else if (mode == ModeTransport.RAIL) {
                if (v instanceof Train) {
                    return v;
                }
            } else if (mode == ModeTransport.MER) {
                if (v instanceof Bateau) {
                    if (refrigere && !((Bateau) v).aConteneursRefrigeres()) {
                        continue;
                    }
                    return v;
                }
            } else if (mode == ModeTransport.AIR) {
                if (v instanceof Avion) {
                    return v;
                }
            }
        }
        return null;
    }
}
