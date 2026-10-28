package fr.efrei.transfret;

public class SupplementUrgence extends DecorateurPrix {

    private static final double COEFFICIENT = 1.50;

    public SupplementUrgence(PrixCalculable composant) {
        super(composant);
    }

    @Override
    public double calculer(ContexteTarification contexte) {
        double prix = prixDeBase(contexte);
        return contexte.urgent() ? prix * COEFFICIENT : prix;
    }
}
