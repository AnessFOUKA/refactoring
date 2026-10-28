package fr.efrei.transfret;

@FunctionalInterface
public interface PrixCalculable {

    double calculer(ContexteTarification contexte);
}
