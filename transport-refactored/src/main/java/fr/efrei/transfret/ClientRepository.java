package fr.efrei.transfret;

import java.util.Optional;

public interface ClientRepository {

    void sauvegarder(Client client);

    Optional<Client> trouverParEmail(String email);
}
