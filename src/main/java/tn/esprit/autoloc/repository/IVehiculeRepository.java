package tn.esprit.autoloc.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.NbVehiculesAgenceDTO;

public interface IVehiculeRepository extends JpaRepository<Vehicule, Long> {

    // ── K1 ────────────────────────────────────────────────────────────────────
    List<Vehicule> findByMarque(String marque);

    // ── K2 ────────────────────────────────────────────────────────────────────
    List<Vehicule> findByCategorieAndStatut(CategorieVehicule categorie, StatutVehicule statut);

    // ── K3 ────────────────────────────────────────────────────────────────────
    List<Vehicule> findByTarifJournalierBetweenOrderByTarifJournalierAsc(BigDecimal min, BigDecimal max);

    // ── K4 ────────────────────────────────────────────────────────────────────
    List<Vehicule> findByModeleContainingIgnoreCase(String motCle);

    // ── K5 ────────────────────────────────────────────────────────────────────
    List<Vehicule> findByCategorieIn(List<CategorieVehicule> categories);

    // ── K6 ────────────────────────────────────────────────────────────────────
    boolean existsByImmatriculation(String immatriculation);

    // ── K7 ────────────────────────────────────────────────────────────────────
    long countByStatut(StatutVehicule statut);

    // ── K8 ────────────────────────────────────────────────────────────────────
    List<Vehicule> findTop3ByOrderByTarifJournalierAsc();

    // ── K9 ────────────────────────────────────────────────────────────────────
    List<Vehicule> findByAgenceId(Long agenceId);

    // ── K10 ───────────────────────────────────────────────────────────────────
    List<Vehicule> findByAgenceIsNull();

    // ── J1 : véhicules d'une ville d'agence ──────────────────────────────────
    @Query("SELECT v FROM Vehicule v JOIN v.agence a WHERE a.ville = :ville")
    List<Vehicule> findByVilleAgence(@Param("ville") String ville);

    // ── J2 : véhicules ayant un équipement donné ─────────────────────────────
    @Query("SELECT DISTINCT v FROM Vehicule v JOIN v.equipements e WHERE e.libelle = :libelle")
    List<Vehicule> findByEquipement(@Param("libelle") String libelle);

    // ── J3 : véhicules ayant au moins N équipements ──────────────────────────
    @Query("SELECT v FROM Vehicule v JOIN v.equipements e GROUP BY v HAVING COUNT(e) >= :n")
    List<Vehicule> findAvecAuMoinsNEquipements(@Param("n") long n);

    // ── J4 : véhicules réservés par un client ────────────────────────────────
    @Query("SELECT DISTINCT v FROM Vehicule v JOIN v.reservations r WHERE r.client.id = :clientId")
    List<Vehicule> findReservesParClient(@Param("clientId") Long clientId);

    // ── J5 : véhicules jamais réservés ───────────────────────────────────────
    @Query("SELECT v FROM Vehicule v WHERE v.id NOT IN (SELECT DISTINCT r.vehicule.id FROM Reservation r)")
    List<Vehicule> findJamaisReserves();

    // ── J6 : véhicules en maintenance à une date donnée ──────────────────────
    @Query("SELECT DISTINCT v FROM Vehicule v JOIN v.maintenances m WHERE m.dateDebut <= :date AND m.dateFin >= :date")
    List<Vehicule> findEnMaintenanceLe(@Param("date") LocalDate date);

    // ── J7 : chargement avec agence + équipements (fetch join) ───────────────
    @Query("SELECT v FROM Vehicule v LEFT JOIN FETCH v.agence LEFT JOIN FETCH v.equipements WHERE v.id = :id")
    Optional<Vehicule> findByIdAvecEquipements(@Param("id") Long id);

    // ── J8 : comptage par agence ──────────────────────────────────────────────
    @Query("SELECT new tn.esprit.autoloc.web.dto.NbVehiculesAgenceDTO(a.nom, COUNT(v)) " +
           "FROM Agence a LEFT JOIN a.vehicules v GROUP BY a.nom")
    List<NbVehiculesAgenceDTO> compterParAgence();
}
