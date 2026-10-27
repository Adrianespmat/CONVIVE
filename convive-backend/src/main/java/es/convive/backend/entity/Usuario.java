package es.convive.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(name = "uk_usuarios_correo", columnNames = "correo"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 180)
    @Column(nullable = false, unique = true, length = 180)
    private String correo;

    @NotBlank
    @Size(max = 255)
    @Column(name = "hash_contrasena", nullable = false, length = 255)
    private String hashContrasena;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @OneToMany(mappedBy = "usuario")
    private List<Membresia> membresias = new ArrayList<>();

    @PrePersist
    void onCreate() {
        fechaCreacion = Instant.now();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getHashContrasena() { return hashContrasena; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public List<Membresia> getMembresias() { return membresias; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setHashContrasena(String hashContrasena) { this.hashContrasena = hashContrasena; }
}
