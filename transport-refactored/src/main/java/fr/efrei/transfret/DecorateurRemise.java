package fr.efrei.transfret;

public class DecorateurRemise extends DecorateurPrix {

    private final StrategiesTarification strategies;

    public DecorateurRemise(PrixCalculable composant, StrategiesTarification strategies) {
        super(composant);
        this.strategies = strategies;
    }

    @Override
    public double calculer(ContexteTarification contexte) {
        return strategies.remisePour(contexte.typeClient()).appliquer(prixDeBase(contexte));
    }
}
