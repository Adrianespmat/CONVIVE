package es.convive.backend.repository;

import es.convive.backend.entity.Piso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PisoRepository extends JpaRepository<Piso, Long> {

    Optional<Piso> findByIdAndActivoTrue(Long id);

    boolean existsByCodigoInvitacion(String codigoInvitacion);

    Optional<Piso> findByCodigoInvitacionAndActivoTrue(String codigoInvitacion);
}
