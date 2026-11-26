package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.NbVehiculesAgenceDTO;
import tn.esprit.autoloc.web.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.web.dto.VehiculeResumeDTO;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;
    private final VehiculeMapper vehiculeMapper;

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public VehiculeResponseDTO create(VehiculeRequestDTO dto) {
        if (vehiculeRepository.existsByImmatriculation(dto.immatriculation())) {
            throw new IllegalArgumentException("Immatriculation déjà utilisée : " + dto.immatriculation());
        }
        Vehicule v = vehiculeMapper.toEntity(dto);
        Agence agence = agenceRepository.findById(dto.agenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Agence", dto.agenceId()));
        v.setAgence(agence);
        Vehicule saved = vehiculeRepository.save(v);
        return vehiculeMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponseDTO findById(Long id) {
        Vehicule v = vehiculeRepository.findByIdAvecEquipements(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
        return vehiculeMapper.toDto(v);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findAll() {
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toResumeDto)
                .toList();
    }

    @Override
    @Transactional
    public Vehicule update(Long id, Vehicule vehicule) {
        Vehicule existant = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
        existant.setImmatriculation(vehicule.getImmatriculation());
        existant.setMarque(vehicule.getMarque());
        existant.setModele(vehicule.getModele());
        existant.setKilometrage(vehicule.getKilometrage());
        existant.setTarifJournalier(vehicule.getTarifJournalier());
        existant.setDisponible(vehicule.getDisponible());
        existant.setCategorie(vehicule.getCategorie());
        existant.setStatut(vehicule.getStatut());
        existant.setAgence(vehicule.getAgence());
        return vehiculeRepository.save(existant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    // ── Affectation ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Vehicule assignAgence(Long idVehicule, Long idAgence) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", idVehicule));
        Agence agence = agenceRepository.findById(idAgence)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", idAgence));
        vehicule.setAgence(agence);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public Vehicule assignEquipment(Long idVehicule, Long idEquipment) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", idVehicule));
        Equipement equipement = equipementRepository.findById(idEquipment)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", idEquipment));
        if (vehicule.getEquipements() == null) {
            vehicule.setEquipements(new ArrayList<>());
        }
        if (!vehicule.getEquipements().contains(equipement)) {
            vehicule.getEquipements().add(equipement);
        }
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public Vehicule unassignEquipment(Long idVehicule, Long idEquipment) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", idVehicule));
        Equipement equipement = equipementRepository.findById(idEquipment)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", idEquipment));
        if (vehicule.getEquipements() != null) {
            vehicule.getEquipements().remove(equipement);
        }
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public Vehicule addAndAssignAgence(Vehicule vehicule, Long idAgence) {
        if (vehicule.getId() != null) {
            throw new IllegalArgumentException("Un nouveau vehicule ne doit pas avoir d'identifiant");
        }
        Agence agence = agenceRepository.findById(idAgence)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", idAgence));
        vehicule.setAgence(agence);
        if (vehicule.getEquipements() != null && !vehicule.getEquipements().isEmpty()) {
            List<Equipement> resolved = new ArrayList<>();
            for (Equipement eq : vehicule.getEquipements()) {
                if (eq.getId() != null) {
                    resolved.add(equipementRepository.findById(eq.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Equipement", eq.getId())));
                } else {
                    resolved.add(eq);
                }
            }
            vehicule.setEquipements(resolved);
        }
        return vehiculeRepository.save(vehicule);
    }

    // ── K1-K10 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findByMarque(String marque) {
        return vehiculeRepository.findByMarque(marque).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findByCategorieAndStatut(CategorieVehicule categorie, StatutVehicule statut) {
        return vehiculeRepository.findByCategorieAndStatut(categorie, statut).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findByTarifJournalierBetween(BigDecimal min, BigDecimal max) {
        return vehiculeRepository.findByTarifJournalierBetweenOrderByTarifJournalierAsc(min, max).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findByModeleContaining(String motCle) {
        return vehiculeRepository.findByModeleContainingIgnoreCase(motCle).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findByCategorieIn(List<CategorieVehicule> categories) {
        return vehiculeRepository.findByCategorieIn(categories).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByImmatriculation(String immatriculation) {
        return vehiculeRepository.existsByImmatriculation(immatriculation);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatut(StatutVehicule statut) {
        return vehiculeRepository.countByStatut(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findTop3ByOrderByTarifJournalierAsc() {
        return vehiculeRepository.findTop3ByOrderByTarifJournalierAsc().stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findByAgenceId(Long agenceId) {
        return vehiculeRepository.findByAgenceId(agenceId).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findByAgenceIsNull() {
        return vehiculeRepository.findByAgenceIsNull().stream().map(vehiculeMapper::toResumeDto).toList();
    }

    // ── J1-J8 ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findByVilleAgence(String ville) {
        return vehiculeRepository.findByVilleAgence(ville).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findByEquipement(String libelle) {
        return vehiculeRepository.findByEquipement(libelle).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findAvecAuMoinsNEquipements(long n) {
        return vehiculeRepository.findAvecAuMoinsNEquipements(n).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findReservesParClient(Long clientId) {
        return vehiculeRepository.findReservesParClient(clientId).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findJamaisReserves() {
        return vehiculeRepository.findJamaisReserves().stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResumeDTO> findEnMaintenanceLe(LocalDate date) {
        return vehiculeRepository.findEnMaintenanceLe(date).stream().map(vehiculeMapper::toResumeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NbVehiculesAgenceDTO> compterParAgence() {
        return vehiculeRepository.compterParAgence();
    }
}
