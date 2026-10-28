package fr.efrei.transfret;

import java.util.Date;

public class GenerateurFacture {

    private static final int TAUX_TVA_EN_POURCENT = 20;

    public String generer(Expedition expedition) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================\n");
        sb.append("              FACTURE TRANSFRET\n");
        sb.append("==============================================\n");
        sb.append("Facture n° F-" + expedition.getId() + "\n");
        sb.append("Date : " + Utils.formaterDate(new Date()) + "\n");
        sb.append("Client : " + expedition.getClient().getNom() + "\n");
        if (expedition.getClient().getType() == TypeClient.ENTREPRISE) {
            sb.append("SIRET : " + expedition.getClient().getSiret() + "\n");
        }
        sb.append("Adresse : " + expedition.getClient().getAdresse() + "\n");
        sb.append("----------------------------------------------\n");
        sb.append("Trajet : " + expedition.getVilleDepart() + " -> " + expedition.getVilleArrivee()
                + " (" + expedition.getDistance() + " km)\n");
        sb.append("Mode : " + expedition.getMode() + (expedition.isUrgent() ? " (URGENT)" : "") + "\n");
        sb.append("Colis :\n");
        for (Colis colis : expedition.getColis()) {
            sb.append("  - " + colis.getDescription() + " : " + colis.getPoids()
                    + " kg [" + colis.getLibelleType() + "]\n");
        }
        sb.append("----------------------------------------------\n");
        if (expedition.getStatut() == StatutExpedition.ANNULEE) {
            sb.append("EXPEDITION ANNULEE\n");
            sb.append("Frais d'annulation TTC : "
                    + String.format("%.2f", expedition.getFraisAnnulation()) + " €\n");
        } else {
            sb.append("Total HT  : " + String.format("%.2f", expedition.getPrixHT()) + " €\n");
            sb.append("TVA " + TAUX_TVA_EN_POURCENT + "%   : "
                    + String.format("%.2f", expedition.getPrixTTC() - expedition.getPrixHT()) + " €\n");
            sb.append("Total TTC : " + String.format("%.2f", expedition.getPrixTTC()) + " €\n");
        }
        sb.append("==============================================\n");
        return sb.toString();
    }
}
