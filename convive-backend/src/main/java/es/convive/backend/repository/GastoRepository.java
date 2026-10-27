package es.convive.backend.repository;

import es.convive.backend.entity.Gasto;
import es.convive.backend.entity.CategoriaGasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findAllByPisoIdOrderByFechaDescIdDesc(Long pisoId);

    List<Gasto> findAllByPisoIdAndFechaBetweenOrderByFechaDescIdDesc(Long pisoId, LocalDate desde, LocalDate hasta);

    List<Gasto> findAllByPisoIdAndCategoriaOrderByFechaDescIdDesc(Long pisoId, CategoriaGasto categoria);

    List<Gasto> findAllByPisoIdAndFechaBetweenAndCategoriaOrderByFechaDescIdDesc(
            Long pisoId, LocalDate desde, LocalDate hasta, CategoriaGasto categoria);
}
