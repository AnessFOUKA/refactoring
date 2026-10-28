package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Expedition {

    private String id;
    private Client client;
    private List<Colis> colis = new ArrayList<Colis>();
    private String villeDepart;
    private String villeArrivee;
    private int distance;
    private String mode;
    private boolean urgent;
    private boolean assurance;
    private String statut;
    private Date dateCreation;
    private Date dateLivraisonPrevue;
    private Vehicule vehicule;
    private double prixHT;
    private double prixTTC;
    private double fraisAnnulation;
    private List<String> historique = new ArrayList<String>();

    public double getPoidsTotal() {
        double total = 0;
        for (int i = 0; i < colis.size(); i++) {
            total = total + colis.get(i).getPoids();
        }
        return total;
    }

    public double getVolumeTotal() {
        double total = 0;
        for (int i = 0; i < colis.size(); i++) {
            Colis c = colis.get(i);
            total = total + (c.getLongueur() * c.getLargeur() * c.getHauteur()) / 1000000;
        }
        return total;
    }

    public double getValeurTotale() {
        double total = 0;
        for (int i = 0; i < colis.size(); i++) {
            total = total + colis.get(i).getValeurDeclaree();
        }
        return total;
    }

    public String genererFacture() {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================\n");
        sb.append("              FACTURE TRANSFRET\n");
        sb.append("==============================================\n");
        sb.append("Facture n° F-" + id + "\n");
        sb.append("Date : " + Utils.formaterDate(new Date()) + "\n");
        sb.append("Client : " + client.getNom() + "\n");
        if (client.getType().equals("ENTREPRISE")) {
            sb.append("SIRET : " + client.getSiret() + "\n");
        }
        sb.append("Adresse : " + client.getAdresse() + "\n");
        sb.append("----------------------------------------------\n");
        sb.append("Trajet : " + villeDepart + " -> " + villeArrivee + " (" + distance + " km)\n");
        sb.append("Mode : " + mode + (urgent ? " (URGENT)" : "") + "\n");
        sb.append("Colis :\n");
        for (Colis c : colis) {
            sb.append("  - " + c.getDescription() + " : " + c.getPoids() + " kg [" + c.getLibelleType() + "]\n");
        }
        sb.append("----------------------------------------------\n");
        if (statut.equals("ANNULEE")) {
            sb.append("EXPEDITION ANNULEE\n");
            sb.append("Frais d'annulation TTC : " + String.format("%.2f", fraisAnnulation) + " €\n");
        } else {
            sb.append("Total HT  : " + String.format("%.2f", prixHT) + " €\n");
            sb.append("TVA 20%   : " + String.format("%.2f", prixTTC - prixHT) + " €\n");
            sb.append("Total TTC : " + String.format("%.2f", prixTTC) + " €\n");
        }
        sb.append("==============================================\n");
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Colis> getColis() {
        return colis;
    }

    public void setColis(List<Colis> colis) {
        this.colis = colis;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public void setVilleDepart(String villeDepart) {
        this.villeDepart = villeDepart;
    }

    public String getVilleArrivee() {
        return villeArrivee;
    }

    public void setVilleArrivee(String villeArrivee) {
        this.villeArrivee = villeArrivee;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public boolean isAssurance() {
        return assurance;
    }

    public void setAssurance(boolean assurance) {
        this.assurance = assurance;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateLivraisonPrevue() {
        return dateLivraisonPrevue;
    }

    public void setDateLivraisonPrevue(Date dateLivraisonPrevue) {
        this.dateLivraisonPrevue = dateLivraisonPrevue;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public double getPrixHT() {
        return prixHT;
    }

    public void setPrixHT(double prixHT) {
        this.prixHT = prixHT;
    }

    public double getPrixTTC() {
        return prixTTC;
    }

    public void setPrixTTC(double prixTTC) {
        this.prixTTC = prixTTC;
    }

    public double getFraisAnnulation() {
        return fraisAnnulation;
    }

    public void setFraisAnnulation(double fraisAnnulation) {
        this.fraisAnnulation = fraisAnnulation;
    }

    public List<String> getHistorique() {
        return historique;
    }
}
