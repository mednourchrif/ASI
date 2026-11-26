package tn.esprit.autoloc.web.dto;

import java.math.BigDecimal;
import java.util.List;

import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

public record VehiculeResponseDTO(
        Long id,
        String immatriculation,
        String marque,
        String modele,
        CategorieVehicule categorie,
        StatutVehicule statut,
        BigDecimal tarifJournalier,
        String agenceNom,
        List<String> equipementLibelles
) {}
