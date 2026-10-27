package es.convive.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "membresias", uniqueConstraints = @UniqueConstraint(name = "uk_membresias_usuario_piso", columnNames = {"usuario_id", "piso_id"}))
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_membresias_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "piso_id", nullable = false, foreignKey = @ForeignKey(name = "fk_membresias_piso"))
    private Piso piso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    protected Membresia() { }

    public Membresia(Usuario usuario, Piso piso, Rol rol) {
        this.usuario = usuario;
        this.piso = piso;
        this.rol = rol;
        this.fechaAlta = LocalDate.now();
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Piso getPiso() { return piso; }
    public Rol getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public LocalDate getFechaBaja() { return fechaBaja; }

    public void setRol(Rol rol) { this.rol = rol; }
    public void setActivo(boolean activo) {
        this.activo = activo;
        this.fechaBaja = activo ? null : LocalDate.now();
    }
}
