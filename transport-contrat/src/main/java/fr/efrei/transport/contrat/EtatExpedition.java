package fr.efrei.transport.contrat;

/**
 * Vue d'une expédition telle que l'observent les tests.
 *
 * @param delaiJours   nombre de jours entre la création et la livraison prévue
 * @param vehicule     immatriculation du véhicule affecté
 */
public record EtatExpedition(String id, Statut statut, double prixHT, double prixTTC, int delaiJours,
                             String vehicule, double fraisAnnulation) {
}
