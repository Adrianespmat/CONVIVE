package es.convive.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "participantes_gasto", uniqueConstraints = @UniqueConstraint(name = "uk_participante_gasto", columnNames = {"gasto_id", "usuario_id"}))
public class ParticipanteGasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gasto_id", nullable = false, foreignKey = @ForeignKey(name = "fk_participantes_gasto"))
    private Gasto gasto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_participantes_usuario"))
    private Usuario usuario;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 10, fraction = 2)
    @Column(name = "importe_asignado", nullable = false, precision = 12, scale = 2)
    private BigDecimal importeAsignado;

    @Column(name = "orden_participante", nullable = false)
    private Integer ordenParticipante;

    protected ParticipanteGasto() { }

    public ParticipanteGasto(Gasto gasto, Usuario usuario, BigDecimal importeAsignado, Integer ordenParticipante) {
        this.gasto = gasto;
        this.usuario = usuario;
        this.importeAsignado = importeAsignado;
        this.ordenParticipante = ordenParticipante;
    }

    public Long getId() { return id; }
    public Gasto getGasto() { return gasto; }
    public Usuario getUsuario() { return usuario; }
    public BigDecimal getImporteAsignado() { return importeAsignado; }
    public Integer getOrdenParticipante() { return ordenParticipante; }

    public void setImporteAsignado(BigDecimal importeAsignado) { this.importeAsignado = importeAsignado; }
    public void setOrdenParticipante(Integer ordenParticipante) { this.ordenParticipante = ordenParticipante; }
}
