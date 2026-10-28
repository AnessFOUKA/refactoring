package fr.efrei.transfret;

public class SupplementHorsGabarit extends DecorateurPrix {

    private static final double DIMENSION_MAXIMALE_STANDARD_CM = 200;
    private static final double COEFFICIENT = 1.10;

    public SupplementHorsGabarit(PrixCalculable composant) {
        super(composant);
    }

    @Override
    public double calculer(ContexteTarification contexte) {
        double prix = prixDeBase(contexte);
        boolean horsGabarit = contexte.colis().stream().anyMatch(colis ->
                colis.getLongueur() > DIMENSION_MAXIMALE_STANDARD_CM
                        || colis.getLargeur() > DIMENSION_MAXIMALE_STANDARD_CM
                        || colis.getHauteur() > DIMENSION_MAXIMALE_STANDARD_CM
        );
        return horsGabarit ? prix * COEFFICIENT : prix;
    }
}
