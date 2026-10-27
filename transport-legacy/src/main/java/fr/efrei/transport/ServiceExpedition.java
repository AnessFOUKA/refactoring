package fr.efrei.transport;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ServiceExpedition {

    public static int compteur = 0;

    private Database db = Database.getInstance();
    private CalculateurPrix calculateur = new CalculateurPrix();
    private AffectationVehicule affectation = new AffectationVehicule();

    public Expedition creerExpedition(Client client, List<Colis> colis, String villeDepart, String villeArrivee, String mode, boolean urgent, boolean assurance) {
        if (client == null) {
            System.out.println("ERREUR : client obligatoire");
            return null;
        }
        if (colis == null || colis.isEmpty()) {
            System.out.println("ERREUR : au moins un colis est nécessaire");
            return null;
        }
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            if (c.getPoids() <= 0) {
                System.out.println("ERREUR : poids invalide pour " + c.getDescription());
                return null;
            }
            if (c.getType() == 2) dangereux = true;
            if (c.getType() == 3) refrigere = true;
        }
        if (dangereux && mode.equals("AIR")) {
            System.out.println("ERREUR : les matières dangereuses sont interdites en transport aérien");
            return null;
        }
        if (refrigere && !(mode.equals("ROUTE") || mode.equals("MER"))) {
            System.out.println("ERREUR : le transport réfrigéré n'est possible que par route ou par mer");
            return null;
        }
        if (urgent && mode.equals("MER")) {
            System.out.println("ERREUR : pas d'option urgente pour le transport maritime");
            return null;
        }
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == -1) {
            System.out.println("ERREUR : trajet inconnu " + villeDepart + " -> " + villeArrivee);
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
        e.setId("EXP-" + compteur + "-" + (1000 + new Random().nextInt(9000)));
        e.setClient(client);
        e.setColis(colis);
        e.setVilleDepart(villeDepart);
        e.setVilleArrivee(villeArrivee);
        e.setDistance(distance);
        e.setMode(mode);
        e.setUrgent(urgent);
        e.setAssurance(assurance);
        e.setStatut("CREEE");
        e.setVehicule(v);
        e.setPrixHT(calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, client.getType()));
        e.setPrixTTC(Math.round(e.getPrixHT() * 1.2 * 100.0) / 100.0);
        e.setDateCreation(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(e.getDateCreation());
        cal.add(Calendar.DAY_OF_MONTH, calculerDelai(distance, mode, urgent));
        e.setDateLivraisonPrevue(cal.getTime());

        db.sauvegarderExpedition(e);
        notifier(client, "Confirmation de votre expédition " + e.getId(),
                "Votre expédition " + villeDepart + " -> " + villeArrivee + " (" + mode + ") a été enregistrée. Montant : " + e.getPrixTTC() + " € TTC.",
                "TransFret : expédition " + e.getId() + " créée, livraison prévue le " + Utils.formaterDate(e.getDateLivraisonPrevue()));
        return e;
    }

    // retourne le prix TTC, -1 si le trajet est inconnu
    public double calculerDevis(List<Colis> colis, String villeDepart, String villeArrivee, String mode, boolean urgent, boolean assurance, String typeClient) {
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == -1) {
            return -1;
        }
        double ht = calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, typeClient);
        return Math.round(ht * 1.2 * 100.0) / 100.0;
    }

    // retourne le délai de livraison en jours
    public int calculerDelai(int distance, String mode, boolean urgent) {
        int jours = 0;
        switch (mode) {
            case "ROUTE":
                double heures = distance / 70.0;
                if (urgent) {
                    jours = (int) Math.ceil(heures / 18);
                } else {
                    jours = (int) Math.ceil(heures / 9);
                }
                break;
            case "RAIL":
                jours = (int) Math.ceil(distance / 1440.0) + 1;
                if (urgent) jours = jours - 1;
                break;
            case "MER":
                jours = (int) Math.ceil(distance / 720.0) + 2;
                break;
            case "AIR":
                jours = 1;
                if (distance > 3000) jours = 2;
                if (urgent) jours = 1;
                break;
        }
        if (jours < 1) jours = 1;
        return jours;
    }

    public boolean demarrerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || !e.getStatut().equals("CREEE")) {
            System.out.println("ERREUR : impossible de démarrer l'expédition " + id);
            return false;
        }
        e.setStatut("EN_COURS");
        Vehicule v = e.getVehicule();
        v.setDisponible(false);
        try {
            v.faireLePlein();
        } catch (Exception ex) {
        }
        if (e.getMode().equals("ROUTE") || e.getMode().equals("RAIL")) {
            v.rouler(e.getDistance());
        } else if (e.getMode().equals("MER")) {
            v.naviguer((int) (e.getDistance() / 1.852));
        } else if (e.getMode().equals("AIR")) {
            v.rouler(2);
            v.voler(35000);
        }
        for (Colis c : e.getColis()) {
            if (c.getType() == 3) {
                v.activerRefrigeration(4);
                break;
            }
        }
        db.mettreAJourStatut(e);
        notifier(e.getClient(), "Votre expédition " + e.getId() + " est en route",
                "Votre expédition a quitté " + e.getVilleDepart() + ".",
                "TransFret : expédition " + e.getId() + " partie de " + e.getVilleDepart());
        return true;
    }

    public boolean livrerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || !e.getStatut().equals("EN_COURS")) {
            System.out.println("ERREUR : impossible de livrer l'expédition " + id);
            return false;
        }
        e.setStatut("LIVREE");
        libererVehicule(e);
        db.mettreAJourStatut(e);
        notifier(e.getClient(), "Votre expédition " + e.getId() + " est livrée",
                "Votre expédition est arrivée à " + e.getVilleArrivee() + ". Merci de votre confiance.",
                "TransFret : expédition " + e.getId() + " livrée à " + e.getVilleArrivee());
        return true;
    }

    public boolean annulerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || e.getStatut().equals("LIVREE") || e.getStatut().equals("ANNULEE")) {
            System.out.println("ERREUR : impossible d'annuler l'expédition " + id);
            return false;
        }
        if (e.getStatut().equals("EN_COURS")) {
            // 50% de frais si déjà partie
            e.setFraisAnnulation(Math.round(e.getPrixTTC() * 0.5 * 100.0) / 100.0);
        }
        e.setStatut("ANNULEE");
        libererVehicule(e);
        db.mettreAJourStatut(e);
        notifier(e.getClient(), "Annulation de votre expédition " + e.getId(),
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

    private void notifier(Client client, String sujet, String corps, String sms) {
        EmailService.envoyer(client.getEmail(), sujet, corps);
        if (client.getType().equals("PREMIUM")) {
            new SmsService().envoyerSms(client.getTelephone(), sms);
        }
    }
}
