package fr.efrei.transfret;

import fr.efrei.transport.contrat.ContratTransportTest;
import fr.efrei.transport.contrat.SystemeTransport;

class NouveauSystemeContratTest extends ContratTransportTest {

    @Override
    protected SystemeTransport creerSysteme() {
        return new NouveauSystemeAdapter();
    }
}
