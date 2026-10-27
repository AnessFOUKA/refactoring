package fr.efrei.transport.contrat;

import java.util.List;

public record DemandeDevis(List<ColisDto> colis, String villeDepart, String villeArrivee, ModeTransport mode,
                           boolean urgent, boolean assurance, TypeClient typeClient) {
}
