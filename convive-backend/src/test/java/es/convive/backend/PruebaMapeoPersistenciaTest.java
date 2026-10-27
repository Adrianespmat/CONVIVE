package es.convive.backend;

import es.convive.backend.entity.Piso;
import es.convive.backend.entity.Membresia;
import es.convive.backend.entity.Rol;
import es.convive.backend.entity.Usuario;
import es.convive.backend.repository.PisoRepository;
import es.convive.backend.repository.MembresiaRepository;
import es.convive.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PruebaMapeoPersistenciaTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PisoRepository pisoRepository;

    @Autowired
    private MembresiaRepository membresiaRepository;

    @Test
    void persisteUsuarioPisoYMembresiaConRelaciones() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setCorreo("ana@example.com");
        usuario.setHashContrasena("hash-de-prueba");
        usuario = usuarioRepository.saveAndFlush(usuario);

        Piso piso = new Piso();
        piso.setNombre("Piso Centro");
        piso.setDireccion("Calle Mayor 1");
        piso.setCodigoInvitacion("ABC12345");
        piso = pisoRepository.saveAndFlush(piso);

        Membresia membresia = membresiaRepository.saveAndFlush(new Membresia(usuario, piso, Rol.ADMINISTRADOR));

        assertThat(membresia.getId()).isNotNull();
        assertThat(membresiaRepository.findByUsuarioIdAndActivoTrue(usuario.getId()))
                .get()
                .extracting(Membresia::getRol)
                .isEqualTo(Rol.ADMINISTRADOR);
        assertThat(pisoRepository.findByCodigoInvitacionAndActivoTrue("ABC12345")).isPresent();
    }
}
