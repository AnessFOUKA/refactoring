package fr.efrei.transfret;

public class SupplementRefrigere extends DecorateurPrix {

    private static final double COEFFICIENT = 1.20;

    public SupplementRefrigere(PrixCalculable composant) {
        super(composant);
    }

    @Override
    public double calculer(ContexteTarification contexte) {
        double prix = prixDeBase(contexte);
        return contexte.contient(TypeMarchandise.REFRIGERE) ? prix * COEFFICIENT : prix;
    }
}
