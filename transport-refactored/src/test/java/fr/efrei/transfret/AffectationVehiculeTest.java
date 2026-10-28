package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AffectationVehiculeTest {

    @Test
    void choisit_un_camion_compatible_pour_une_marchandise_standard() {
        List<Vehicule> flotte = flotteStandard();
        Vehicule affecte = new AffectationVehicule(() -> flotte)
                .trouverVehicule(List.of(colis(TypeMarchandise.STANDARD, 100)), ModeTransport.ROUTE);

        assertSame(flotte.get(0), affecte);
    }

    @Test
    void choisit_un_camion_certifie_pour_les_marchandises_dangereuses() {
        List<Vehicule> flotte = flotteStandard();
        Vehicule affecte = new AffectationVehicule(() -> flotte)
                .trouverVehicule(List.of(colis(TypeMarchandise.DANGEREUX, 100)), ModeTransport.ROUTE);

        assertSame(flotte.get(1), affecte);
        assertTrue(((VehiculeCertifieDangereux) affecte).peutTransporterMatieresDangereuses());
    }

    @Test
    void choisit_un_vehicule_refrigerant_pour_les_marchandises_refrigerees() {
        List<Vehicule> flotte = flotteStandard();
        Vehicule affecte = new AffectationVehicule(() -> flotte)
                .trouverVehicule(List.of(colis(TypeMarchandise.REFRIGERE, 100)), ModeTransport.ROUTE);

        assertSame(flotte.get(2), affecte);
        assertTrue(VehiculeRefrigerant.class.isAssignableFrom(affecte.getClass()));
    }

    @Test
    void un_camion_frigorifique_n_est_pas_affecte_a_une_marchandise_standard() {
        Vehicule camionFrigorifique = new CamionFrigorifique("FRIGO", 8_000);
        AffectationVehicule affectation = new AffectationVehicule(() -> List.of(camionFrigorifique));

        assertFalse(camionFrigorifique.estCompatible(
                ModeTransport.ROUTE,
                List.of(colis(TypeMarchandise.STANDARD, 100)),
                100
        ));
        assertNull(affectation.trouverVehicule(
                List.of(colis(TypeMarchandise.STANDARD, 100)),
                ModeTransport.ROUTE
        ));
    }

    @Test
    void les_vehicules_sont_affectes_par_mode_sans_connaissance_de_leur_classe() {
        List<Vehicule> flotte = flotteStandard();
        AffectationVehicule affectation = new AffectationVehicule(() -> flotte);

        assertSame(flotte.get(3), affectation.trouverVehicule(
                List.of(colis(TypeMarchandise.STANDARD, 100)),
                ModeTransport.RAIL
        ));
        assertSame(flotte.get(4), affectation.trouverVehicule(
                List.of(colis(TypeMarchandise.STANDARD, 100)),
                ModeTransport.MER
        ));
        assertSame(flotte.get(5), affectation.trouverVehicule(
                List.of(colis(TypeMarchandise.STANDARD, 100)),
                ModeTransport.AIR
        ));
    }

    @Test
    void les_vehicules_compatibles_demarrent_leur_propre_mode_sans_operation_non_supportee() {
        List<Vehicule> flotte = flotteStandard();
        List<Colis> colis = List.of(colis(TypeMarchandise.STANDARD, 100));

        for (Vehicule vehicule : flotte) {
            vehicule.partir(100, colis);
        }
    }

    private static List<Vehicule> flotteStandard() {
        return new Flotte().vehicules();
    }

    private static Colis colis(TypeMarchandise type, double poids) {
        return new Colis("Test", poids, 50, 40, 30, type, 100);
    }
}
