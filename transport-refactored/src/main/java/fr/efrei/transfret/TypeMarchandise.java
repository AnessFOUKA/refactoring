package fr.efrei.transfret;

public enum TypeMarchandise {
    STANDARD("Standard"),
    FRAGILE("Fragile"),
    DANGEREUX("Dangereux"),
    REFRIGERE("Réfrigéré");

    private final String libelle;

    TypeMarchandise(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
