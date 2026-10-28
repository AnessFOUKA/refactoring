package fr.efrei.transfret;

import java.util.List;
import java.util.Optional;

public interface ExpeditionRepository {

    void sauvegarder(Expedition expedition);

    void mettreAJour(Expedition expedition);

    Optional<Expedition> trouverParId(String id);

    List<Expedition> toutes();
}
