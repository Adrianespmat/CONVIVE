package es.convive.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class ServicioJwt {
    private final SecretKey clave;
    private final long expiracionMilisegundos;

    public ServicioJwt(@Value("${convive.seguridad.jwt-secret}") String secreto,
                       @Value("${convive.seguridad.expiracion-milisegundos}") long expiracionMilisegundos) {
        byte[] bytes;
        try {
            bytes = Decoders.BASE64.decode(secreto);
        } catch (RuntimeException ex) {
            bytes = secreto.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
        if (bytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 bytes");
        }
        this.clave = Keys.hmacShaKeyFor(bytes);
        this.expiracionMilisegundos = expiracionMilisegundos;
    }

    public String generarToken(UserDetails detalles) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(detalles.getUsername())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMilisegundos))
                .signWith(clave)
                .compact();
    }

    public String extraerCorreo(String token) { return extraerClaim(token, Claims::getSubject); }

    public boolean esValido(String token, UserDetails detalles) {
        try {
            return detalles.getUsername().equalsIgnoreCase(extraerCorreo(token)) && !estaCaducado(token);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private boolean estaCaducado(String token) { return extraerClaim(token, Claims::getExpiration).before(new Date()); }

    private <T> T extraerClaim(String token, Function<Claims, T> extractor) {
        return extractor.apply(Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload());
    }
}
