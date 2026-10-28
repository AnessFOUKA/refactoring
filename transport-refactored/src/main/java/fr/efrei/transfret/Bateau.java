package fr.efrei.transfret;

import java.util.List;

public class Bateau extends Vehicule implements VehiculeNavigable, VehiculeRefrigerant,
        VehiculeCertifieDangereux {

    private static final double TEMPERATURE_REFRIGERATION_CELSIUS = 4;
    private final boolean conteneursRefrigeres;

    public Bateau(String immatriculation, double capaciteMax, boolean conteneursRefrigeres) {
        super(immatriculation, capaciteMax);
        this.conteneursRefrigeres = conteneursRefrigeres;
    }

    public ModeTransport mode() {
        return ModeTransport.MER;
    }

    @Override
    protected boolean accepteMarchandises(List<Colis> colis) {
        return conteneursRefrigeres || !contient(TypeMarchandise.REFRIGERE, colis);
    }

    @Override
    protected void effectuerDepart(int distanceKm, List<Colis> colis) {
        naviguer(convertirEnMillesNautiques(distanceKm));
        if (contient(TypeMarchandise.REFRIGERE, colis)) {
            activerRefrigeration(TEMPERATURE_REFRIGERATION_CELSIUS);
        }
    }

    @Override
    public void naviguer(int milesNautiques) {
        System.out.println("Le navire " + immatriculation + " navigue sur " + milesNautiques + " milles nautiques");
    }

    @Override
    public void activerRefrigeration(double temperatureCelsius) {
        System.out.println("Conteneurs frigorifiques réglés à " + temperatureCelsius + "°C");
    }

    @Override
    public boolean peutTransporterMatieresDangereuses() {
        return true;
    }
}
