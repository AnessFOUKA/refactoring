package fr.efrei.transport.contrat;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Point d'entrée unique utilisé par les tests de contrat.
 * <p>
 * Chaque implémentation (ancien code, nouveau code) fournit un adaptateur qui traduit ces appels
 * vers son propre modèle. Les tests ne dépendent donc d'aucune classe interne d'une implémentation.
 */
public interface SystemeTransport {

    /** Remet le système à l'état initial : flotte neuve, aucun client, aucune expédition, aucune notification. */
    void reinitialiser();

    /** @return une référence du client, ou vide si la demande est refusée */
    Optional<String> creerClient(DemandeClient demande);

    /** @return l'identifiant de l'expédition, ou vide si la demande est refusée */
    Optional<String> creerExpedition(DemandeExpedition demande);

    Optional<EtatExpedition> consulter(String idExpedition);

    /** @return le montant TTC, ou vide si le trajet est inconnu */
    OptionalDouble devisTTC(DemandeDevis demande);

    boolean demarrer(String idExpedition);

    boolean livrer(String idExpedition);

    boolean annuler(String idExpedition);

    Optional<String> facture(String idExpedition);

    /** Toutes les notifications envoyées depuis la dernière réinitialisation, dans l'ordre d'envoi par canal. */
    List<Notification> notifications();
}
