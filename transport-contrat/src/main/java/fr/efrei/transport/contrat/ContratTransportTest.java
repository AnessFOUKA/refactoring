package fr.efrei.transport.contrat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static fr.efrei.transport.contrat.ModeTransport.AIR;
import static fr.efrei.transport.contrat.ModeTransport.MER;
import static fr.efrei.transport.contrat.ModeTransport.RAIL;
import static fr.efrei.transport.contrat.ModeTransport.ROUTE;
import static fr.efrei.transport.contrat.TypeMarchandise.DANGEREUX;
import static fr.efrei.transport.contrat.TypeMarchandise.FRAGILE;
import static fr.efrei.transport.contrat.TypeMarchandise.REFRIGERE;
import static fr.efrei.transport.contrat.TypeMarchandise.STANDARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests d'acceptation de TransFret.
 * <p>
 * Ils décrivent le comportement attendu, indépendamment de l'implémentation.
 * Pour les exécuter sur une implémentation, créer une sous-classe qui fournit son adaptateur :
 * <pre>
 * class MonImplementationTest extends ContratTransportTest {
 *     protected SystemeTransport creerSysteme() { return new MonAdaptateur(); }
 * }
 * </pre>
 */
public abstract class ContratTransportTest {

    private static final double CENTIME = 0.001;
    private static final DateTimeFormatter JJ_MM_AAAA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    protected SystemeTransport systeme;
    private Locale localeOrigine;
    private final Map<DemandeClient, String> clientsCrees = new HashMap<>();

    protected abstract SystemeTransport creerSysteme();

    @BeforeEach
    void initialiser() {
        // les montants sont formatés avec la locale de la JVM : on la fixe pour des tests reproductibles
        localeOrigine = Locale.getDefault();
        Locale.setDefault(Locale.FRANCE);
        systeme = creerSysteme();
        systeme.reinitialiser();
    }

    @AfterEach
    void restaurerLocale() {
        Locale.setDefault(localeOrigine);
    }

    // =====================================================================
    // Jeu de données
    // =====================================================================

    static final DemandeClient DUPONT = new DemandeClient("Jean Dupont", "jean.dupont@mail.fr", "0601020304",
            "12 rue de la Paix, 75002 Paris", TypeClient.PARTICULIER, null);
    static final DemandeClient TRANSCO = new DemandeClient("Transco SA", "contact@transco.fr", "0144556677",
            "ZI des Chanteraines, 92390 Villeneuve", TypeClient.ENTREPRISE, "12345678900012");
    static final DemandeClient LOGIPLUS = new DemandeClient("LogiPlus", "ops@logiplus.com", "0788990011",
            "3 quai du Port, 13002 Marseille", TypeClient.PREMIUM, null);

    static ColisDto colis(double poids, TypeMarchandise type) {
        return new ColisDto("Colis " + type.name().toLowerCase(), poids, 50, 40, 30, type, 100);
    }

    static final ColisDto CARTONS_LIVRES = new ColisDto("Cartons livres", 35, 60, 40, 40, STANDARD, 300);
    static final ColisDto VAISSELLE = new ColisDto("Vaisselle", 12, 50, 50, 30, FRAGILE, 800);
    static final ColisDto FUTS_SOLVANT = new ColisDto("Fûts de solvant", 5000, 240, 120, 150, DANGEREUX, 20000);
    static final ColisDto PRODUITS_LAITIERS = new ColisDto("Produits laitiers", 3000, 240, 120, 180, REFRIGERE, 9000);
    static final ColisDto BOBINES_ACIER = new ColisDto("Bobines d'acier", 200000, 1200, 250, 250, STANDARD, 150000);
    static final ColisDto CONTENEUR = new ColisDto("Conteneur machines-outils", 15000, 1200, 235, 239, STANDARD, 250000);
    static final ColisDto BATTERIES = new ColisDto("Batteries lithium", 80, 80, 60, 50, DANGEREUX, 4000);
    static final ColisDto ECHANTILLONS = new ColisDto("Échantillons", 50, 100, 80, 60, FRAGILE, 1500);
    static final ColisDto PALETTE = new ColisDto("Palette", 800, 120, 80, 150, STANDARD, 2000);

    /** Crée le client au premier appel, puis renvoie toujours la même référence. */
    String client(DemandeClient demande) {
        return clientsCrees.computeIfAbsent(demande, d ->
                systeme.creerClient(d).orElseThrow(() -> new AssertionError("client refusé : " + d)));
    }

    String expedier(DemandeClient client, List<ColisDto> colis, String depart, String arrivee, ModeTransport mode,
                    boolean urgent, boolean assurance) {
        DemandeExpedition demande = new DemandeExpedition(client(client), colis, depart, arrivee, mode, urgent, assurance);
        return systeme.creerExpedition(demande).orElseThrow(() -> new AssertionError("expédition refusée : " + demande));
    }

    boolean estAcceptee(DemandeClient client, List<ColisDto> colis, String depart, String arrivee, ModeTransport mode,
                        boolean urgent, boolean assurance) {
        return systeme.creerExpedition(new DemandeExpedition(client(client), colis, depart, arrivee, mode, urgent, assurance)).isPresent();
    }

    EtatExpedition etat(String id) {
        return systeme.consulter(id).orElseThrow(() -> new AssertionError("expédition introuvable : " + id));
    }

    static String aujourdhui() {
        return LocalDate.now().format(JJ_MM_AAAA);
    }

    // =====================================================================

    @Nested
    @DisplayName("Création des clients")
    class Clients {

        @Test
        void un_particulier_valide_est_accepte() {
            assertTrue(systeme.creerClient(DUPONT).isPresent());
        }

        @Test
        void une_entreprise_avec_siret_est_acceptee() {
            assertTrue(systeme.creerClient(TRANSCO).isPresent());
        }

        @Test
        void un_client_premium_est_accepte() {
            assertTrue(systeme.creerClient(LOGIPLUS).isPresent());
        }

        @Test
        void le_nom_est_obligatoire() {
            assertTrue(systeme.creerClient(new DemandeClient("  ", "a@b.fr", "06", "adr", TypeClient.PARTICULIER, null)).isEmpty());
            assertTrue(systeme.creerClient(new DemandeClient(null, "a@b.fr", "06", "adr", TypeClient.PARTICULIER, null)).isEmpty());
        }

        @Test
        void l_email_doit_contenir_arobase_et_point() {
            assertTrue(systeme.creerClient(new DemandeClient("A", "pas-un-email", "06", "adr", TypeClient.PARTICULIER, null)).isEmpty());
            assertTrue(systeme.creerClient(new DemandeClient("A", "a@bfr", "06", "adr", TypeClient.PARTICULIER, null)).isEmpty());
            assertTrue(systeme.creerClient(new DemandeClient("A", null, "06", "adr", TypeClient.PARTICULIER, null)).isEmpty());
        }

        @Test
        void une_entreprise_doit_avoir_un_siret_de_14_caracteres() {
            assertTrue(systeme.creerClient(new DemandeClient("A", "a@b.fr", "06", "adr", TypeClient.ENTREPRISE, null)).isEmpty());
            assertTrue(systeme.creerClient(new DemandeClient("A", "a@b.fr", "06", "adr", TypeClient.ENTREPRISE, "1234567890001")).isEmpty());
        }

        @Test
        void un_email_de_bienvenue_est_envoye() {
            client(LOGIPLUS);
            assertEquals(List.of(new Notification(Canal.EMAIL, "ops@logiplus.com", "Bienvenue chez TransFret")),
                    systeme.notifications());
        }

        @Test
        void aucun_email_si_le_client_est_refuse() {
            systeme.creerClient(new DemandeClient("A", "pas-un-email", "06", "adr", TypeClient.PARTICULIER, null));
            assertTrue(systeme.notifications().isEmpty());
        }
    }

    @Nested
    @DisplayName("Tarification")
    class Tarification {

        @Test
        void route_particulier_avec_fragile_et_assurance() {
            EtatExpedition e = etat(expedier(DUPONT, List.of(CARTONS_LIVRES, VAISSELLE), "Paris", "Lyon", ROUTE, false, true));
            assertEquals(669.11, e.prixHT(), CENTIME);
            assertEquals(802.93, e.prixTTC(), CENTIME);
        }

        @Test
        void route_entreprise_dangereux_urgent_remise_10_pourcent() {
            EtatExpedition e = etat(expedier(TRANSCO, List.of(FUTS_SOLVANT), "Paris", "Marseille", ROUTE, true, false));
            assertEquals(2509.65, e.prixHT(), CENTIME);
            assertEquals(3011.58, e.prixTTC(), CENTIME);
        }

        @Test
        void route_premium_refrigere_avec_assurance() {
            EtatExpedition e = etat(expedier(LOGIPLUS, List.of(PRODUITS_LAITIERS), "Paris", "Lille", ROUTE, false, true));
            assertEquals(727.20, e.prixHT(), CENTIME);
            assertEquals(872.64, e.prixTTC(), CENTIME);
        }

        @Test
        void rail_entreprise_au_dela_de_5000_euros_remise_15_pourcent() {
            EtatExpedition e = etat(expedier(TRANSCO, List.of(BOBINES_ACIER), "Paris", "Lyon", RAIL, false, false));
            assertEquals(12216.20, e.prixHT(), CENTIME);
            assertEquals(14659.44, e.prixTTC(), CENTIME);
        }

        @Test
        void mer_premium_avec_assurance_proportionnelle() {
            EtatExpedition e = etat(expedier(LOGIPLUS, List.of(CONTENEUR), "Le Havre", "New York", MER, false, true));
            assertEquals(7700.00, e.prixHT(), CENTIME);
            assertEquals(9240.00, e.prixTTC(), CENTIME);
        }

        @Test
        void mer_minimum_de_500_euros() {
            EtatExpedition e = etat(expedier(DUPONT, List.of(colis(10, STANDARD)), "Marseille", "Alger", MER, false, false));
            assertEquals(500.00, e.prixHT(), CENTIME);
            assertEquals(600.00, e.prixTTC(), CENTIME);
        }

        @Test
        void air_au_poids_volumetrique_urgent_fragile_assure() {
            EtatExpedition e = etat(expedier(DUPONT, List.of(ECHANTILLONS), "Paris", "New York", AIR, true, true));
            assertEquals(6696.64, e.prixHT(), CENTIME);
            assertEquals(8035.97, e.prixTTC(), CENTIME);
        }

        @Test
        void air_au_poids_reel_quand_il_depasse_le_poids_volumetrique() {
            ColisDto dense = new ColisDto("Lingots", 100, 10, 10, 10, STANDARD, 100);
            EtatExpedition e = etat(expedier(DUPONT, List.of(dense), "Paris", "New York", AIR, false, false));
            assertEquals(3954.00, e.prixHT(), CENTIME);
            assertEquals(4744.80, e.prixTTC(), CENTIME);
        }

        @Test
        void les_supplements_s_appliquent_l_un_apres_l_autre() {
            EtatExpedition e = etat(expedier(DUPONT, List.of(colis(10, FRAGILE), colis(10, DANGEREUX)), "Paris", "Lille", ROUTE, false, false));
            assertEquals(406.64, e.prixHT(), CENTIME);
            assertEquals(487.97, e.prixTTC(), CENTIME);
        }

        @Test
        void l_assurance_coute_au_minimum_15_euros() {
            EtatExpedition e = etat(expedier(DUPONT, List.of(colis(10, STANDARD)), "Paris", "Lille", ROUTE, false, true));
            assertEquals(286.00, e.prixHT(), CENTIME);
            assertEquals(343.20, e.prixTTC(), CENTIME);
        }

        @Test
        void les_villes_ne_sont_pas_sensibles_a_la_casse_ni_au_sens() {
            EtatExpedition e = etat(expedier(DUPONT, List.of(colis(10, STANDARD)), "LILLE", "paris", ROUTE, false, false));
            assertEquals(271.00, e.prixHT(), CENTIME);
        }
    }

    @Nested
    @DisplayName("Devis")
    class Devis {

        @Test
        void devis_route_particulier() {
            assertEquals(938.40, systeme.devisTTC(new DemandeDevis(List.of(PALETTE), "Paris", "Bordeaux", ROUTE, false, false, TypeClient.PARTICULIER)).getAsDouble(), CENTIME);
        }

        @Test
        void devis_route_entreprise() {
            assertEquals(844.56, systeme.devisTTC(new DemandeDevis(List.of(PALETTE), "Paris", "Bordeaux", ROUTE, false, false, TypeClient.ENTREPRISE)).getAsDouble(), CENTIME);
        }

        @Test
        void devis_rail_premium_assure() {
            assertEquals(458.88, systeme.devisTTC(new DemandeDevis(List.of(PALETTE), "Paris", "Lyon", RAIL, false, true, TypeClient.PREMIUM)).getAsDouble(), CENTIME);
        }

        @Test
        void pas_de_devis_pour_un_trajet_inconnu() {
            assertTrue(systeme.devisTTC(new DemandeDevis(List.of(PALETTE), "Paris", "Tokyo", AIR, false, false, TypeClient.PARTICULIER)).isEmpty());
        }

        @Test
        void le_devis_est_egal_au_prix_de_l_expedition() {
            List<ColisDto> colis = List.of(CARTONS_LIVRES, VAISSELLE);
            double devis = systeme.devisTTC(new DemandeDevis(colis, "Paris", "Lyon", ROUTE, false, true, TypeClient.PARTICULIER)).getAsDouble();
            assertEquals(etat(expedier(DUPONT, colis, "Paris", "Lyon", ROUTE, false, true)).prixTTC(), devis, CENTIME);

            devis = systeme.devisTTC(new DemandeDevis(List.of(ECHANTILLONS), "Paris", "New York", AIR, true, true, TypeClient.PARTICULIER)).getAsDouble();
            assertEquals(etat(expedier(DUPONT, List.of(ECHANTILLONS), "Paris", "New York", AIR, true, true)).prixTTC(), devis, CENTIME);
        }

        @Test
        void un_devis_n_envoie_aucune_notification() {
            systeme.devisTTC(new DemandeDevis(List.of(PALETTE), "Paris", "Bordeaux", ROUTE, false, false, TypeClient.PARTICULIER));
            assertTrue(systeme.notifications().isEmpty());
        }
    }

    @Nested
    @DisplayName("Règles d'acceptation d'une expédition")
    class Contraintes {

        @Test
        void matieres_dangereuses_interdites_en_aerien() {
            assertFalse(estAcceptee(DUPONT, List.of(BATTERIES), "Paris", "New York", AIR, false, false));
        }

        @Test
        void refrigere_interdit_par_rail_et_par_air() {
            assertFalse(estAcceptee(DUPONT, List.of(colis(100, REFRIGERE)), "Paris", "Lyon", RAIL, false, false));
            assertFalse(estAcceptee(DUPONT, List.of(colis(100, REFRIGERE)), "Paris", "New York", AIR, false, false));
        }

        @Test
        void refrigere_accepte_par_mer() {
            assertTrue(estAcceptee(DUPONT, List.of(colis(100, REFRIGERE)), "Le Havre", "New York", MER, false, false));
        }

        @Test
        void pas_d_urgence_en_maritime() {
            assertFalse(estAcceptee(DUPONT, List.of(colis(100, STANDARD)), "Le Havre", "New York", MER, true, false));
        }

        @Test
        void trajet_inconnu_refuse() {
            assertFalse(estAcceptee(DUPONT, List.of(colis(100, STANDARD)), "Paris", "Nantes", ROUTE, false, false));
        }

        @Test
        void au_moins_un_colis() {
            assertFalse(estAcceptee(DUPONT, List.of(), "Paris", "Lyon", ROUTE, false, false));
        }

        @Test
        void poids_strictement_positif() {
            assertFalse(estAcceptee(DUPONT, List.of(colis(0, STANDARD)), "Paris", "Lyon", ROUTE, false, false));
        }

        @Test
        void client_inconnu_refuse() {
            assertTrue(systeme.creerExpedition(new DemandeExpedition("inconnu@nulle.part", List.of(colis(10, STANDARD)), "Paris", "Lyon", ROUTE, false, false)).isEmpty());
        }

        @Test
        void refuse_si_aucun_camion_assez_grand() {
            assertFalse(estAcceptee(DUPONT, List.of(colis(25000, STANDARD)), "Paris", "Lyon", ROUTE, false, false));
        }

        @Test
        void refuse_si_le_camion_frigorifique_est_trop_petit() {
            assertFalse(estAcceptee(DUPONT, List.of(colis(9000, REFRIGERE)), "Paris", "Lyon", ROUTE, false, false));
        }

        @Test
        void une_expedition_refusee_n_envoie_pas_de_notification() {
            String dupont = client(DUPONT);
            int avant = systeme.notifications().size();
            systeme.creerExpedition(new DemandeExpedition(dupont, List.of(BATTERIES), "Paris", "New York", AIR, false, false));
            assertEquals(avant, systeme.notifications().size());
        }
    }

    @Nested
    @DisplayName("Affectation des véhicules")
    class Affectation {

        @Test
        void route_standard_premier_camion() {
            assertEquals("AB-123-CD", etat(expedier(DUPONT, List.of(colis(100, STANDARD)), "Paris", "Lyon", ROUTE, false, false)).vehicule());
        }

        @Test
        void route_dangereux_camion_certifie_adr() {
            assertEquals("EF-456-GH", etat(expedier(DUPONT, List.of(colis(100, DANGEREUX)), "Paris", "Lyon", ROUTE, false, false)).vehicule());
        }

        @Test
        void route_refrigere_camion_frigorifique() {
            assertEquals("IJ-789-KL", etat(expedier(DUPONT, List.of(colis(100, REFRIGERE)), "Paris", "Lyon", ROUTE, false, false)).vehicule());
        }

        @Test
        void le_camion_frigorifique_n_est_pas_utilise_pour_le_reste() {
            expedier(DUPONT, List.of(colis(15000, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
            expedier(DUPONT, List.of(colis(11000, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
            assertFalse(estAcceptee(DUPONT, List.of(colis(5000, STANDARD)), "Paris", "Lyon", ROUTE, false, false));
        }

        @Test
        void un_camion_plein_passe_au_suivant() {
            assertEquals("AB-123-CD", etat(expedier(DUPONT, List.of(colis(15000, STANDARD)), "Paris", "Lyon", ROUTE, false, false)).vehicule());
            assertEquals("EF-456-GH", etat(expedier(DUPONT, List.of(colis(10000, STANDARD)), "Paris", "Lyon", ROUTE, false, false)).vehicule());
        }

        @Test
        void un_camion_peut_regrouper_plusieurs_expeditions() {
            assertEquals("AB-123-CD", etat(expedier(DUPONT, List.of(colis(5000, STANDARD)), "Paris", "Lyon", ROUTE, false, false)).vehicule());
            assertEquals("AB-123-CD", etat(expedier(DUPONT, List.of(colis(5000, STANDARD)), "Paris", "Lille", ROUTE, false, false)).vehicule());
        }

        @Test
        void un_camion_en_route_n_est_plus_disponible() {
            String id = expedier(DUPONT, List.of(colis(100, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
            systeme.demarrer(id);
            assertEquals("EF-456-GH", etat(expedier(DUPONT, List.of(colis(100, STANDARD)), "Paris", "Lyon", ROUTE, false, false)).vehicule());
        }

        @Test
        void un_camion_livre_redevient_disponible() {
            String id = expedier(DUPONT, List.of(colis(15000, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
            systeme.demarrer(id);
            systeme.livrer(id);
            assertEquals("AB-123-CD", etat(expedier(DUPONT, List.of(colis(15000, STANDARD)), "Paris", "Lyon", ROUTE, false, false)).vehicule());
        }

        @Test
        void une_annulation_libere_la_capacite() {
            String id = expedier(DUPONT, List.of(colis(15000, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
            systeme.annuler(id);
            assertEquals("AB-123-CD", etat(expedier(DUPONT, List.of(colis(15000, STANDARD)), "Paris", "Lyon", ROUTE, false, false)).vehicule());
        }

        @Test
        void rail_mer_air() {
            assertEquals("TR-0001", etat(expedier(DUPONT, List.of(colis(100, STANDARD)), "Paris", "Lyon", RAIL, false, false)).vehicule());
            assertEquals("CMA-CGM-01", etat(expedier(DUPONT, List.of(colis(100, STANDARD)), "Le Havre", "New York", MER, false, false)).vehicule());
            assertEquals("F-GTXA", etat(expedier(DUPONT, List.of(colis(100, STANDARD)), "Paris", "New York", AIR, false, false)).vehicule());
        }
    }

    @Nested
    @DisplayName("Délais de livraison")
    class Delais {

        int delai(String depart, String arrivee, ModeTransport mode, boolean urgent) {
            return etat(expedier(DUPONT, List.of(colis(10, STANDARD)), depart, arrivee, mode, urgent, false)).delaiJours();
        }

        @Test
        void route() {
            assertEquals(1, delai("Paris", "Lyon", ROUTE, false));
            assertEquals(2, delai("Paris", "Marseille", ROUTE, false));
            assertEquals(1, delai("Paris", "Marseille", ROUTE, true));
        }

        @Test
        void rail() {
            assertEquals(2, delai("Paris", "Lyon", RAIL, false));
            assertEquals(1, delai("Paris", "Lyon", RAIL, true));
        }

        @Test
        void mer() {
            assertEquals(11, delai("Le Havre", "New York", MER, false));
            assertEquals(4, delai("Marseille", "Alger", MER, false));
        }

        @Test
        void air() {
            assertEquals(2, delai("Paris", "New York", AIR, false));
            assertEquals(1, delai("Paris", "New York", AIR, true));
            assertEquals(1, delai("Paris", "Lyon", AIR, false));
        }
    }

    @Nested
    @DisplayName("Cycle de vie")
    class CycleDeVie {

        String expedition() {
            return expedier(DUPONT, List.of(colis(100, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
        }

        @Test
        void une_nouvelle_expedition_est_creee() {
            EtatExpedition e = etat(expedition());
            assertEquals(Statut.CREEE, e.statut());
            assertEquals(0, e.fraisAnnulation(), CENTIME);
        }

        @Test
        void demarrer_puis_livrer() {
            String id = expedition();
            assertTrue(systeme.demarrer(id));
            assertEquals(Statut.EN_COURS, etat(id).statut());
            assertTrue(systeme.livrer(id));
            assertEquals(Statut.LIVREE, etat(id).statut());
        }

        @Test
        void on_ne_livre_pas_une_expedition_non_partie() {
            String id = expedition();
            assertFalse(systeme.livrer(id));
            assertEquals(Statut.CREEE, etat(id).statut());
        }

        @Test
        void on_ne_demarre_pas_deux_fois() {
            String id = expedition();
            systeme.demarrer(id);
            assertFalse(systeme.demarrer(id));
        }

        @Test
        void on_ne_livre_pas_deux_fois() {
            String id = expedition();
            systeme.demarrer(id);
            systeme.livrer(id);
            assertFalse(systeme.livrer(id));
        }

        @Test
        void annulation_gratuite_avant_le_depart() {
            String id = expedition();
            assertTrue(systeme.annuler(id));
            assertEquals(Statut.ANNULEE, etat(id).statut());
            assertEquals(0, etat(id).fraisAnnulation(), CENTIME);
        }

        @Test
        void annulation_en_cours_facturee_50_pourcent_du_ttc() {
            String id = expedier(TRANSCO, List.of(FUTS_SOLVANT), "Paris", "Marseille", ROUTE, true, false);
            systeme.demarrer(id);
            assertTrue(systeme.annuler(id));
            assertEquals(1505.79, etat(id).fraisAnnulation(), CENTIME);
        }

        @Test
        void on_n_annule_pas_une_expedition_livree_ou_deja_annulee() {
            String livree = expedition();
            systeme.demarrer(livree);
            systeme.livrer(livree);
            assertFalse(systeme.annuler(livree));

            String annulee = expedition();
            systeme.annuler(annulee);
            assertFalse(systeme.annuler(annulee));
            assertFalse(systeme.demarrer(annulee));
        }

        @Test
        void expedition_inconnue() {
            assertFalse(systeme.demarrer("EXP-999-0000"));
            assertFalse(systeme.livrer("EXP-999-0000"));
            assertFalse(systeme.annuler("EXP-999-0000"));
            assertTrue(systeme.consulter("EXP-999-0000").isEmpty());
        }

        @Test
        void chaque_expedition_a_un_identifiant_unique() {
            String a = expedition();
            String b = expedition();
            assertFalse(a.equals(b));
        }
    }

    @Nested
    @DisplayName("Notifications")
    class Notifications {

        @Test
        void particulier_email_a_chaque_etape_sans_sms() {
            String id = expedier(DUPONT, List.of(colis(100, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
            systeme.demarrer(id);
            systeme.livrer(id);
            assertEquals(List.of(
                    new Notification(Canal.EMAIL, "jean.dupont@mail.fr", "Bienvenue chez TransFret"),
                    new Notification(Canal.EMAIL, "jean.dupont@mail.fr", "Confirmation de votre expédition " + id),
                    new Notification(Canal.EMAIL, "jean.dupont@mail.fr", "Votre expédition " + id + " est en route"),
                    new Notification(Canal.EMAIL, "jean.dupont@mail.fr", "Votre expédition " + id + " est livrée")
            ), systeme.notifications());
        }

        @Test
        void entreprise_pas_de_sms() {
            String id = expedier(TRANSCO, List.of(colis(100, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
            systeme.annuler(id);
            assertTrue(systeme.notifications().stream().noneMatch(n -> n.canal() == Canal.SMS));
            assertTrue(systeme.notifications().contains(
                    new Notification(Canal.EMAIL, "contact@transco.fr", "Annulation de votre expédition " + id)));
        }

        @Test
        void premium_recoit_aussi_des_sms() {
            String id = expedier(LOGIPLUS, List.of(colis(100, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
            String livraisonPrevue = LocalDate.now().plusDays(etat(id).delaiJours()).format(JJ_MM_AAAA);
            systeme.demarrer(id);
            systeme.livrer(id);
            List<Notification> sms = systeme.notifications().stream().filter(n -> n.canal() == Canal.SMS).toList();
            assertEquals(List.of(
                    new Notification(Canal.SMS, "0788990011", "TransFret : expédition " + id + " créée, livraison prévue le " + livraisonPrevue),
                    new Notification(Canal.SMS, "0788990011", "TransFret : expédition " + id + " partie de Paris"),
                    new Notification(Canal.SMS, "0788990011", "TransFret : expédition " + id + " livrée à Lyon")
            ), sms);
        }

        @Test
        void premium_sms_d_annulation() {
            String id = expedier(LOGIPLUS, List.of(colis(100, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
            systeme.annuler(id);
            assertTrue(systeme.notifications().contains(
                    new Notification(Canal.SMS, "0788990011", "TransFret : expédition " + id + " annulée")));
        }

        @Test
        void pas_de_notification_si_la_transition_est_refusee() {
            String id = expedier(LOGIPLUS, List.of(colis(100, STANDARD)), "Paris", "Lyon", ROUTE, false, false);
            int avant = systeme.notifications().size();
            systeme.livrer(id);
            assertEquals(avant, systeme.notifications().size());
        }
    }

    @Nested
    @DisplayName("Factures")
    class Factures {

        @Test
        void facture_texte_particulier() {
            String id = expedier(DUPONT, List.of(CARTONS_LIVRES, VAISSELLE), "Paris", "Lyon", ROUTE, false, true);
            assertEquals("""
                    ==============================================
                                  FACTURE TRANSFRET
                    ==============================================
                    Facture n° F-%s
                    Date : %s
                    Client : Jean Dupont
                    Adresse : 12 rue de la Paix, 75002 Paris
                    ----------------------------------------------
                    Trajet : Paris -> Lyon (465 km)
                    Mode : ROUTE
                    Colis :
                      - Cartons livres : 35.0 kg [Standard]
                      - Vaisselle : 12.0 kg [Fragile]
                    ----------------------------------------------
                    Total HT  : 669,11 €
                    TVA 20%%   : 133,82 €
                    Total TTC : 802,93 €
                    ==============================================
                    """.formatted(id, aujourdhui()), systeme.facture(id).orElseThrow());
        }

        @Test
        void facture_texte_entreprise_annulee_en_cours() {
            String id = expedier(TRANSCO, List.of(FUTS_SOLVANT), "Paris", "Marseille", ROUTE, true, false);
            systeme.demarrer(id);
            systeme.annuler(id);
            assertEquals("""
                    ==============================================
                                  FACTURE TRANSFRET
                    ==============================================
                    Facture n° F-%s
                    Date : %s
                    Client : Transco SA
                    SIRET : 12345678900012
                    Adresse : ZI des Chanteraines, 92390 Villeneuve
                    ----------------------------------------------
                    Trajet : Paris -> Marseille (775 km)
                    Mode : ROUTE (URGENT)
                    Colis :
                      - Fûts de solvant : 5000.0 kg [Dangereux]
                    ----------------------------------------------
                    EXPEDITION ANNULEE
                    Frais d'annulation TTC : 1505,79 €
                    ==============================================
                    """.formatted(id, aujourdhui()), systeme.facture(id).orElseThrow());
        }

        @Test
        void pas_de_facture_pour_une_expedition_inconnue() {
            assertTrue(systeme.facture("EXP-999-0000").isEmpty());
        }
    }
}
