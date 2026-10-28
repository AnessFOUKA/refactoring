package fr.efrei.transfret;

public class ApplicationTransFret {

    private final DepotClientsMemoire clients = new DepotClientsMemoire();
    private final DepotExpeditionsMemoire expeditions = new DepotExpeditionsMemoire();
    private final Notificateur notificateur = new Notificateur(new CanalEmailConsole(), new CanalSmsConsole());
    private final Flotte flotte = new Flotte();
    private final ServiceClient serviceClient = new ServiceClient(clients, notificateur);
    private final ServiceExpedition serviceExpedition = new ServiceExpedition(
            expeditions,
            new CalculateurPrix(),
            new AffectationVehicule(flotte),
            new ValidateurExpedition(),
            new EstimateurDelai(),
            notificateur
    );
    private final GenerateurFacture generateurFacture = new GenerateurFacture();

    public ClientRepository clients() {
        return clients;
    }

    public ExpeditionRepository expeditions() {
        return expeditions;
    }

    public Notificateur notificateur() {
        return notificateur;
    }

    public ServiceClient serviceClient() {
        return serviceClient;
    }

    public ServiceExpedition serviceExpedition() {
        return serviceExpedition;
    }

    public GenerateurFacture generateurFacture() {
        return generateurFacture;
    }
}
