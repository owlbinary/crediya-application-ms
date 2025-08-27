package co.com.crediya.api.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityUtils - Utilidades de Seguridad")
class SecurityUtilsTest {

    private final JwtUserPrincipal testUserPrincipal = JwtUserPrincipal.builder()
            .email("test@test.com")
            .idUsuario("123")
            .nombre("Juan")
            .apellido("Pérez")
            .idRol("1")
            .build();

    @Test
    @DisplayName("Debe obtener el usuario actual del contexto de seguridad")
    void debeObtenerUsuarioActualDelContextoDeSeguridad() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(testUserPrincipal, null);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        SecurityUtils.getCurrentUser()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .as(StepVerifier::create)
                .expectNext(testUserPrincipal)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe obtener el ID del usuario actual")
    void debeObtenerIdDelUsuarioActual() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(testUserPrincipal, null);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        SecurityUtils.getCurrentUserId()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .as(StepVerifier::create)
                .expectNext("123")
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe obtener el email del usuario actual")
    void debeObtenerEmailDelUsuarioActual() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(testUserPrincipal, null);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        SecurityUtils.getCurrentUserEmail()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .as(StepVerifier::create)
                .expectNext("test@test.com")
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar contexto de seguridad vacío")
    void debeManejarContextoDeSeguridadVacio() {
        SecurityUtils.getCurrentUser()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Debe manejar contexto de seguridad vacío para getCurrentUserId")
    void debeManejarContextoDeSeguridadVacioParaGetCurrentUserId() {
        SecurityUtils.getCurrentUserId()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Debe manejar contexto de seguridad vacío para getCurrentUserEmail")
    void debeManejarContextoDeSeguridadVacioParaGetCurrentUserEmail() {
        SecurityUtils.getCurrentUserEmail()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Debe manejar autenticación con principal diferente")
    void debeManejarAutenticacionConPrincipalDiferente() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("otherPrincipal", null);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        SecurityUtils.getCurrentUser()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .as(StepVerifier::create)
                .expectError(ClassCastException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe ser una clase final")
    void debeSerUnaClaseFinal() {
        assertThat(Modifier.isFinal(SecurityUtils.class.getModifiers())).isTrue();
    }

    @Test
    @DisplayName("Debe tener constructor privado")
    void debeTenerConstructorPrivado() throws Exception {
        Constructor<SecurityUtils> constructor = SecurityUtils.class.getDeclaredConstructor();
        
        assertThat(constructor).isNotNull();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        
        constructor.setAccessible(true);
        SecurityUtils instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }

    @Test
    @DisplayName("Debe tener métodos estáticos")
    void debeTenerMetodosEstaticos() throws Exception {
        Method getCurrentUser = SecurityUtils.class.getDeclaredMethod("getCurrentUser");
        Method getCurrentUserId = SecurityUtils.class.getDeclaredMethod("getCurrentUserId");
        Method getCurrentUserEmail = SecurityUtils.class.getDeclaredMethod("getCurrentUserEmail");

        assertThat(getCurrentUser).isNotNull();
        assertThat(Modifier.isStatic(getCurrentUser.getModifiers())).isTrue();
        assertThat(getCurrentUser.getReturnType()).isEqualTo(Mono.class);

        assertThat(getCurrentUserId).isNotNull();
        assertThat(Modifier.isStatic(getCurrentUserId.getModifiers())).isTrue();
        assertThat(getCurrentUserId.getReturnType()).isEqualTo(Mono.class);

        assertThat(getCurrentUserEmail).isNotNull();
        assertThat(Modifier.isStatic(getCurrentUserEmail.getModifiers())).isTrue();
        assertThat(getCurrentUserEmail.getReturnType()).isEqualTo(Mono.class);
    }

    @Test
    @DisplayName("Debe tener nombre correcto de la clase")
    void debeTenerNombreCorrectoDeLaClase() {
        assertThat(SecurityUtils.class.getSimpleName()).isEqualTo("SecurityUtils");
    }

    @Test
    @DisplayName("Debe estar en el paquete correcto")
    void debeEstarEnElPaqueteCorrecto() {
        assertThat(SecurityUtils.class.getPackage().getName()).isEqualTo("co.com.crediya.api.security");
    }

    @Test
    @DisplayName("Debe tener métodos públicos")
    void debeTenerMetodosPublicos() throws Exception {
        Method getCurrentUser = SecurityUtils.class.getDeclaredMethod("getCurrentUser");
        Method getCurrentUserId = SecurityUtils.class.getDeclaredMethod("getCurrentUserId");
        Method getCurrentUserEmail = SecurityUtils.class.getDeclaredMethod("getCurrentUserEmail");

        assertThat(Modifier.isPublic(getCurrentUser.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(getCurrentUserId.getModifiers())).isTrue();
        assertThat(Modifier.isPublic(getCurrentUserEmail.getModifiers())).isTrue();
    }

    @Test
    @DisplayName("getCurrentUser debe transformar correctamente el contexto")
    void getCurrentUserDebeTransformarCorrectamenteElContexto() {
        JwtUserPrincipal customPrincipal = JwtUserPrincipal.builder()
                .email("custom@example.com")
                .idUsuario("456")
                .nombre("María")
                .apellido("García")
                .idRol("2")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(customPrincipal, null);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        SecurityUtils.getCurrentUser()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .as(StepVerifier::create)
                .expectNext(customPrincipal)
                .verifyComplete();
    }

    @Test
    @DisplayName("getCurrentUserId debe extraer correctamente el ID")
    void getCurrentUserIdDebeExtraerCorrectamenteElId() {
        JwtUserPrincipal customPrincipal = JwtUserPrincipal.builder()
                .email("user@example.com")
                .idUsuario("789")
                .nombre("Carlos")
                .apellido("López")
                .idRol("3")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(customPrincipal, null);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        SecurityUtils.getCurrentUserId()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .as(StepVerifier::create)
                .expectNext("789")
                .verifyComplete();
    }

    @Test
    @DisplayName("getCurrentUserEmail debe extraer correctamente el email")
    void getCurrentUserEmailDebeExtraerCorrectamenteElEmail() {
        JwtUserPrincipal customPrincipal = JwtUserPrincipal.builder()
                .email("admin@example.com")
                .idUsuario("999")
                .nombre("Ana")
                .apellido("Martínez")
                .idRol("4")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(customPrincipal, null);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        SecurityUtils.getCurrentUserEmail()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .as(StepVerifier::create)
                .expectNext("admin@example.com")
                .verifyComplete();
    }
}
