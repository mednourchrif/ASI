package tn.esprit.autoloc.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import tn.esprit.autoloc.domain.CategorieVehicule;

public record VehiculeRequestDTO(

        @NotBlank(message = "L'immatriculation est obligatoire")
        @Pattern(regexp = "^\\d{2,3}[A-Z]{2}\\d{4}$",
                message = "Format attendu : ex. 120TU1001 (2-3 chiffres, 2 lettres maj, 4 chiffres)")
        String immatriculation,

        @NotBlank(message = "La marque est obligatoire")
        @Size(max = 30, message = "La marque ne doit pas dépasser 30 caractères")
        String marque,

        @NotBlank(message = "Le modèle est obligatoire")
        @Size(max = 30, message = "Le modèle ne doit pas dépasser 30 caractères")
        String modele,

        @NotNull(message = "La catégorie est obligatoire")
        CategorieVehicule categorie,

        @NotNull(message = "Le tarif journalier est obligatoire")
        @Positive(message = "Le tarif doit être strictement positif")
        BigDecimal tarifJournalier,

        @NotNull(message = "L'agence est obligatoire")
        Long agenceId

) {}
