package fr.efrei.transfret;

public class SupplementDangereux extends DecorateurPrix {

    private static final double COEFFICIENT = 1.30;

    public SupplementDangereux(PrixCalculable composant) {
        super(composant);
    }

    @Override
    public double calculer(ContexteTarification contexte) {
        double prix = prixDeBase(contexte);
        return contexte.contient(TypeMarchandise.DANGEREUX) ? prix * COEFFICIENT : prix;
    }
}
