package fr.efrei.transfret;

import fr.efrei.transport.contrat.ContratTransportTest;
import fr.efrei.transport.contrat.SystemeTransport;
import org.junit.jupiter.api.Disabled;

@Disabled("Retirer cette annotation dès que NouveauSystemeAdapter est branché sur le nouveau code")
class NouveauSystemeContratTest extends ContratTransportTest {

    @Override
    protected SystemeTransport creerSysteme() {
        return new NouveauSystemeAdapter();
    }
}
