package fr.efrei.transport;

import fr.efrei.transport.contrat.Canal;
import fr.efrei.transport.contrat.ColisDto;
import fr.efrei.transport.contrat.DemandeClient;
import fr.efrei.transport.contrat.DemandeDevis;
import fr.efrei.transport.contrat.DemandeExpedition;
import fr.efrei.transport.contrat.EtatExpedition;
import fr.efrei.transport.contrat.Notification;
import fr.efrei.transport.contrat.Statut;
import fr.efrei.transport.contrat.SystemeTransport;
import fr.efrei.transport.contrat.TypeMarchandise;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * Branche le code historique sur les tests de contrat.
 * <p>
 * Tout le « bricolage » nécessaire pour tester l'ancien code (état statique à vider,
 * conversion des codes {@code String}/{@code int}…) est confiné ici.
 */
public class LegacyAdapter implements SystemeTransport {

    private ServiceClient serviceClient;
    private ServiceExpedition serviceExpedition;

    @Override
    public void reinitialiser() {
        Database.getInstance();
        Database.expeditions.clear();
        Database.clients.clear();
        EmailService.journal.clear();
        SmsService.journal.clear();
        ServiceExpedition.compteur = 0;
        Flotte.initialiser();
        serviceClient = new ServiceClient();
        serviceExpedition = new ServiceExpedition();
    }

    @Override
    public Optional<String> creerClient(DemandeClient d) {
        Client client = serviceClient.creerClient(d.nom(), d.email(), d.telephone(), d.adresse(), d.type().name(), d.siret());
        return Optional.ofNullable(client).map(Client::getEmail);
    }

    @Override
    public Optional<String> creerExpedition(DemandeExpedition d) {
        Client client = Database.clients.get(d.refClient());
        Expedition expedition = serviceExpedition.creerExpedition(client, versColis(d.colis()), d.villeDepart(),
                d.villeArrivee(), d.mode().name(), d.urgent(), d.assurance());
        return Optional.ofNullable(expedition).map(Expedition::getId);
    }

    @Override
    public Optional<EtatExpedition> consulter(String id) {
        Expedition e = Database.expeditions.get(id);
        if (e == null) {
            return Optional.empty();
        }
        int delai = (int) ChronoUnit.DAYS.between(jour(e.getDateCreation()), jour(e.getDateLivraisonPrevue()));
        return Optional.of(new EtatExpedition(e.getId(), Statut.valueOf(e.getStatut()), e.getPrixHT(), e.getPrixTTC(),
                delai, e.getVehicule().getImmatriculation(), e.getFraisAnnulation()));
    }

    @Override
    public OptionalDouble devisTTC(DemandeDevis d) {
        double devis = serviceExpedition.calculerDevis(versColis(d.colis()), d.villeDepart(), d.villeArrivee(),
                d.mode().name(), d.urgent(), d.assurance(), d.typeClient().name());
        return devis == -1 ? OptionalDouble.empty() : OptionalDouble.of(devis);
    }

    @Override
    public boolean demarrer(String id) {
        return serviceExpedition.demarrerExpedition(id);
    }

    @Override
    public boolean livrer(String id) {
        return serviceExpedition.livrerExpedition(id);
    }

    @Override
    public boolean annuler(String id) {
        return serviceExpedition.annulerExpedition(id);
    }

    @Override
    public Optional<String> facture(String id) {
        return Optional.ofNullable(Database.expeditions.get(id)).map(Expedition::genererFacture);
    }

    @Override
    public List<Notification> notifications() {
        List<Notification> notifications = new ArrayList<>();
        for (String[] email : EmailService.journal) {
            notifications.add(new Notification(Canal.EMAIL, email[0], email[1]));
        }
        for (String[] sms : SmsService.journal) {
            notifications.add(new Notification(Canal.SMS, sms[0], sms[1]));
        }
        return notifications;
    }

    private static java.time.LocalDate jour(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static List<Colis> versColis(List<ColisDto> colis) {
        return colis.stream().map(LegacyAdapter::versColis).collect(Collectors.toCollection(ArrayList::new));
    }

    private static Colis versColis(ColisDto c) {
        return new Colis(c.description(), c.poidsKg(), c.longueurCm(), c.largeurCm(), c.hauteurCm(),
                codeType(c.type()), c.valeurDeclaree());
    }

    private static int codeType(TypeMarchandise type) {
        return switch (type) {
            case STANDARD -> Colis.STANDARD;
            case FRAGILE -> Colis.FRAGILE;
            case DANGEREUX -> Colis.DANGEREUX;
            case REFRIGERE -> Colis.REFRIGERE;
        };
    }
}
