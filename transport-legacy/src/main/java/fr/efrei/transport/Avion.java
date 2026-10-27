package fr.efrei.transport;

public class Avion extends Vehicule {

    public Avion(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax);
    }

    public void rouler(int km) {
        // roulage sur la piste
        System.out.println("L'avion " + immatriculation + " roule sur la piste");
    }

    public void naviguer(int milesNautiques) {
        throw new UnsupportedOperationException("Un avion ne navigue pas");
    }

    public void voler(int altitude) {
        System.out.println("L'avion " + immatriculation + " vole à " + altitude + " pieds");
    }

    public void activerRefrigeration(double temperature) {
        throw new UnsupportedOperationException("Pas de soute réfrigérée");
    }

    public boolean peutTransporterMatieresDangereuses() {
        return false;
    }
}
