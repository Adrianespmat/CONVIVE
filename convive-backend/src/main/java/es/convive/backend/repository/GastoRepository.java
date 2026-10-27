package es.convive.backend.repository;

import es.convive.backend.entity.Gasto;
import es.convive.backend.entity.CategoriaGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    @Query("select g from Gasto g where g.piso.id = :pisoId "
            + "and (:desde is null or g.fecha >= :desde) "
            + "and (:hasta is null or g.fecha <= :hasta) "
            + "and (:categoria is null or g.categoria = :categoria) "
            + "order by g.fecha desc, g.id desc")
    List<Gasto> buscarPorFiltros(@Param("pisoId") Long pisoId,
                                 @Param("desde") LocalDate desde,
                                 @Param("hasta") LocalDate hasta,
                                 @Param("categoria") CategoriaGasto categoria);

    List<Gasto> findAllByPisoIdOrderByFechaDescIdDesc(Long pisoId);

    List<Gasto> findAllByPisoIdAndFechaBetweenOrderByFechaDescIdDesc(Long pisoId, LocalDate desde, LocalDate hasta);

    List<Gasto> findAllByPisoIdAndCategoriaOrderByFechaDescIdDesc(Long pisoId, CategoriaGasto categoria);

    List<Gasto> findAllByPisoIdAndFechaBetweenAndCategoriaOrderByFechaDescIdDesc(
            Long pisoId, LocalDate desde, LocalDate hasta, CategoriaGasto categoria);
}
