package es.convive.backend.service;

import es.convive.backend.dto.ActualizarGastoSolicitud;
import es.convive.backend.dto.CrearGastoSolicitud;
import es.convive.backend.dto.GastoRespuesta;
import es.convive.backend.entity.Gasto;
import es.convive.backend.entity.Membresia;
import es.convive.backend.entity.ParticipanteGasto;
import es.convive.backend.entity.Piso;
import es.convive.backend.entity.Usuario;
import es.convive.backend.entity.CategoriaGasto;
import es.convive.backend.exception.AccesoDenegadoException;
import es.convive.backend.exception.DatosGastoException;
import es.convive.backend.exception.RecursoNoEncontradoException;
import es.convive.backend.repository.GastoRepository;
import es.convive.backend.repository.MembresiaRepository;
import es.convive.backend.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class ServicioGastos {
    private final GastoRepository gastoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MembresiaRepository membresiaRepository;
    private final ServicioPisos servicioPisos;
    private final CalculadorReparto calculadorReparto;
    private final EntityManager entityManager;

    public ServicioGastos(GastoRepository gastoRepository, UsuarioRepository usuarioRepository,
                          MembresiaRepository membresiaRepository, ServicioPisos servicioPisos,
                          CalculadorReparto calculadorReparto, EntityManager entityManager) {
        this.gastoRepository = gastoRepository;
        this.usuarioRepository = usuarioRepository;
        this.membresiaRepository = membresiaRepository;
        this.servicioPisos = servicioPisos;
        this.calculadorReparto = calculadorReparto;
        this.entityManager = entityManager;
    }

    @Transactional
    public GastoRespuesta crear(Long usuarioId, CrearGastoSolicitud solicitud) {
        Membresia membresia = servicioPisos.obtenerMembresiaActiva(usuarioId);
        DatosGasto datos = validarDatos(membresia.getPiso(), solicitud.concepto(), solicitud.importe(),
                solicitud.fecha(), solicitud.categoria(), solicitud.pagadorUsuarioId(), solicitud.participantesUsuarioIds());
        Gasto gasto = new Gasto();
        gasto.setPiso(membresia.getPiso());
        gasto.setCreadoPor(membresia.getUsuario());
        aplicarDatos(gasto, datos);
        return GastoRespuesta.desde(gastoRepository.save(gasto));
    }

    @Transactional(readOnly = true)
    public List<GastoRespuesta> listar(Long usuarioId, LocalDate desde, LocalDate hasta, CategoriaGasto categoria) {
        Membresia membresia = servicioPisos.obtenerMembresiaActiva(usuarioId);
        validarIntervalo(desde, hasta);
        return gastoRepository.buscarPorFiltros(membresia.getPiso().getId(), desde, hasta, categoria)
                .stream().map(GastoRespuesta::desde).toList();
    }

    @Transactional(readOnly = true)
    public GastoRespuesta obtener(Long usuarioId, Long gastoId) {
        Gasto gasto = obtenerGastoDelPiso(usuarioId, gastoId);
        return GastoRespuesta.desde(gasto);
    }

    @Transactional
    public GastoRespuesta actualizar(Long usuarioId, Long gastoId, ActualizarGastoSolicitud solicitud) {
        Gasto gasto = obtenerGastoDelPiso(usuarioId, gastoId);
        comprobarPropietario(gasto, usuarioId);
        Membresia membresia = servicioPisos.obtenerMembresiaActiva(usuarioId);
        DatosGasto datos = validarDatos(membresia.getPiso(), solicitud.concepto(), solicitud.importe(),
                solicitud.fecha(), solicitud.categoria(), solicitud.pagadorUsuarioId(), solicitud.participantesUsuarioIds());
        aplicarDatos(gasto, datos);
        return GastoRespuesta.desde(gastoRepository.save(gasto));
    }

    @Transactional
    public void eliminar(Long usuarioId, Long gastoId) {
        Gasto gasto = obtenerGastoDelPiso(usuarioId, gastoId);
        comprobarPropietario(gasto, usuarioId);
        gastoRepository.delete(gasto);
    }

    private Gasto obtenerGastoDelPiso(Long usuarioId, Long gastoId) {
        Membresia membresia = servicioPisos.obtenerMembresiaActiva(usuarioId);
        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El gasto no existe"));
        if (!gasto.getPiso().getId().equals(membresia.getPiso().getId())) {
            throw new RecursoNoEncontradoException("El gasto no existe en tu piso");
        }
        return gasto;
    }

    private void comprobarPropietario(Gasto gasto, Long usuarioId) {
        if (!gasto.getCreadoPor().getId().equals(usuarioId)) {
            throw new AccesoDenegadoException("Solo el creador puede modificar o eliminar este gasto");
        }
    }

    private DatosGasto validarDatos(Piso piso, String concepto, BigDecimal importe, LocalDate fecha,
                                    CategoriaGasto categoria, Long pagadorId, List<Long> participantesIds) {
        if (concepto == null || concepto.isBlank() || concepto.trim().length() > 180) {
            throw new DatosGastoException("El concepto es obligatorio y no puede superar los 180 caracteres");
        }
        if (importe == null || importe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatosGastoException("El importe debe ser mayor que cero");
        }
        try {
            importe = importe.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new DatosGastoException("El importe debe tener como máximo dos decimales");
        }
        if (fecha == null || categoria == null || pagadorId == null || participantesIds == null || participantesIds.isEmpty()) {
            throw new DatosGastoException("Faltan datos obligatorios del gasto");
        }
        List<Long> idsOrdenados = new ArrayList<>(participantesIds);
        if (idsOrdenados.stream().anyMatch(java.util.Objects::isNull)
                || new HashSet<>(idsOrdenados).size() != idsOrdenados.size()) {
            throw new DatosGastoException("Los participantes deben ser usuarios distintos");
        }
        idsOrdenados.sort(Long::compareTo);
        List<Long> idsAValidar = new ArrayList<>(idsOrdenados);
        idsAValidar.add(pagadorId);
        Map<Long, Usuario> usuarios = cargarUsuariosDelPiso(piso, idsAValidar);
        Map<Long, BigDecimal> reparto = calculadorReparto.repartir(importe, idsOrdenados);
        return new DatosGasto(concepto.trim(), importe, fecha, categoria, usuarios.get(pagadorId), idsOrdenados, usuarios, reparto);
    }

    private Map<Long, Usuario> cargarUsuariosDelPiso(Piso piso, List<Long> ids) {
        Map<Long, Usuario> usuarios = new HashMap<>();
        usuarioRepository.findAllById(ids).forEach(usuario -> usuarios.put(usuario.getId(), usuario));
        for (Long id : new HashSet<>(ids)) {
            if (!usuarios.containsKey(id) || !membresiaRepository.existsByPisoIdAndUsuarioIdAndActivoTrue(piso.getId(), id)) {
                throw new DatosGastoException("Todos los usuarios deben pertenecer al piso activo");
            }
        }
        return usuarios;
    }

    private void aplicarDatos(Gasto gasto, DatosGasto datos) {
        gasto.setPagador(datos.pagador());
        gasto.setConcepto(datos.concepto());
        gasto.setImporte(datos.importe());
        gasto.setFecha(datos.fecha());
        gasto.setCategoria(datos.categoria());
        gasto.getParticipantes().clear();
        entityManager.flush();
        for (int i = 0; i < datos.participantesIds().size(); i++) {
            Long id = datos.participantesIds().get(i);
            gasto.getParticipantes().add(new ParticipanteGasto(gasto, datos.usuarios().get(id), datos.reparto().get(id), i));
        }
    }

    private void validarIntervalo(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosGastoException("La fecha inicial no puede ser posterior a la fecha final");
        }
    }

    private record DatosGasto(String concepto, BigDecimal importe, LocalDate fecha, CategoriaGasto categoria,
                              Usuario pagador, List<Long> participantesIds, Map<Long, Usuario> usuarios,
                              Map<Long, BigDecimal> reparto) { }
}
