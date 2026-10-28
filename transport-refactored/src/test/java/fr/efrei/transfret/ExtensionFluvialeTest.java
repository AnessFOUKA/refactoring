package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionFluvialeTest {

    private static final double DELTA = 0.0001;
    private final TarifFluvial tarif = new TarifFluvial();
    private final EstimateurDelai estimateurDelai = new EstimateurDelai();
    private final ValidateurExpedition validateur = new ValidateurExpedition();

    @Test
    void le_tarif_fluvial_calcule_le_prix_kilometrique_et_ponderal() {
        assertEquals(420, tarif.calculer(1_000, 1_000, 0), DELTA);
    }

    @Test
    void le_tarif_fluvial_applique_le_minimum_de_trois_cents_euros() {
        assertEquals(300, tarif.calculer(365, 1_000, 0), DELTA);
    }

    @Test
    void le_delai_fluvial_est_la_distance_sur_360_arrondie_au_superieur_plus_un_jour() {
        assertEquals(4, estimateurDelai.estimer(721, ModeTransport.FLUVIAL, false));
        assertEquals(2, estimateurDelai.estimer(360, ModeTransport.FLUVIAL, false));
        assertEquals(4, estimateurDelai.estimer(721, ModeTransport.FLUVIAL, true));
    }

    @Test
    void le_trajet_paris_le_havre_est_connu_dans_les_deux_sens() {
        assertEquals(365, Utils.getDistance("Paris", "Le Havre"));
        assertEquals(365, Utils.getDistance("Le Havre", "Paris"));
    }

    @Test
    void le_mode_fluvial_refuse_l_urgence_et_le_refrigere() {
        Client client = client();
        assertTrue(validateur.valider(client, List.of(colis(TypeMarchandise.STANDARD)),
                ModeTransport.FLUVIAL, false).isEmpty());
        assertTrue(validateur.valider(client, List.of(colis(TypeMarchandise.STANDARD)),
                ModeTransport.FLUVIAL, true).isPresent());
        assertTrue(validateur.valider(client, List.of(colis(TypeMarchandise.REFRIGERE)),
                ModeTransport.FLUVIAL, false).isPresent());
    }

    @Test
    void la_flotte_affecte_la_peniche_identifiee_et_limitee_a_2500_tonnes() {
        List<Vehicule> flotte = new Flotte().vehicules();
        Peniche peniche = (Peniche) flotte.stream()
                .filter(vehicule -> vehicule.mode() == ModeTransport.FLUVIAL)
                .findFirst()
                .orElseThrow();
        AffectationVehicule affectation = new AffectationVehicule(() -> flotte);

        assertEquals("PEN-0001", peniche.getImmatriculation());
        assertEquals(2_500_000, peniche.getCapaciteMax(), DELTA);
        assertTrue(peniche.estCompatible(ModeTransport.FLUVIAL,
                List.of(colis(TypeMarchandise.STANDARD)), 1));
        assertFalse(peniche.estCompatible(ModeTransport.FLUVIAL,
                List.of(colis(TypeMarchandise.REFRIGERE)), 1));
        assertEquals(peniche, affectation.trouverVehicule(
                List.of(colis(TypeMarchandise.STANDARD)), ModeTransport.FLUVIAL
        ));
    }

    @Test
    void la_peniche_effectue_son_depart_par_navigation() {
        Peniche peniche = new Peniche();
        peniche.partir(365, List.of(colis(TypeMarchandise.STANDARD)));
    }

    private static Client client() {
        return new Client("Client", "client@example.fr", "0600000000", "Adresse",
                TypeClient.PARTICULIER, null);
    }

    private static Colis colis(TypeMarchandise type) {
        return new Colis("Test", 1, 50, 40, 30, type, 100);
    }
}
