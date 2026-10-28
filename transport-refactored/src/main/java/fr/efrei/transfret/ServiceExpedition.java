package fr.efrei.transfret;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ServiceExpedition {

    private static final int PREMIER_NUMERO_ALEATOIRE_EXPEDITION = 1_000;
    private static final int PLAGE_NUMEROS_ALEATOIRES_EXPEDITION = 9_000;
    private static final double TAUX_TVA = 1.20;
    private static final int JOURS_CALENDAIRES = Calendar.DAY_OF_MONTH;
    private static final double KILOMETRES_PAR_MILLE_NAUTIQUE = 1.852;
    private static final int DISTANCE_ROULAGE_AVION_KM = 2;
    private static final int ALTITUDE_CROISIERE_PIEDS = 35_000;
    private static final double TEMPERATURE_REFRIGERATION_CELSIUS = 4;
    private static final double FRAIS_ANNULATION_EN_COURS = 0.50;

    public static int compteur = 0;
    public static final double DEVIS_INDISPONIBLE = -1;

    private Database db = Database.getInstance();
    private CalculateurPrix calculateur = new CalculateurPrix();
    private AffectationVehicule affectation = new AffectationVehicule();
    private ValidateurExpedition validateur = new ValidateurExpedition();
    private EstimateurDelai estimateurDelai = new EstimateurDelai();
    private Notificateur notificateur = new Notificateur();

    public Expedition creerExpedition(Client client, List<Colis> colis, String villeDepart, String villeArrivee,
                                      ModeTransport mode, boolean urgent, boolean assurance) {
        if (afficherErreur(validateur.valider(client, colis, mode, urgent))) {
            return null;
        }
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (afficherErreur(validateur.validerTrajet(distance, villeDepart, villeArrivee))) {
            return null;
        }

        Vehicule v = affectation.trouverVehicule(colis, mode);
        if (v == null) {
            System.out.println("ERREUR : aucun véhicule disponible pour cette expédition");
            return null;
        }
        for (Colis c : colis) {
            v.charger(c);
        }

        Expedition e = new Expedition();
        compteur++;
        e.setId("EXP-" + compteur + "-" + (PREMIER_NUMERO_ALEATOIRE_EXPEDITION
                + new Random().nextInt(PLAGE_NUMEROS_ALEATOIRES_EXPEDITION)));
        e.setClient(client);
        e.setColis(colis);
        e.setVilleDepart(villeDepart);
        e.setVilleArrivee(villeArrivee);
        e.setDistance(distance);
        e.setMode(mode);
        e.setUrgent(urgent);
        e.setAssurance(assurance);
        e.setStatut(StatutExpedition.CREEE);
        e.setVehicule(v);
        e.setPrixHT(calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, client.getType()));
        e.setPrixTTC(Utils.arrondir(e.getPrixHT() * TAUX_TVA));
        e.setDateCreation(new Date());
        Calendar dateLivraisonPrevue = Calendar.getInstance();
        dateLivraisonPrevue.setTime(e.getDateCreation());
        dateLivraisonPrevue.add(JOURS_CALENDAIRES, estimateurDelai.estimer(distance, mode, urgent));
        e.setDateLivraisonPrevue(dateLivraisonPrevue.getTime());

        db.sauvegarderExpedition(e);
        notificateur.notifier(client, "Confirmation de votre expédition " + e.getId(),
                "Votre expédition " + villeDepart + " -> " + villeArrivee + " (" + mode + ") a été enregistrée. Montant : " + e.getPrixTTC() + " € TTC.",
                "TransFret : expédition " + e.getId() + " créée, livraison prévue le " + Utils.formaterDate(e.getDateLivraisonPrevue()));
        return e;
    }

    // retourne le prix TTC, -1 si le trajet est inconnu
    public double calculerDevis(List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode,
                                boolean urgent, boolean assurance, TypeClient typeClient) {
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == Utils.DISTANCE_INCONNUE) {
            return DEVIS_INDISPONIBLE;
        }
        double ht = calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, typeClient);
        return Utils.arrondir(ht * TAUX_TVA);
    }

    public boolean demarrerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || e.getStatut() != StatutExpedition.CREEE) {
            System.out.println("ERREUR : impossible de démarrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.EN_COURS);
        Vehicule v = e.getVehicule();
        v.setDisponible(false);
        try {
            v.faireLePlein();
        } catch (Exception ex) {
        }
        if (e.getMode() == ModeTransport.ROUTE || e.getMode() == ModeTransport.RAIL) {
            v.rouler(e.getDistance());
        } else if (e.getMode() == ModeTransport.MER) {
            v.naviguer((int) (e.getDistance() / KILOMETRES_PAR_MILLE_NAUTIQUE));
        } else if (e.getMode() == ModeTransport.AIR) {
            v.rouler(DISTANCE_ROULAGE_AVION_KM);
            v.voler(ALTITUDE_CROISIERE_PIEDS);
        }
        for (Colis c : e.getColis()) {
            if (c.getType() == TypeMarchandise.REFRIGERE) {
                v.activerRefrigeration(TEMPERATURE_REFRIGERATION_CELSIUS);
                break;
            }
        }
        db.mettreAJourStatut(e);
        notificateur.notifier(e.getClient(), "Votre expédition " + e.getId() + " est en route",
                "Votre expédition a quitté " + e.getVilleDepart() + ".",
                "TransFret : expédition " + e.getId() + " partie de " + e.getVilleDepart());
        return true;
    }

    public boolean livrerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || e.getStatut() != StatutExpedition.EN_COURS) {
            System.out.println("ERREUR : impossible de livrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.LIVREE);
        libererVehicule(e);
        db.mettreAJourStatut(e);
        notificateur.notifier(e.getClient(), "Votre expédition " + e.getId() + " est livrée",
                "Votre expédition est arrivée à " + e.getVilleArrivee() + ". Merci de votre confiance.",
                "TransFret : expédition " + e.getId() + " livrée à " + e.getVilleArrivee());
        return true;
    }

    public boolean annulerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || e.getStatut() == StatutExpedition.LIVREE || e.getStatut() == StatutExpedition.ANNULEE) {
            System.out.println("ERREUR : impossible d'annuler l'expédition " + id);
            return false;
        }
        if (e.getStatut() == StatutExpedition.EN_COURS) {
            // 50% de frais si déjà partie
            e.setFraisAnnulation(Utils.arrondir(e.getPrixTTC() * FRAIS_ANNULATION_EN_COURS));
        }
        e.setStatut(StatutExpedition.ANNULEE);
        libererVehicule(e);
        db.mettreAJourStatut(e);
        notificateur.notifier(e.getClient(), "Annulation de votre expédition " + e.getId(),
                "Votre expédition a été annulée. Frais d'annulation : " + e.getFraisAnnulation() + " €",
                "TransFret : expédition " + e.getId() + " annulée");
        return true;
    }

    private void libererVehicule(Expedition e) {
        for (Colis c : e.getColis()) {
            e.getVehicule().decharger(c);
        }
        e.getVehicule().setDisponible(true);
    }

    private boolean afficherErreur(Optional<String> erreur) {
        erreur.ifPresent(System.out::println);
        return erreur.isPresent();
    }
}
