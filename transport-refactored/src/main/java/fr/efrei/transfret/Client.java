package fr.efrei.transfret;

import java.util.Date;

public class Client {

    private String nom;
    private String email;
    private String telephone;
    private String adresse;
    private TypeClient type;
    private String siret; // seulement pour les entreprises (null sinon)
    private int nbExpeditions;
    private Date dateInscription;

    public Client() {
    }

    public Client(String nom, String email, String telephone, String adresse, TypeClient type, String siret) {
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.type = type;
        this.siret = siret;
        this.nbExpeditions = 0;
        this.dateInscription = new Date();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public TypeClient getType() {
        return type;
    }

    public void setType(TypeClient type) {
        this.type = type;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public int getNbExpeditions() {
        return nbExpeditions;
    }

    public void setNbExpeditions(int nbExpeditions) {
        this.nbExpeditions = nbExpeditions;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public String toString() {
        return nom + " (" + type + ")";
    }
}
