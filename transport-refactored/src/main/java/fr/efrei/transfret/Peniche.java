package fr.efrei.transfret;

import java.util.List;

public class Peniche extends Vehicule implements VehiculeNavigable, VehiculeCertifieDangereux {

    public static final String IMMATRICULATION = "PEN-0001";
    public static final double CAPACITE_TONNES = 2_500;
    private static final double KILOGRAMMES_PAR_TONNE = 1_000;

    public Peniche() {
        super(IMMATRICULATION, CAPACITE_TONNES * KILOGRAMMES_PAR_TONNE);
    }

    @Override
    public ModeTransport mode() {
        return ModeTransport.FLUVIAL;
    }

    @Override
    protected boolean accepteMarchandises(List<Colis> colis) {
        return !contient(TypeMarchandise.REFRIGERE, colis);
    }

    @Override
    protected void effectuerDepart(int distanceKm, List<Colis> colis) {
        naviguer(convertirEnMillesNautiques(distanceKm));
    }

    @Override
    public void naviguer(int milesNautiques) {
        System.out.println("La péniche " + immatriculation + " navigue sur " + milesNautiques
                + " milles nautiques");
    }

    @Override
    public boolean peutTransporterMatieresDangereuses() {
        return true;
    }
}
