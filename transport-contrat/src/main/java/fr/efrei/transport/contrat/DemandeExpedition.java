package fr.efrei.transport.contrat;

import java.util.List;

/** {@code refClient} est la référence renvoyée par {@link SystemeTransport#creerClient}. */
public record DemandeExpedition(String refClient, List<ColisDto> colis, String villeDepart, String villeArrivee,
                                ModeTransport mode, boolean urgent, boolean assurance) {
}
