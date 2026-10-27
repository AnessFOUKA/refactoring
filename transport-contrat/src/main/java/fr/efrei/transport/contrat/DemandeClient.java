package fr.efrei.transport.contrat;

/** {@code siret} vaut {@code null} pour les clients qui ne sont pas des entreprises. */
public record DemandeClient(String nom, String email, String telephone, String adresse, TypeClient type, String siret) {
}
