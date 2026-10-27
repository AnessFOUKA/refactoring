package fr.efrei.transport;

import java.util.ArrayList;
import java.util.List;

public abstract class Vehicule implements IVehicule {

    protected String immatriculation;
    protected double capaciteMax;
    protected double chargeActuelle;
    protected boolean disponible = true;
    protected List<Colis> chargement = new ArrayList<Colis>();

    public Vehicule(String immatriculation, double capaciteMax) {
        this.immatriculation = immatriculation;
        this.capaciteMax = capaciteMax;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public double getCapaciteMax() {
        return capaciteMax;
    }

    public double getChargeActuelle() {
        return chargeActuelle;
    }

    public boolean estDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void charger(Colis colis) {
        if (chargeActuelle + colis.getPoids() > capaciteMax) {
            System.out.println("ERREUR : capacité dépassée pour " + immatriculation);
            return;
        }
        chargement.add(colis);
        chargeActuelle = chargeActuelle + colis.getPoids();
    }

    public void decharger(Colis colis) {
        if (chargement.remove(colis)) {
            chargeActuelle = chargeActuelle - colis.getPoids();
        }
    }

    public void faireLePlein() {
        System.out.println("Plein effectué pour " + immatriculation);
    }
}
