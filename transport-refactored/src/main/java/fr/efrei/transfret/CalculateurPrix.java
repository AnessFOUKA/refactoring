package fr.efrei.transfret;

import java.util.List;

public class CalculateurPrix {

    private final PrixCalculable calcul;

    public CalculateurPrix(StrategiesTarification strategies) {
        this.calcul = new AssembleurPrix().assembler(strategies);
    }

    public double calculerPrixHT(List<Colis> colis, int distance, ModeTransport mode, boolean urgent,
                                 boolean assurance, TypeClient typeClient) {
        ContexteTarification contexte = new ContexteTarification(
                colis, distance, mode, urgent, assurance, typeClient
        );
        return Utils.arrondir(calcul.calculer(contexte));
    }
}
