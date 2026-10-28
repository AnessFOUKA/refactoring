package fr.efrei.transfret;

public class Colis {

    private static final double CENTIMETRES_CUBES_PAR_METRE_CUBE = 1_000_000;

    private String description;
    private double poids;        // en kg
    private double longueur;     // en cm
    private double largeur;      // en cm
    private double hauteur;      // en cm
    private TypeMarchandise type;
    private double valeurDeclaree;

    public Colis(String description, double poids, double longueur, double largeur, double hauteur, TypeMarchandise type, double valeurDeclaree) {
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
        return (longueur * largeur * hauteur) / CENTIMETRES_CUBES_PAR_METRE_CUBE;
    }

    public String getLibelleType() {
        return type.getLibelle();
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

    public TypeMarchandise getType() {
        return type;
    }

    public void setType(TypeMarchandise type) {
        this.type = type;
    }

    public double getValeurDeclaree() {
        return valeurDeclaree;
    }

    public void setValeurDeclaree(double valeurDeclaree) {
        this.valeurDeclaree = valeurDeclaree;
    }
}
