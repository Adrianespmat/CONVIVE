package es.convive.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pisos", uniqueConstraints = @UniqueConstraint(name = "uk_pisos_codigo_invitacion", columnNames = "codigo_invitacion"))
public class Piso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String direccion;

    @NotBlank
    @Size(max = 32)
    @Column(name = "codigo_invitacion", nullable = false, unique = true, length = 32)
    private String codigoInvitacion;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @OneToMany(mappedBy = "piso")
    private List<Membresia> membresias = new ArrayList<>();

    @OneToMany(mappedBy = "piso")
    private List<Gasto> gastos = new ArrayList<>();

    @PrePersist
    void onCreate() {
        fechaCreacion = Instant.now();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getCodigoInvitacion() { return codigoInvitacion; }
    public boolean isActivo() { return activo; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public List<Membresia> getMembresias() { return membresias; }
    public List<Gasto> getGastos() { return gastos; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setCodigoInvitacion(String codigoInvitacion) { this.codigoInvitacion = codigoInvitacion; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
