package fr.efrei.transfret;

import fr.efrei.transport.contrat.DemandeClient;
import fr.efrei.transport.contrat.DemandeDevis;
import fr.efrei.transport.contrat.DemandeExpedition;
import fr.efrei.transport.contrat.EtatExpedition;
import fr.efrei.transport.contrat.Notification;
import fr.efrei.transport.contrat.SystemeTransport;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;


public class NouveauSystemeAdapter implements SystemeTransport {

    @Override
    public void reinitialiser() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Optional<String> creerClient(DemandeClient demande) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Optional<String> creerExpedition(DemandeExpedition demande) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Optional<EtatExpedition> consulter(String idExpedition) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public OptionalDouble devisTTC(DemandeDevis demande) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean demarrer(String idExpedition) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean livrer(String idExpedition) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean annuler(String idExpedition) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Optional<String> facture(String idExpedition) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<Notification> notifications() {
        throw new UnsupportedOperationException("TODO");
    }
}
