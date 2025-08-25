# Crediya Application Microservice

Microservicio de gestión de solicitudes de préstamos desarrollado con arquitectura hexagonal y programación reactiva para el sistema Crediya.

## 🏗️ Arquitectura

Este proyecto sigue los principios de **Arquitectura Hexagonal (Clean Architecture)** usando el [Plugin de Bancolombia](https://bancolombia.github.io/scaffold-clean-architecture) con las siguientes capas:

- **Domain**: Entidades de negocio y reglas del dominio
- **Use Cases**: Casos de uso de la aplicación
- **Infrastructure**: Adaptadores para entrada y salida
- **Applications**: Configuración y punto de entrada de la aplicación

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

### Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio como `Solicitud`, `EstadoSolicitud` y `TipoPrestamo`.

### Use Cases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema como `RegistrarSolicitudUseCase`, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points.

### Infrastructure

#### Driven Adapters

Los driven adapter representan implementaciones externas como conexiones a bases de datos PostgreSQL con R2DBC para persistencia reactiva de solicitudes.

#### Entry Points

Los entry points representan los puntos de entrada de la aplicación como controladores REST reactivos para gestión de solicitudes.

#### Application

Este módulo ensambla los distintos módulos, resuelve dependencias y crea los beans de los casos de uso automáticamente mediante `@ComponentScan`.

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 3.5.4**
- **Spring WebFlux** (Programación Reactiva)
- **Spring Data R2DBC** (Base de datos reactiva)
- **PostgreSQL**
- **Gradle 8+** (Gestión de dependencias)
- **JUnit 5** (Testing)
- **Mockito** (Mocking)
- **Reactor Test** (Testing reactivo)
- **Jacoco** (Cobertura de código)
- **PiTest** (Mutation testing)
- **OpenAPI 3/Swagger** (Documentación de API)
- **MapStruct** (Mapeo entre DTOs y entidades)

## 🔗 Endpoints API

### Gestión de Solicitudes

#### POST /api/v1/solicitudes
Registra una nueva solicitud de préstamo.

**Request:**
```json
{
  "documentoIdentidad": "12345678",
  "monto": 1000000.00,
  "plazo": 12,
  "tipoPrestamoId": "1"
}
```

**Response (201):**
```json
{
  "id": "1",
  "documentoIdentidad": "12345678",
  "monto": 1000000.00,
  "plazo": 12,
  "tipoPrestamoId": "1",
  "estado": "PENDIENTE_REVISION",
  "estadoDescripcion": "Pendiente de revisión",
  "fechaCreacion": "2025-08-24T10:30:00",
  "fechaActualizacion": "2025-08-24T10:30:00"
}
```

## 🛠️ Instalación y Configuración

### Prerrequisitos
- Java 21
- PostgreSQL 13+
- Gradle 8+

### Ejecución

```bash
# Clonar repositorio
git clone <repository-url>
cd crediya-application-ms

# Ejecutar aplicación
./gradlew bootRun

# La aplicación estará disponible en http://localhost:8080
```

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
./gradlew test

# Tests con reporte de cobertura
./gradlew test jacocoTestReport

# Mutation testing
./gradlew pitest

# Reporte combinado
./gradlew jacocoMergedReport
```

### Cobertura de Código
- **Objetivo**: 90% de cobertura de líneas
- **Actual**: 96% de cobertura alcanzada
- **Herramientas**: Jacoco + PiTest
- **Reportes**: `build/reports/jacocoHtml/index.html`

## 📚 Documentación API

La documentación completa de la API está disponible en:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔒 Gestión de Excepciones

- **Manejo Reactivo**: GlobalExceptionHandler con `@RestControllerAdvice`
- **Excepciones Específicas**: `DatosInvalidosException`, `TipoPrestamoNoExisteException`
- **Respuestas Estructuradas**: Códigos de error consistentes
- **Validación**: Bean Validation en DTOs

## 🏗️ Estructura del Proyecto

```
crediya-application-ms/
├── applications/
│   └── app-service/          # Configuración principal y MainApplication
├── domain/
│   ├── model/                # Entidades de dominio (Solicitud, EstadoSolicitud)
│   └── usecase/              # Casos de uso (RegistrarSolicitudUseCase)
├── infrastructure/
│   ├── driven-adapters/
│   │   └── r2dbc-postgresql/ # Adaptador de BD reactiva
│   └── entry-points/
│       └── reactive-web/     # Controladores REST reactivos
└── deployment/               # Configuración de despliegue
```

## 📈 Monitoreo

- **Health Check**: `/actuator/health`
- **Métricas**: `/actuator/metrics`
- **Info**: `/actuator/info`

---

**Artículo de referencia**: [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)
