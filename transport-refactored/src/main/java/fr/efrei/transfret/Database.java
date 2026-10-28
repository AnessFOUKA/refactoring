package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Accès à la base de données (simulée en mémoire pour l'instant).
 */
public class Database {

    private static Database instance;

    public static Map<String, Expedition> expeditions = new HashMap<String, Expedition>();
    public static Map<String, Client> clients = new HashMap<String, Client>();

    private Database() {
        System.out.println("[DB] Connexion à jdbc:mysql://prod-db.transfret.local:3306/transfret");
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void executer(String sql) {
        System.out.println("[SQL] " + sql);
    }

    public void sauvegarderClient(Client c) {
        executer("INSERT INTO client (nom, email, telephone, type) VALUES ('" + c.getNom() + "', '" + c.getEmail() + "', '" + c.getTelephone() + "', '" + c.getType() + "')");
        clients.put(c.getEmail(), c);
    }

    public void sauvegarderExpedition(Expedition e) {
        executer("INSERT INTO expedition (id, client, depart, arrivee, mode, statut, prix_ttc) VALUES ('" + e.getId() + "', '" + e.getClient().getEmail() + "', '" + e.getVilleDepart() + "', '" + e.getVilleArrivee() + "', '" + e.getMode() + "', '" + e.getStatut() + "', " + e.getPrixTTC() + ")");
        expeditions.put(e.getId(), e);
    }

    public void mettreAJourStatut(Expedition e) {
        executer("UPDATE expedition SET statut = '" + e.getStatut() + "' WHERE id = '" + e.getId() + "'");
    }

    public Expedition trouverExpedition(String id) {
        executer("SELECT * FROM expedition WHERE id = '" + id + "'");
        return expeditions.get(id);
    }

    public List<Expedition> toutesLesExpeditions() {
        executer("SELECT * FROM expedition");
        return new ArrayList<Expedition>(expeditions.values());
    }
}
