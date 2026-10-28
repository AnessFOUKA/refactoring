package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SupplementHorsGabaritTest {

    private static final double DELTA = 0.0001;
    private static final double PRIX_BASE = 100;
    private final PrixCalculable supplement = new SupplementHorsGabarit(contexte -> PRIX_BASE);

    @Test
    void une_dimension_egale_a_deux_cents_centimetres_ne_declenche_pas_le_supplement() {
        assertEquals(PRIX_BASE, calculer(List.of(colis(200, 100, 100))), DELTA);
    }

    @Test
    void une_dimension_superieure_a_deux_cents_centimetres_declenche_le_supplement() {
        assertEquals(110, calculer(List.of(colis(201, 100, 100))), DELTA);
        assertEquals(110, calculer(List.of(colis(100, 201, 100))), DELTA);
        assertEquals(110, calculer(List.of(colis(100, 100, 201))), DELTA);
    }

    @Test
    void plusieurs_colis_hors_gabarit_ne_declenchent_le_supplement_qu_une_fois() {
        assertEquals(110, calculer(List.of(
                colis(201, 100, 100),
                colis(100, 250, 100),
                colis(100, 100, 300)
        )), DELTA);
    }

    @Test
    void l_assembleur_applique_le_hors_gabarit_apres_les_autres_supplements_et_avant_la_remise() {
        TarifTransport tarifFixe = (distance, poids, volume) -> PRIX_BASE;
        StrategiesTarification strategies = new StrategiesTarification(
                Map.of(
                        ModeTransport.ROUTE, tarifFixe,
                        ModeTransport.RAIL, tarifFixe,
                        ModeTransport.MER, tarifFixe,
                        ModeTransport.AIR, tarifFixe,
                        ModeTransport.FLUVIAL, tarifFixe
                ),
                Map.of(
                        TypeClient.PARTICULIER, new RemiseParticulier(),
                        TypeClient.ENTREPRISE, new RemiseEntreprise(),
                        TypeClient.PREMIUM, new RemisePremium()
                )
        );
        ContexteTarification contexte = new ContexteTarification(
                List.of(colis(201, 100, 100)),
                0,
                ModeTransport.ROUTE,
                false,
                false,
                TypeClient.ENTREPRISE
        );

        assertEquals(99, new AssembleurPrix().assembler(strategies).calculer(contexte), DELTA);
    }

    private double calculer(List<Colis> colis) {
        return supplement.calculer(new ContexteTarification(
                colis, 0, ModeTransport.ROUTE, false, false, TypeClient.PARTICULIER
        ));
    }

    private static Colis colis(double longueur, double largeur, double hauteur) {
        return new Colis("Hors gabarit", 1, longueur, largeur, hauteur, TypeMarchandise.STANDARD, 100);
    }
}
