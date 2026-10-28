package fr.efrei.transfret;

/**
 * Interface commune à tous les véhicules de la flotte.
 */
public interface IVehicule {

    String getImmatriculation();

    double getCapaciteMax();

    double getChargeActuelle();

    boolean estDisponible();

    void setDisponible(boolean disponible);

    void charger(Colis colis);

    void decharger(Colis colis);

    void rouler(int km);

    void naviguer(int milesNautiques);

    void voler(int altitude);

    void activerRefrigeration(double temperature);

    boolean peutTransporterMatieresDangereuses();

    void faireLePlein();
}
