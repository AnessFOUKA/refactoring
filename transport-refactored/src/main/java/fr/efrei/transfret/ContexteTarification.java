package fr.efrei.transfret;

import java.util.List;

public record ContexteTarification(
        List<Colis> colis,
        int distanceKm,
        ModeTransport mode,
        boolean urgent,
        boolean assurance,
        TypeClient typeClient
) {
    public ContexteTarification {
        colis = List.copyOf(colis);
    }

    public double poidsTotalKg() {
        return colis.stream().mapToDouble(Colis::getPoids).sum();
    }

    public double volumeTotalMetresCubes() {
        return colis.stream().mapToDouble(Colis::getVolume).sum();
    }

    public double valeurDeclareeTotale() {
        return colis.stream().mapToDouble(Colis::getValeurDeclaree).sum();
    }

    public boolean contient(TypeMarchandise type) {
        return colis.stream().anyMatch(colisCourant -> colisCourant.getType() == type);
    }
}
