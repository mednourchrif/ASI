package tn.esprit.autoloc.web.dto;

public record VehiculeResumeDTO(
        Long id,
        String immatriculation,
        String marque,
        String modele
) {}
