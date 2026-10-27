package es.convive.backend.repository;

import es.convive.backend.entity.ParticipanteGasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipanteGastoRepository extends JpaRepository<ParticipanteGasto, Long> {

    List<ParticipanteGasto> findAllByUsuarioId(Long usuarioId);

    List<ParticipanteGasto> findAllByGastoIdOrderByOrdenParticipanteAsc(Long gastoId);
}
