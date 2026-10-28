package fr.efrei.transfret;

public class Colis {

    public static final int STANDARD = 0;
    public static final int FRAGILE = 1;
    public static final int DANGEREUX = 2;
    public static final int REFRIGERE = 3;

    private String description;
    private double poids;        // en kg
    private double longueur;     // en cm
    private double largeur;      // en cm
    private double hauteur;      // en cm
    private int type;            // 0 = standard, 1 = fragile, 2 = dangereux, 3 = refrigere
    private double valeurDeclaree;

    public Colis(String description, double poids, double longueur, double largeur, double hauteur, int type, double valeurDeclaree) {
        this.description = description;
        this.poids = poids;
        this.longueur = longueur;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.type = type;
        this.valeurDeclaree = valeurDeclaree;
    }

    // volume en m3
    public double getVolume() {
        return (longueur * largeur * hauteur) / 1000000;
    }

    public String getLibelleType() {
        switch (type) {
            case 0:
                return "Standard";
            case 1:
                return "Fragile";
            case 2:
                return "Dangereux";
            case 3:
                return "Réfrigéré";
            default:
                return "Inconnu";
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPoids() {
        return poids;
    }

    public void setPoids(double poids) {
        this.poids = poids;
    }

    public double getLongueur() {
        return longueur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public double getLargeur() {
        return largeur;
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
    }

    public double getHauteur() {
        return hauteur;
    }

    public void setHauteur(double hauteur) {
        this.hauteur = hauteur;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getValeurDeclaree() {
        return valeurDeclaree;
    }

    public void setValeurDeclaree(double valeurDeclaree) {
        this.valeurDeclaree = valeurDeclaree;
    }
}
