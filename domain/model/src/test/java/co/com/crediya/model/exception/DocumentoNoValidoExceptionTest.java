package co.com.crediya.model.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentoNoValidoExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajeSimple() {
        String mensaje = "El documento no existe en el sistema";
        
        DocumentoNoValidoException excepcion = new DocumentoNoValidoException(mensaje);
        
        assertThat(excepcion.getMessage()).isEqualTo(mensaje);
        assertThat(excepcion.getCause()).isNull();
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    void deberiaCrearExcepcionConDocumentoYMensaje() {
        String documentoIdentidad = "12345678";
        String mensaje = "no se encuentra registrado";
        String mensajeEsperado = "Documento 12345678 no es válido: no se encuentra registrado";
        
        DocumentoNoValidoException excepcion = new DocumentoNoValidoException(documentoIdentidad, mensaje);
        
        assertThat(excepcion.getMessage()).isEqualTo(mensajeEsperado);
        assertThat(excepcion.getCause()).isNull();
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    void deberiaCrearExcepcionConParametrosNulos() {
        DocumentoNoValidoException excepcionMensajeNulo = new DocumentoNoValidoException((String) null);
        DocumentoNoValidoException excepcionDocumentoNulo = new DocumentoNoValidoException(null, "mensaje");
        DocumentoNoValidoException excepcionAmbosNulos = new DocumentoNoValidoException(null, null);
        
        assertThat(excepcionMensajeNulo.getMessage()).isNull();
        
        assertThat(excepcionDocumentoNulo.getMessage())
            .isEqualTo("Documento null no es válido: mensaje");
        
        assertThat(excepcionAmbosNulos.getMessage())
            .isEqualTo("Documento null no es válido: null");
    }

    @Test
    void deberiaCrearExcepcionConParametrosVacios() {
        String documentoVacio = "";
        String mensajeVacio = "";
        String mensajeEsperado = "Documento  no es válido: ";
        
        DocumentoNoValidoException excepcionMensajeVacio = new DocumentoNoValidoException(mensajeVacio);
        DocumentoNoValidoException excepcionDocumentoVacio = new DocumentoNoValidoException(documentoVacio, "mensaje");
        DocumentoNoValidoException excepcionAmbosVacios = new DocumentoNoValidoException(documentoVacio, mensajeVacio);
        
        assertThat(excepcionMensajeVacio.getMessage()).isEqualTo(mensajeVacio);
        
        assertThat(excepcionDocumentoVacio.getMessage())
            .isEqualTo("Documento  no es válido: mensaje");
        
        assertThat(excepcionAmbosVacios.getMessage())
            .isEqualTo(mensajeEsperado);
    }

    @Test
    void deberiaFormatearCorrectamenteMensajeConCaracteresEspeciales() {
        String documentoIdentidad = "CC-12345678";
        String mensaje = "formato inválido: debe contener solo números";
        String mensajeEsperado = "Documento CC-12345678 no es válido: formato inválido: debe contener solo números";
        
        DocumentoNoValidoException excepcion = new DocumentoNoValidoException(documentoIdentidad, mensaje);
        
        assertThat(excepcion.getMessage()).isEqualTo(mensajeEsperado);
    }

    @Test
    void deberiaFormatearCorrectamenteMensajeConNumeros() {
        String documentoIdentidad = "987654321";
        String mensaje = "longitud debe ser entre 8 y 12 dígitos";
        String mensajeEsperado = "Documento 987654321 no es válido: longitud debe ser entre 8 y 12 dígitos";
        
        DocumentoNoValidoException excepcion = new DocumentoNoValidoException(documentoIdentidad, mensaje);
        
        assertThat(excepcion.getMessage()).isEqualTo(mensajeEsperado);
    }

    @Test
    void deberiaManejarMensajesLargos() {
        String documentoIdentidad = "12345678";
        String mensajeLargo = "Este es un mensaje muy largo que describe en detalle por qué el documento no es válido " +
            "incluyendo múltiples razones como formato incorrecto, longitud inadecuada, caracteres no permitidos " +
            "y falta de verificación en las bases de datos externas del sistema de validación";
        String mensajeEsperado = "Documento 12345678 no es válido: " + mensajeLargo;
        
        DocumentoNoValidoException excepcion = new DocumentoNoValidoException(documentoIdentidad, mensajeLargo);
        
        assertThat(excepcion.getMessage()).isEqualTo(mensajeEsperado);
    }

    @Test
    void deberiaSerConsistenteConEquals() {
        String mensaje = "Documento no válido";
        
        DocumentoNoValidoException excepcion1 = new DocumentoNoValidoException(mensaje);
        DocumentoNoValidoException excepcion2 = new DocumentoNoValidoException(mensaje);
        
        assertThat(excepcion1.getMessage()).isEqualTo(excepcion2.getMessage());
        assertThat(excepcion1.getClass()).isEqualTo(excepcion2.getClass());
    }

    @Test
    void deberiaSerCompatibleConStackTrace() {
        String documentoIdentidad = "12345678";
        String mensaje = "no existe en el sistema";
        
        DocumentoNoValidoException excepcion = new DocumentoNoValidoException(documentoIdentidad, mensaje);
        
        assertThat(excepcion.getStackTrace()).isNotNull();
        assertThat(excepcion.getStackTrace()).isNotEmpty();
        assertThat(excepcion.getStackTrace()[0].getClassName())
            .contains("DocumentoNoValidoExceptionTest");
    }
}
