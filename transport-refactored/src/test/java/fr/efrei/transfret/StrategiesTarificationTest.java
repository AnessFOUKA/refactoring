package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategiesTarificationTest {

    private static final double DELTA = 0.0001;

    @Test
    void tarif_route_applique_le_prix_par_kilometre_et_par_kilo() {
        assertEquals(125, new TarifRoute().calculer(100, 50, 0), DELTA);
    }

    @Test
    void tarif_rail_respecte_le_minimum_de_facturation() {
        assertEquals(150, new TarifRail().calculer(100, 50, 0), DELTA);
    }

    @Test
    void tarif_mer_respecte_le_minimum_de_facturation() {
        assertEquals(500, new TarifMer().calculer(100, 50, 0), DELTA);
    }

    @Test
    void tarif_air_utilise_le_poids_volumetrique_quand_il_est_superieur() {
        assertEquals(1_623, new TarifAir().calculer(200, 100, 2), DELTA);
    }

    @Test
    void tarif_air_utilise_le_poids_reel_quand_il_est_superieur() {
        assertEquals(2_370, new TarifAir().calculer(200, 500, 1), DELTA);
    }

    @Test
    void remise_particulier_ne_change_pas_le_prix() {
        assertEquals(5_000, new RemiseParticulier().appliquer(5_000), DELTA);
    }

    @Test
    void remise_entreprise_standard_s_applique_jusqu_au_seuil_inclus() {
        assertEquals(4_500, new RemiseEntreprise().appliquer(5_000), DELTA);
    }

    @Test
    void remise_entreprise_superieure_s_applique_au_dessus_du_seuil() {
        assertEquals(4_250.0085, new RemiseEntreprise().appliquer(5_000.01), DELTA);
    }

    @Test
    void remise_premium_applique_vingt_pourcent() {
        assertEquals(4_000, new RemisePremium().appliquer(5_000), DELTA);
    }
}
