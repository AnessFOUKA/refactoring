package fr.efrei.transfret;

import java.util.List;
import java.util.Optional;

public class ValidateurExpedition {

    public Optional<String> valider(Client client, List<Colis> colis, ModeTransport mode, boolean urgent) {
        if (client == null) {
            return Optional.of("ERREUR : client obligatoire");
        }
        if (colis == null || colis.isEmpty()) {
            return Optional.of("ERREUR : au moins un colis est nécessaire");
        }

        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis colisCourant : colis) {
            if (colisCourant.getPoids() <= 0) {
                return Optional.of("ERREUR : poids invalide pour " + colisCourant.getDescription());
            }
            if (colisCourant.getType() == TypeMarchandise.DANGEREUX) {
                dangereux = true;
            }
            if (colisCourant.getType() == TypeMarchandise.REFRIGERE) {
                refrigere = true;
            }
        }
        if (dangereux && mode == ModeTransport.AIR) {
            return Optional.of("ERREUR : les matières dangereuses sont interdites en transport aérien");
        }
        if (refrigere && mode == ModeTransport.FLUVIAL) {
            return Optional.of("ERREUR : le transport réfrigéré n'est pas possible par voie fluviale");
        }
        if (urgent && mode == ModeTransport.FLUVIAL) {
            return Optional.of("ERREUR : pas d'option urgente pour le transport fluvial");
        }
        if (refrigere && mode != ModeTransport.ROUTE && mode != ModeTransport.MER) {
            return Optional.of("ERREUR : le transport réfrigéré n'est possible que par route ou par mer");
        }
        if (urgent && mode == ModeTransport.MER) {
            return Optional.of("ERREUR : pas d'option urgente pour le transport maritime");
        }
        return Optional.empty();
    }

    public Optional<String> validerTrajet(int distance, String villeDepart, String villeArrivee) {
        if (distance == Utils.DISTANCE_INCONNUE) {
            return Optional.of("ERREUR : trajet inconnu " + villeDepart + " -> " + villeArrivee);
        }
        return Optional.empty();
    }
}
