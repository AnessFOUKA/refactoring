package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.List;

public abstract class Vehicule implements IVehicule {

    private static final int ALTITUDE_CROISIERE_AVION_PIEDS = 35_000;
    private static final double KILOMETRES_PAR_MILLE_NAUTIQUE = 1.852;
    private static final double TEMPERATURE_REFRIGERATION_CELSIUS = 4;

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

    public abstract ModeTransport mode();

    protected abstract boolean accepteMarchandises(List<Colis> colis);

    protected abstract void effectuerDepart(int distanceKm, List<Colis> colis);

    public final boolean estCompatible(ModeTransport mode, List<Colis> colis, double poidsTotal) {
        return disponible
                && this.mode() == mode
                && capaciteMax - chargeActuelle >= poidsTotal
                && accepteMarchandises(colis);
    }

    public final void partir(int distanceKm, List<Colis> colis) {
        effectuerDepart(distanceKm, colis);
    }

    protected final boolean contient(TypeMarchandise type, List<Colis> colis) {
        return colis.stream().anyMatch(colisCourant -> colisCourant.getType() == type);
    }

    protected final void refrigererSiNecessaire(List<Colis> colis, VehiculeRefrigerant vehicule) {
        if (contient(TypeMarchandise.REFRIGERE, colis)) {
            vehicule.activerRefrigeration(TEMPERATURE_REFRIGERATION_CELSIUS);
        }
    }

    protected final int convertirEnMillesNautiques(int distanceKm) {
        return (int) (distanceKm / KILOMETRES_PAR_MILLE_NAUTIQUE);
    }

    protected final void effectuerVol(VehiculeVolant vehicule) {
        vehicule.roulerSurPiste();
        vehicule.voler(ALTITUDE_CROISIERE_AVION_PIEDS);
    }
}
