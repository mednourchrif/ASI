package tn.esprit.autoloc.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.ModePaiement;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutContrat;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IClientRepository clientRepository;
    private final IEmployeRepository employeRepository;
    private final IReservationRepository reservationRepository;
    private final IMaintenanceRepository maintenanceRepository;
    private final IContratRepository contratRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (agenceRepository.count() > 0) {
            return;
        }

        // ── 1. Agences tunisiennes ───────────────────────────────────────────
        Agence tunisCentre = agenceRepository.save(new Agence(null, "AutoLoc Tunis Centre",
                "45 Avenue Habib Bourguiba", "Tunis", "71234567", List.of()));
        Agence tunisAeroport = agenceRepository.save(new Agence(null, "AutoLoc Aéroport Carthage",
                "Zone Aéroportuaire Tunis-Carthage", "Tunis", "71987654", List.of()));
        Agence sousse = agenceRepository.save(new Agence(null, "AutoLoc Sousse Corniche",
                "Boulevard 14 Janvier", "Sousse", "73221100", List.of()));
        Agence sfax = agenceRepository.save(new Agence(null, "AutoLoc Sfax El Jadida",
                "Route de Gabès Km 2", "Sfax", "74445566", List.of()));
        Agence djerba = agenceRepository.save(new Agence(null, "AutoLoc Djerba Midoun",
                "Avenue de la Liberté, Midoun", "Djerba", "75654321", List.of()));

        // ── 2. Équipements ───────────────────────────────────────────────────
        Equipement gps = equipementRepository.save(new Equipement(null, "GPS", 12, "Navigation GPS Tunisie & Maghreb"));
        Equipement siegeBebe = equipementRepository.save(new Equipement(null, "Siège Bébé", 8, "Siège auto homologué groupe 0+/1"));
        Equipement clim = equipementRepository.save(new Equipement(null, "Climatisation", 20, "Climatisation automatique bi-zone"));
        Equipement coffreToit = equipementRepository.save(new Equipement(null, "Coffre de toit", 5, "Coffre de toit 400L"));
        Equipement cameraRecul = equipementRepository.save(new Equipement(null, "Caméra de recul", 10, "Aide au stationnement HD"));
        Equipement wifi = equipementRepository.save(new Equipement(null, "Wi-Fi embarqué", 6, "Routeur 4G Tunisie Telecom / Ooredoo"));

        // ── 3. Véhicules (plaques tunisiennes, tarifs en TND) ────────────────
        Vehicule v1 = vehiculeRepository.save(buildVehicule("215TU1001", "Renault", "Clio 5", 24500,
                new BigDecimal("85.00"), true, CategorieVehicule.CITADINE, StatutVehicule.DISPONIBLE,
                tunisCentre, List.of(gps, clim)));

        Vehicule v2 = vehiculeRepository.save(buildVehicule("220TU2045", "Peugeot", "208", 18200,
                new BigDecimal("90.00"), true, CategorieVehicule.CITADINE, StatutVehicule.DISPONIBLE,
                tunisCentre, List.of(clim, cameraRecul)));

        Vehicule v3 = vehiculeRepository.save(buildVehicule("198TU3310", "Volkswagen", "Golf 8", 41000,
                new BigDecimal("140.00"), false, CategorieVehicule.BERLINE, StatutVehicule.RESERVE,
                tunisCentre, List.of(gps, clim, cameraRecul, wifi)));

        Vehicule v4 = vehiculeRepository.save(buildVehicule("231TU4412", "Hyundai", "Tucson", 15000,
                new BigDecimal("185.00"), true, CategorieVehicule.SUV, StatutVehicule.DISPONIBLE,
                tunisAeroport, List.of(gps, clim, cameraRecul, siegeBebe)));

        Vehicule v5 = vehiculeRepository.save(buildVehicule("210TU5588", "Dacia", "Duster", 53000,
                new BigDecimal("130.00"), false, CategorieVehicule.SUV, StatutVehicule.EN_MAINTENANCE,
                tunisAeroport, List.of(clim, coffreToit)));

        Vehicule v6 = vehiculeRepository.save(buildVehicule("205TU6621", "Kia", "Picanto", 32000,
                new BigDecimal("65.00"), true, CategorieVehicule.CITADINE, StatutVehicule.DISPONIBLE,
                sousse, List.of(clim)));

        Vehicule v7 = vehiculeRepository.save(buildVehicule("228TU7733", "Mercedes-Benz", "Classe C", 12000,
                new BigDecimal("320.00"), false, CategorieVehicule.LUXE, StatutVehicule.RESERVE,
                sousse, List.of(gps, clim, cameraRecul, wifi)));

        Vehicule v8 = vehiculeRepository.save(buildVehicule("192TU8844", "Peugeot", "Partner", 78000,
                new BigDecimal("110.00"), true, CategorieVehicule.UTILITAIRE, StatutVehicule.DISPONIBLE,
                sfax, List.of(clim)));

        Vehicule v9 = vehiculeRepository.save(buildVehicule("219TU9955", "Renault", "Megane", 36000,
                new BigDecimal("125.00"), false, CategorieVehicule.BERLINE, StatutVehicule.EN_MAINTENANCE,
                sfax, List.of(gps, clim, siegeBebe)));

        // Véhicule sans agence affectée (pour K10 : findByAgenceIsNull)
        vehiculeRepository.save(buildVehicule("240TU0099", "Suzuki", "Swift", 5400,
                new BigDecimal("75.00"), true, CategorieVehicule.CITADINE, StatutVehicule.DISPONIBLE,
                null, List.of()));

        // ── 4. Clients tunisiens ─────────────────────────────────────────────
        Client c1 = clientRepository.save(new Client(null, "Ben Ali", "Youssef",
                "youssef.benali@esprit.tn", "+216 22 345 678", "TN-0987654"));
        Client c2 = clientRepository.save(new Client(null, "Trabelsi", "Amira",
                "amira.trabelsi@gmail.com", "+216 55 112 233", "TN-1122334"));
        Client c3 = clientRepository.save(new Client(null, "Gharbi", "Mehdi",
                "mehdi.gharbi@yahoo.fr", "+216 98 765 432", "TN-4455667"));
        Client c4 = clientRepository.save(new Client(null, "Khelifi", "Salma",
                "salma.khelifi@esprit.tn", "+216 29 887 766", "TN-7788990"));
        clientRepository.save(new Client(null, "Mansouri", "Skander",
                "skander.mansouri@outlook.com", "+216 50 334 455", "TN-3344556"));

        // ── 5. Employés ──────────────────────────────────────────────────────
        employeRepository.save(new Employe(null, "Jaziri", "Karim", "Chef d'agence", "karim.jaziri@autoloc.tn", tunisCentre));
        employeRepository.save(new Employe(null, "Bouazizi", "Nour", "Agent de location", "nour.bouazizi@autoloc.tn", tunisAeroport));
        employeRepository.save(new Employe(null, "Chaabane", "Houssem", "Responsable parc", "houssem.chaabane@autoloc.tn", sousse));
        employeRepository.save(new Employe(null, "Miled", "Ines", "Agent commercial", "ines.miled@autoloc.tn", sfax));
        employeRepository.save(new Employe(null, "Sassi", "Rami", "Chef d'agence", "rami.sassi@autoloc.tn", djerba));

        // ── 6. Réservations ──────────────────────────────────────────────────
        Reservation r1 = reservationRepository.save(new Reservation(null,
                LocalDate.of(2026, 11, 20), LocalDate.of(2026, 11, 28),
                StatutReservation.CONFIRMEE, c1, v3));
        Reservation r2 = reservationRepository.save(new Reservation(null,
                LocalDate.of(2026, 11, 25), LocalDate.of(2026, 12, 2),
                StatutReservation.CONFIRMEE, c2, v7));
        Reservation r3 = reservationRepository.save(new Reservation(null,
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 10),
                StatutReservation.TERMINEE, c1, v1));
        reservationRepository.save(new Reservation(null,
                LocalDate.of(2026, 12, 10), LocalDate.of(2026, 12, 15),
                StatutReservation.EN_ATTENTE, c3, v4));
        reservationRepository.save(new Reservation(null,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 4),
                StatutReservation.ANNULEE, c4, v2));
        // Remarque : v5, v6, v8, v9 et le véhicule sans agence n'ont aucune réservation (pour J5)

        // ── 7. Maintenances ──────────────────────────────────────────────────
        maintenanceRepository.save(new Maintenance(null,
                LocalDate.of(2026, 11, 20), LocalDate.of(2026, 11, 30),
                480.0, "Vidange complète, plaquettes de frein et géométrie", v5));
        maintenanceRepository.save(new Maintenance(null,
                LocalDate.of(2026, 11, 24), LocalDate.of(2026, 12, 3),
                650.0, "Remplacement courroie de distribution", v9));
        maintenanceRepository.save(new Maintenance(null,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 3),
                190.0, "Changement pneus avant", v8));

        // ── 8. Contrats & Paiements ──────────────────────────────────────────
        Contrat ct1 = new Contrat();
        ct1.setDateDebut(LocalDate.of(2026, 11, 20));
        ct1.setDateFin(LocalDate.of(2026, 11, 28));
        ct1.setMontantTotal(1120.0);
        ct1.setStatut(StatutContrat.EN_COURS);
        ct1.setReservation(r1);
        ct1.getPaiements().add(new Paiement(null, 500.0, LocalDate.of(2026, 11, 20), ModePaiement.CARTE_BANCAIRE, ct1));
        ct1.getPaiements().add(new Paiement(null, 620.0, LocalDate.of(2026, 11, 22), ModePaiement.ESPECES, ct1));
        contratRepository.save(ct1);

        Contrat ct2 = new Contrat();
        ct2.setDateDebut(LocalDate.of(2026, 11, 25));
        ct2.setDateFin(LocalDate.of(2026, 12, 2));
        ct2.setMontantTotal(2240.0);
        ct2.setStatut(StatutContrat.EN_COURS);
        ct2.setReservation(r2);
        ct2.getPaiements().add(new Paiement(null, 2240.0, LocalDate.of(2026, 11, 25), ModePaiement.VIREMENT, ct2));
        contratRepository.save(ct2);

        Contrat ct3 = new Contrat();
        ct3.setDateDebut(LocalDate.of(2026, 10, 5));
        ct3.setDateFin(LocalDate.of(2026, 10, 10));
        ct3.setMontantTotal(425.0);
        ct3.setStatut(StatutContrat.TERMINE);
        ct3.setReservation(r3);
        ct3.getPaiements().add(new Paiement(null, 425.0, LocalDate.of(2026, 10, 5), ModePaiement.CARTE_BANCAIRE, ct3));
        contratRepository.save(ct3);
    }

    private Vehicule buildVehicule(
            String immatriculation, String marque, String modele, Integer kilometrage,
            BigDecimal tarifJournalier, Boolean disponible, CategorieVehicule categorie,
            StatutVehicule statut, Agence agence, List<Equipement> equipements) {
        Vehicule v = new Vehicule();
        v.setImmatriculation(immatriculation);
        v.setMarque(marque);
        v.setModele(modele);
        v.setKilometrage(kilometrage);
        v.setTarifJournalier(tarifJournalier);
        v.setDisponible(disponible);
        v.setCategorie(categorie);
        v.setStatut(statut);
        v.setAgence(agence);
        v.setEquipements(equipements);
        return v;
    }
}
