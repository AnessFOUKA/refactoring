package fr.efrei.transfret;

import java.util.Map;

public class StrategiesTarification {

    private final Map<ModeTransport, TarifTransport> tarifs;
    private final Map<TypeClient, PolitiqueRemise> remises;

    public StrategiesTarification(Map<ModeTransport, TarifTransport> tarifs,
                                  Map<TypeClient, PolitiqueRemise> remises) {
        this.tarifs = Map.copyOf(tarifs);
        this.remises = Map.copyOf(remises);
        for (ModeTransport mode : ModeTransport.values()) {
            if (!this.tarifs.containsKey(mode)) {
                throw new IllegalArgumentException("Tarif manquant pour le mode " + mode);
            }
        }
        for (TypeClient typeClient : TypeClient.values()) {
            if (!this.remises.containsKey(typeClient)) {
                throw new IllegalArgumentException("Politique de remise manquante pour " + typeClient);
            }
        }
    }

    public TarifTransport tarifPour(ModeTransport mode) {
        return tarifs.get(mode);
    }

    public PolitiqueRemise remisePour(TypeClient typeClient) {
        return remises.get(typeClient);
    }

    public static StrategiesTarification standard() {
        return new StrategiesTarification(
                Map.of(
                        ModeTransport.ROUTE, new TarifRoute(),
                        ModeTransport.RAIL, new TarifRail(),
                        ModeTransport.MER, new TarifMer(),
                        ModeTransport.AIR, new TarifAir()
                ),
                Map.of(
                        TypeClient.PARTICULIER, new RemiseParticulier(),
                        TypeClient.ENTREPRISE, new RemiseEntreprise(),
                        TypeClient.PREMIUM, new RemisePremium()
                )
        );
    }
}
