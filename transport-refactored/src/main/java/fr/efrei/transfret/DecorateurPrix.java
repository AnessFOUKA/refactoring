package fr.efrei.transfret;

public abstract class DecorateurPrix implements PrixCalculable {

    private final PrixCalculable composant;

    protected DecorateurPrix(PrixCalculable composant) {
        this.composant = composant;
    }

    protected double prixDeBase(ContexteTarification contexte) {
        return composant.calculer(contexte);
    }
}
