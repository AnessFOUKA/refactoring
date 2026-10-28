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
    private static final double FRAIS_ANNULATION_EN_COURS = 0.50;

    public static final double DEVIS_INDISPONIBLE = -1;
    private int compteur;

    private final ExpeditionRepository expeditions;
    private final CalculateurPrix calculateur;
    private final AffectationVehicule affectation;
    private final ValidateurExpedition validateur;
    private final EstimateurDelai estimateurDelai;
    private final Notificateur notificateur;

    public ServiceExpedition(ExpeditionRepository expeditions, CalculateurPrix calculateur,
                             AffectationVehicule affectation, ValidateurExpedition validateur,
                             EstimateurDelai estimateurDelai, Notificateur notificateur) {
        this.expeditions = expeditions;
        this.calculateur = calculateur;
        this.affectation = affectation;
        this.validateur = validateur;
        this.estimateurDelai = estimateurDelai;
        this.notificateur = notificateur;
    }

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

        expeditions.sauvegarder(e);
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
        Expedition e = expeditions.trouverParId(id).orElse(null);
        if (e == null || e.getStatut() != StatutExpedition.CREEE) {
            System.out.println("ERREUR : impossible de démarrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.EN_COURS);
        Vehicule v = e.getVehicule();
        v.setDisponible(false);
        v.partir(e.getDistance(), e.getColis());
        expeditions.mettreAJour(e);
        notificateur.notifier(e.getClient(), "Votre expédition " + e.getId() + " est en route",
                "Votre expédition a quitté " + e.getVilleDepart() + ".",
                "TransFret : expédition " + e.getId() + " partie de " + e.getVilleDepart());
        return true;
    }

    public boolean livrerExpedition(String id) {
        Expedition e = expeditions.trouverParId(id).orElse(null);
        if (e == null || e.getStatut() != StatutExpedition.EN_COURS) {
            System.out.println("ERREUR : impossible de livrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.LIVREE);
        libererVehicule(e);
        expeditions.mettreAJour(e);
        notificateur.notifier(e.getClient(), "Votre expédition " + e.getId() + " est livrée",
                "Votre expédition est arrivée à " + e.getVilleArrivee() + ". Merci de votre confiance.",
                "TransFret : expédition " + e.getId() + " livrée à " + e.getVilleArrivee());
        return true;
    }

    public boolean annulerExpedition(String id) {
        Expedition e = expeditions.trouverParId(id).orElse(null);
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
        expeditions.mettreAJour(e);
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
