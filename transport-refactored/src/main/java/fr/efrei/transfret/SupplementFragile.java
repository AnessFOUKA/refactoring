package fr.efrei.transfret;

public class SupplementFragile extends DecorateurPrix {

    private static final double COEFFICIENT = 1.15;

    public SupplementFragile(PrixCalculable composant) {
        super(composant);
    }

    @Override
    public double calculer(ContexteTarification contexte) {
        double prix = prixDeBase(contexte);
        return contexte.contient(TypeMarchandise.FRAGILE) ? prix * COEFFICIENT : prix;
    }
}
