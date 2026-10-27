package es.convive.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gastos")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "piso_id", nullable = false, foreignKey = @ForeignKey(name = "fk_gastos_piso"))
    private Piso piso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creado_por_usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_gastos_creador"))
    private Usuario creadoPor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pagador_usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_gastos_pagador"))
    private Usuario pagador;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String concepto;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @NotNull
    @Column(name = "fecha_gasto", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaGasto categoria;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    @OneToMany(mappedBy = "gasto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipanteGasto> participantes = new ArrayList<>();

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        fechaCreacion = now;
        fechaActualizacion = now;
    }

    @PreUpdate
    void alActualizar() {
        fechaActualizacion = Instant.now();
    }

    public Long getId() { return id; }
    public Piso getPiso() { return piso; }
    public Usuario getCreadoPor() { return creadoPor; }
    public Usuario getPagador() { return pagador; }
    public String getConcepto() { return concepto; }
    public BigDecimal getImporte() { return importe; }
    public LocalDate getFecha() { return fecha; }
    public CategoriaGasto getCategoria() { return categoria; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public Instant getFechaActualizacion() { return fechaActualizacion; }
    public List<ParticipanteGasto> getParticipantes() { return participantes; }

    public void setPiso(Piso piso) { this.piso = piso; }
    public void setCreadoPor(Usuario creadoPor) { this.creadoPor = creadoPor; }
    public void setPagador(Usuario pagador) { this.pagador = pagador; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setCategoria(CategoriaGasto categoria) { this.categoria = categoria; }
}
