package fr.efrei.transfret;

import java.util.List;

public class Camion extends Vehicule implements VehiculeRoutier, VehiculeCertifieDangereux {

    private final boolean certifieDangereux;

    public Camion(String immatriculation, double capaciteMax, boolean certifieDangereux) {
        super(immatriculation, capaciteMax);
        this.certifieDangereux = certifieDangereux;
    }

    @Override
    public ModeTransport mode() {
        return ModeTransport.ROUTE;
    }

    @Override
    protected boolean accepteMarchandises(java.util.List<Colis> colis) {
        return !contient(TypeMarchandise.REFRIGERE, colis)
                && (!contient(TypeMarchandise.DANGEREUX, colis) || peutTransporterMatieresDangereuses());
    }

    @Override
    protected void effectuerDepart(int distanceKm, List<Colis> colis) {
        rouler(distanceKm);
    }

    @Override
    public void rouler(int kilometres) {
        System.out.println("Le camion " + immatriculation + " roule sur " + kilometres + " km");
    }

    @Override
    public boolean peutTransporterMatieresDangereuses() {
        return certifieDangereux;
    }
}
