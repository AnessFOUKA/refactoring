package fr.efrei.transfret;

public class Camion extends Vehicule {

    protected boolean adr; // certification matières dangereuses

    public Camion(String immatriculation, double capaciteMax, boolean adr) {
        super(immatriculation, capaciteMax);
        this.adr = adr;
    }

    public void rouler(int km) {
        System.out.println("Le camion " + immatriculation + " roule sur " + km + " km");
    }

    public void naviguer(int milesNautiques) {
        throw new UnsupportedOperationException("Un camion ne navigue pas");
    }

    public void voler(int altitude) {
        throw new UnsupportedOperationException("Un camion ne vole pas");
    }

    public void activerRefrigeration(double temperature) {
        throw new UnsupportedOperationException("Ce camion n'est pas frigorifique");
    }

    public boolean peutTransporterMatieresDangereuses() {
        return adr;
    }
}
