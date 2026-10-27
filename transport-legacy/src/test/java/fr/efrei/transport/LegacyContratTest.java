package fr.efrei.transport;

import fr.efrei.transport.contrat.ContratTransportTest;
import fr.efrei.transport.contrat.SystemeTransport;

class LegacyContratTest extends ContratTransportTest {

    @Override
    protected SystemeTransport creerSysteme() {
        return new LegacyAdapter();
    }
}
