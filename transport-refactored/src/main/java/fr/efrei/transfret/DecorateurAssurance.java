package fr.efrei.transfret;

public class DecorateurAssurance extends DecorateurPrix {

    private static final double TAUX_ASSURANCE = 0.02;
    private static final double ASSURANCE_MINIMALE = 15;

    public DecorateurAssurance(PrixCalculable composant) {
        super(composant);
    }

    @Override
    public double calculer(ContexteTarification contexte) {
        double prix = prixDeBase(contexte);
        if (!contexte.assurance()) {
            return prix;
        }
        double montantAssurance = Math.max(
                contexte.valeurDeclareeTotale() * TAUX_ASSURANCE,
                ASSURANCE_MINIMALE
        );
        return prix + montantAssurance;
    }
}
