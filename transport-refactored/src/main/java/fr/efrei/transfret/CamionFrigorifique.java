package fr.efrei.transfret;

import java.util.List;

public class CamionFrigorifique extends Vehicule implements VehiculeRoutier, VehiculeRefrigerant {

    public CamionFrigorifique(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax);
    }

    @Override
    public ModeTransport mode() {
        return ModeTransport.ROUTE;
    }

    @Override
    protected boolean accepteMarchandises(List<Colis> colis) {
        return !colis.isEmpty() && colis.stream()
                .allMatch(colisCourant -> colisCourant.getType() == TypeMarchandise.REFRIGERE);
    }

    @Override
    protected void effectuerDepart(int distanceKm, List<Colis> colis) {
        rouler(distanceKm);
        refrigererSiNecessaire(colis, this);
    }

    @Override
    public void rouler(int kilometres) {
        System.out.println("Le camion frigorifique " + immatriculation + " roule sur " + kilometres + " km");
    }

    @Override
    public void activerRefrigeration(double temperatureCelsius) {
        System.out.println("Réfrigération activée à " + temperatureCelsius + "°C sur " + immatriculation);
    }
}
