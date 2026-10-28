package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecorateursPrixTest {

    private static final double DELTA = 0.0001;

    @Test
    void les_supplements_sont_appliques_une_fois_dans_l_ordre_defini() {
        StrategiesTarification strategies = strategiesAvecTarifRoute(100);
        PrixCalculable prix = new AssembleurPrix().assembler(strategies);
        ContexteTarification contexte = contexte(
                List.of(
                        colis(TypeMarchandise.FRAGILE, 0),
                        colis(TypeMarchandise.FRAGILE, 0),
                        colis(TypeMarchandise.DANGEREUX, 0),
                        colis(TypeMarchandise.REFRIGERE, 0)
                ),
                true,
                false,
                TypeClient.PARTICULIER
        );

        assertEquals(269.10, new DecorateurAssurance(
                new DecorateurRemise(
                        new SupplementUrgence(
                                new SupplementRefrigere(
                                        new SupplementDangereux(
                                                new SupplementFragile(c -> 100)
                                        )
                                )
                        ),
                        strategies
                )
        ).calculer(contexte), DELTA);
        assertEquals(269.10, prix.calculer(contexte), DELTA);
    }

    @Test
    void la_remise_entreprise_s_applique_apres_les_supplements_et_l_urgence() {
        StrategiesTarification strategies = strategiesAvecTarifRoute(3_000);
        PrixCalculable prix = new AssembleurPrix().assembler(strategies);
        ContexteTarification contexte = contexte(
                List.of(colis(TypeMarchandise.DANGEREUX, 0)),
                true,
                false,
                TypeClient.ENTREPRISE
        );
        PrixCalculable compositionControle = new DecorateurRemise(
                new SupplementUrgence(new SupplementDangereux(c -> 3_000)),
                strategies
        );

        assertEquals(4_972.50, compositionControle.calculer(contexte), DELTA);
        assertEquals(4_972.50, prix.calculer(contexte), DELTA);
    }

    @Test
    void l_assurance_s_ajoute_apres_la_remise_avec_un_minimum_de_quinze_euros() {
        PrixCalculable prix = new AssembleurPrix().assembler(strategiesAvecTarifRoute(100));
        ContexteTarification contexte = contexte(
                List.of(colis(TypeMarchandise.STANDARD, 100)),
                false,
                true,
                TypeClient.ENTREPRISE
        );

        assertEquals(105, prix.calculer(contexte), DELTA);
    }

    @Test
    void l_assurance_proportionnelle_s_ajoute_apres_la_remise() {
        PrixCalculable prix = new AssembleurPrix().assembler(strategiesAvecTarifRoute(100));
        ContexteTarification contexte = contexte(
                List.of(colis(TypeMarchandise.STANDARD, 1_000)),
                false,
                true,
                TypeClient.ENTREPRISE
        );

        assertEquals(110, prix.calculer(contexte), DELTA);
    }

    private static ContexteTarification contexte(List<Colis> colis, boolean urgent, boolean assurance,
                                                TypeClient typeClient) {
        return new ContexteTarification(colis, 0, ModeTransport.ROUTE, urgent, assurance, typeClient);
    }

    private static Colis colis(TypeMarchandise type, double valeurDeclaree) {
        return new Colis("Test", 0, 0, 0, 0, type, valeurDeclaree);
    }

    private static StrategiesTarification strategiesAvecTarifRoute(double prix) {
        TarifTransport tarifFixe = (distance, poids, volume) -> prix;
        return new StrategiesTarification(
                Map.of(
                        ModeTransport.ROUTE, tarifFixe,
                        ModeTransport.RAIL, tarifFixe,
                        ModeTransport.MER, tarifFixe,
                        ModeTransport.AIR, tarifFixe
                ),
                Map.of(
                        TypeClient.PARTICULIER, new RemiseParticulier(),
                        TypeClient.ENTREPRISE, new RemiseEntreprise(),
                        TypeClient.PREMIUM, new RemisePremium()
                )
        );
    }
}
