package co.com.crediya.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService - Servicio de autenticación JWT")
class JwtServiceTest {

    private JwtService jwtService;
    private SecretKey secretKey;
    private String validToken;

    @BeforeEach
    void setUp() {
        String secret = "test12223ADSUYDASIJAS61526132-123123123gsaug";
        jwtService = new JwtService(secret);
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());

        Date fechaExpiracion = new Date(System.currentTimeMillis() + 3600000);
        validToken = Jwts.builder()
                .subject("test@ejemplo.com")
                .claim("userId", 123)
                .claim("nombre", "Juan")
                .claim("apellido", "Pérez")
                .claim("idRol", 1)
                .issuedAt(new Date())
                .expiration(fechaExpiracion)
                .signWith(secretKey)
                .compact();
    }

    @Test
    @DisplayName("Debe validar token JWT correctamente")
    void debeValidarTokenJwtCorrectamente() {
        boolean resultado = jwtService.validarToken(validToken);
        
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Debe rechazar token inválido")
    void debeRechazarTokenInvalido() {
        String tokenInvalido = "token.invalido.test";
        
        boolean resultado = jwtService.validarToken(tokenInvalido);
        
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Debe extraer email del token")
    void debeExtraerEmailDelToken() {
        String email = jwtService.extraerEmail(validToken);
        
        assertThat(email).isEqualTo("test@ejemplo.com");
    }

    @Test
    @DisplayName("Debe extraer ID del usuario del token")
    void debeExtraerIdUsuarioDelToken() {
        String idUsuario = jwtService.extraerIdUsuario(validToken);
        
        assertThat(idUsuario).isEqualTo("123");
    }

    @Test
    @DisplayName("Debe extraer nombre del token")
    void debeExtraerNombreDelToken() {
        String nombre = jwtService.extraerNombre(validToken);
        
        assertThat(nombre).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Debe extraer apellido del token")
    void debeExtraerApellidoDelToken() {
        String apellido = jwtService.extraerApellido(validToken);
        
        assertThat(apellido).isEqualTo("Pérez");
    }

    @Test
    @DisplayName("Debe extraer ID del rol del token")
    void debeExtraerIdRolDelToken() {
        String idRol = jwtService.extraerIdRol(validToken);
        
        assertThat(idRol).isEqualTo("1");
    }

    @Test
    @DisplayName("Debe extraer autoridades del token")
    void debeExtraerAutoridadesDelToken() {
        Collection<GrantedAuthority> autoridades = jwtService.extraerAutoridades(validToken);
        
        assertThat(autoridades).hasSize(1);
        assertThat(autoridades.iterator().next().getAuthority()).isEqualTo("ROLE_1");
    }

    @Test
    @DisplayName("Debe rechazar token expirado")
    void debeRechazarTokenExpirado() {
        Date fechaExpiracion = new Date(System.currentTimeMillis() - 1000);
        String tokenExpirado = Jwts.builder()
                .subject("test@ejemplo.com")
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(fechaExpiracion)
                .signWith(secretKey)
                .compact();
        
        boolean resultado = jwtService.validarToken(tokenExpirado);
        
        assertThat(resultado).isFalse();
    }
}
