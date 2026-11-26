package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.NbVehiculesAgenceDTO;
import tn.esprit.autoloc.web.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.web.dto.VehiculeResumeDTO;

public interface IVehiculeService {

    // ── CRUD ─────────────────────────────────────────────────────────────────
    VehiculeResponseDTO create(VehiculeRequestDTO dto);

    VehiculeResponseDTO findById(Long id);

    List<VehiculeResumeDTO> findAll();

    Vehicule update(Long id, Vehicule vehicule);

    void deleteById(Long id);

    // ── Affectation (legacy endpoints conservés) ──────────────────────────────
    Vehicule assignAgence(Long idVehicule, Long idAgence);

    Vehicule assignEquipment(Long idVehicule, Long idEquipment);

    Vehicule unassignEquipment(Long idVehicule, Long idEquipment);

    Vehicule addAndAssignAgence(Vehicule vehicule, Long idAgence);

    // ── K1-K10 : keyword methods ──────────────────────────────────────────────
    List<VehiculeResumeDTO> findByMarque(String marque);

    List<VehiculeResumeDTO> findByCategorieAndStatut(CategorieVehicule categorie, StatutVehicule statut);

    List<VehiculeResumeDTO> findByTarifJournalierBetween(BigDecimal min, BigDecimal max);

    List<VehiculeResumeDTO> findByModeleContaining(String motCle);

    List<VehiculeResumeDTO> findByCategorieIn(List<CategorieVehicule> categories);

    boolean existsByImmatriculation(String immatriculation);

    long countByStatut(StatutVehicule statut);

    List<VehiculeResumeDTO> findTop3ByOrderByTarifJournalierAsc();

    List<VehiculeResumeDTO> findByAgenceId(Long agenceId);

    List<VehiculeResumeDTO> findByAgenceIsNull();

    // ── J1-J8 : JPQL queries ──────────────────────────────────────────────────
    List<VehiculeResumeDTO> findByVilleAgence(String ville);

    List<VehiculeResumeDTO> findByEquipement(String libelle);

    List<VehiculeResumeDTO> findAvecAuMoinsNEquipements(long n);

    List<VehiculeResumeDTO> findReservesParClient(Long clientId);

    List<VehiculeResumeDTO> findJamaisReserves();

    List<VehiculeResumeDTO> findEnMaintenanceLe(LocalDate date);

    List<NbVehiculesAgenceDTO> compterParAgence();
}
