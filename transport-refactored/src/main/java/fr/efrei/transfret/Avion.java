package fr.efrei.transfret;

import java.util.List;

public class Avion extends Vehicule implements VehiculeVolant {

    public Avion(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax);
    }

    @Override
    public ModeTransport mode() {
        return ModeTransport.AIR;
    }

    @Override
    protected boolean accepteMarchandises(List<Colis> colis) {
        return !contient(TypeMarchandise.DANGEREUX, colis)
                && !contient(TypeMarchandise.REFRIGERE, colis);
    }

    @Override
    protected void effectuerDepart(int distanceKm, List<Colis> colis) {
        effectuerVol(this);
    }

    public void roulerSurPiste() {
        System.out.println("L'avion " + immatriculation + " roule sur la piste");
    }

    public void voler(int altitudePieds) {
        System.out.println("L'avion " + immatriculation + " vole à " + altitudePieds + " pieds");
    }
}
