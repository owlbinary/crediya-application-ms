package co.com.crediya.model;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParametrosPaginacionTest {

    @Test
    void testConstructorPorDefecto() {
        ParametrosPaginacion parametros = new ParametrosPaginacion();
        
        assertNull(parametros.getPagina());
        assertNull(parametros.getTamanio());
        assertNull(parametros.getEstados());
        assertNull(parametros.getOrden());
    }

    @Test
    void testConstructorConTodosLosArgumentos() {
        Integer pagina = 1;
        Integer tamanio = 10;
        List<EstadoSolicitud> estados = Arrays.asList(EstadoSolicitud.PENDIENTE_REVISION, EstadoSolicitud.APROBADO);
        String orden = "fechaModificacion";
        
        ParametrosPaginacion parametros = new ParametrosPaginacion(pagina, tamanio, estados, orden);
        
        assertEquals(pagina, parametros.getPagina());
        assertEquals(tamanio, parametros.getTamanio());
        assertEquals(estados, parametros.getEstados());
        assertEquals(orden, parametros.getOrden());
    }

    @Test
    void testBuilderPattern() {
        List<EstadoSolicitud> estados = Arrays.asList(EstadoSolicitud.RECHAZADO);
        
        ParametrosPaginacion parametros = ParametrosPaginacion.builder()
                .pagina(2)
                .tamanio(50)
                .estados(estados)
                .orden("solicitudId")
                .build();
        
        assertEquals(2, parametros.getPagina());
        assertEquals(50, parametros.getTamanio());
        assertEquals(estados, parametros.getEstados());
        assertEquals("solicitudId", parametros.getOrden());
    }

    @Test
    void testBuilderPatternParcial() {
        ParametrosPaginacion parametros = ParametrosPaginacion.builder()
                .pagina(0)
                .tamanio(25)
                .build();
        
        assertEquals(0, parametros.getPagina());
        assertEquals(25, parametros.getTamanio());
        assertNull(parametros.getEstados());
        assertNull(parametros.getOrden());
    }

    @Test
    void testMetodoPorDefecto() {
        ParametrosPaginacion parametros = ParametrosPaginacion.porDefecto();
        
        assertEquals(0, parametros.getPagina());
        assertEquals(20, parametros.getTamanio());
        assertEquals("fechaCreacion", parametros.getOrden());
        assertNull(parametros.getEstados());
    }

    @Test
    void testSettersYGetters() {
        ParametrosPaginacion parametros = new ParametrosPaginacion();
        List<EstadoSolicitud> estados = Arrays.asList(EstadoSolicitud.REVISION_MANUAL);
        
        parametros.setPagina(5);
        parametros.setTamanio(100);
        parametros.setEstados(estados);
        parametros.setOrden("monto");
        
        assertEquals(5, parametros.getPagina());
        assertEquals(100, parametros.getTamanio());
        assertEquals(estados, parametros.getEstados());
        assertEquals("monto", parametros.getOrden());
    }

    @Test
    void testEqualsYHashCode() {
        List<EstadoSolicitud> estados1 = Arrays.asList(EstadoSolicitud.PENDIENTE_REVISION);
        List<EstadoSolicitud> estados2 = Arrays.asList(EstadoSolicitud.PENDIENTE_REVISION);
        
        ParametrosPaginacion parametros1 = ParametrosPaginacion.builder()
                .pagina(1)
                .tamanio(20)
                .estados(estados1)
                .orden("fechaCreacion")
                .build();
        
        ParametrosPaginacion parametros2 = ParametrosPaginacion.builder()
                .pagina(1)
                .tamanio(20)
                .estados(estados2)
                .orden("fechaCreacion")
                .build();
        
        ParametrosPaginacion parametros3 = ParametrosPaginacion.builder()
                .pagina(2)
                .tamanio(20)
                .estados(estados1)
                .orden("fechaCreacion")
                .build();
        
        assertEquals(parametros1, parametros2);
        assertEquals(parametros1.hashCode(), parametros2.hashCode());
        assertNotEquals(parametros1, parametros3);
        assertNotEquals(parametros1.hashCode(), parametros3.hashCode());
    }

    @Test
    void testToString() {
        List<EstadoSolicitud> estados = Arrays.asList(EstadoSolicitud.APROBADO, EstadoSolicitud.RECHAZADO);
        
        ParametrosPaginacion parametros = ParametrosPaginacion.builder()
                .pagina(1)
                .tamanio(15)
                .estados(estados)
                .orden("plazo")
                .build();
        
        String resultado = parametros.toString();
        
        assertNotNull(resultado);
        assertTrue(resultado.contains("ParametrosPaginacion"));
        assertTrue(resultado.contains("pagina=1"));
        assertTrue(resultado.contains("tamanio=15"));
        assertTrue(resultado.contains("orden=plazo"));
        assertTrue(resultado.contains("estados"));
    }

    @Test
    void testConEstadosVarios() {
        List<EstadoSolicitud> todosLosEstados = Arrays.asList(
                EstadoSolicitud.PENDIENTE_REVISION,
                EstadoSolicitud.REVISION_MANUAL,
                EstadoSolicitud.APROBADO,
                EstadoSolicitud.RECHAZADO
        );
        
        ParametrosPaginacion parametros = ParametrosPaginacion.builder()
                .pagina(0)
                .tamanio(50)
                .estados(todosLosEstados)
                .orden("estado")
                .build();
        
        assertEquals(4, parametros.getEstados().size());
        assertTrue(parametros.getEstados().contains(EstadoSolicitud.PENDIENTE_REVISION));
        assertTrue(parametros.getEstados().contains(EstadoSolicitud.REVISION_MANUAL));
        assertTrue(parametros.getEstados().contains(EstadoSolicitud.APROBADO));
        assertTrue(parametros.getEstados().contains(EstadoSolicitud.RECHAZADO));
    }

    @Test
    void testValoresLimite() {
        ParametrosPaginacion parametros = ParametrosPaginacion.builder()
                .pagina(0)
                .tamanio(1)
                .build();
        
        assertEquals(0, parametros.getPagina());
        assertEquals(1, parametros.getTamanio());
        
        parametros.setPagina(Integer.MAX_VALUE);
        parametros.setTamanio(Integer.MAX_VALUE);
        
        assertEquals(Integer.MAX_VALUE, parametros.getPagina());
        assertEquals(Integer.MAX_VALUE, parametros.getTamanio());
    }
}
