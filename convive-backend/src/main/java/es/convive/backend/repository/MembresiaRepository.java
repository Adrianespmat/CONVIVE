package es.convive.backend.repository;

import es.convive.backend.entity.Membresia;
import es.convive.backend.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    Optional<Membresia> findByUsuarioIdAndActivoTrue(Long usuarioId);

    Optional<Membresia> findByUsuarioIdAndPisoIdAndActivoTrue(Long usuarioId, Long pisoId);

    List<Membresia> findAllByPisoIdAndActivoTrueOrderByFechaAltaAsc(Long pisoId);

    boolean existsByUsuarioIdAndActivoTrue(Long usuarioId);

    boolean existsByPisoIdAndUsuarioIdAndActivoTrue(Long pisoId, Long usuarioId);

    long countByPisoIdAndRolAndActivoTrue(Long pisoId, Rol rol);
}
