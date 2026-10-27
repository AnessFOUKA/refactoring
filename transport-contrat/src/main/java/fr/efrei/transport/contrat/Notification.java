package fr.efrei.transport.contrat;

/**
 * @param destinataire adresse email ou numéro de téléphone
 * @param objet        sujet de l'email, ou texte du SMS
 */
public record Notification(Canal canal, String destinataire, String objet) {
}
