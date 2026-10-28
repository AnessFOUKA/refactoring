package fr.efrei.transfret;

import java.util.List;

public class Train extends Vehicule implements VehiculeRoutier, VehiculeCertifieDangereux {

    public Train(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax);
    }

    @Override
    public ModeTransport mode() {
        return ModeTransport.RAIL;
    }

    @Override
    protected boolean accepteMarchandises(List<Colis> colis) {
        return true;
    }

    @Override
    protected void effectuerDepart(int distanceKm, List<Colis> colis) {
        rouler(distanceKm);
    }

    public void rouler(int kilometres) {
        System.out.println("Le train " + immatriculation + " circule sur " + kilometres + " km de voies ferrées");
    }

    @Override
    public boolean peutTransporterMatieresDangereuses() {
        return true;
    }
}
