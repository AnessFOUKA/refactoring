package fr.efrei.transfret;

import java.util.List;

public class AffectationVehicule {

    private final ParcVehicules parcVehicules;

    public AffectationVehicule(ParcVehicules parcVehicules) {
        this.parcVehicules = parcVehicules;
    }

    // retourne le premier véhicule disponible et compatible, null si aucun
    public Vehicule trouverVehicule(List<Colis> colis, ModeTransport mode) {
        double poids = 0;
        for (Colis c : colis) {
            poids = poids + c.getPoids();
        }
        for (Vehicule v : parcVehicules.vehicules()) {
            if (v.estCompatible(mode, colis, poids)) {
                return v;
            }
        }
        return null;
    }
}
