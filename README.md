# Backend A - Symplifica

Este repositorio corresponde al **Backend A** del sistema Symplifica. Es un microservicio construido con **Java 21** y **Spring Boot 4.1.0**.

## Arquitectura y Clean Code

El proyecto aplica sólidamente la Programación Orientada a Objetos (**POO**) y el patrón arquitectónico **MVC (Model-View-Controller)**:
- **Models / Entities**: Mapeo directo con la base de datos a través de Spring Data JPA y Hibernate.
- **Controllers**: Exponen la API REST, encargados únicamente de manejar las peticiones HTTP y retornar respuestas estructuradas (JSON).
- **Services**: Contienen toda la lógica de negocio. Sirven como intermediarios entre los controladores y los repositorios de datos.
- **Repositories**: Interfaces de abstracción de persistencia que interactúan con **PostgreSQL**.

### Características y Principios:
- **Seguridad**: Autenticación manejada mediante tokens **JWT** (JSON Web Tokens).
- **Comunicación Asíncrona**: Integración con **Apache Kafka** para emitir y consumir eventos hacia/desde otros microservicios (como Backend B).
- **Migraciones de Base de Datos**: Versionado estructurado con **Flyway**.
- **Clean Code**: Uso de **Lombok** para reducir código repetitivo y clases limpias. Inyección de dependencias vía constructor (Spring IoC).

## Configuraciones y Entorno

El servicio está integrado a un `docker-compose.yml` general del ecosistema, mediante el servicio `srv-backend-a`.

### Variables de Entorno Críticas
Estas variables se proveen desde un archivo `.env` ubicado en la raíz del proyecto, inyectándose en el contenedor o entorno de Spring (`application.yml` / `application.properties`):
- `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_USER`, `POSTGRES_PASSWORD`: Credenciales de PostgreSQL.
- `EMPLOYEE_DB`: Base de datos asignada para este servicio (mapeada en el docker-compose desde `${BACKEND_A_DATABASE}`).
- `JWT_SECRET`: Llave secreta compartida con Backend B para generar y validar tokens de autenticación.
- `APP_SECRET`: Secreto adicional para el cifrado interno.
- `KAFKA_BOOTSTRAP_SERVERS`: Dirección del cluster Kafka (en Docker: `srv-kafka:9092`).

### Infraestructura Integrada
- **Kafka**: Ejecutándose en modo KRaft (sin Zookeeper), con el puerto `9092` internamente y `29092` para el host.
- **PostgreSQL**: Se levanta un único contenedor con `POSTGRES_MULTIPLE_DATABASES` creando las bases para ambos backends simultáneamente. El puerto accesible localmente es `5435`.
- **Backend A**: La API Spring Boot es expuesta en el host en el puerto **8080**.

## Requisitos Previos

- **Java Development Kit (JDK) 21**
- **Maven** (o usar el Wrapper incluido)
- **PostgreSQL** y **Apache Kafka** (si se corre fuera de Docker)

## Instrucciones de Ejecución

1. **Configuración de la Base de Datos:**
   Asegúrate de que PostgreSQL y Kafka estén corriendo y las variables de entorno configuradas. Las migraciones de Flyway se aplicarán automáticamente al arrancar.

2. **Compilar el proyecto:**
   ```bash
   ./mvnw clean install
   ```
   *(En Windows utiliza `mvnw.cmd clean install`)*

3. **Ejecutar la aplicación (fuera de Docker):**
   ```bash
   ./mvnw spring-boot:run
   ```
   Por defecto, la API estará disponible en `http://localhost:8080`.

## Testing

Para ejecutar los tests automatizados:
```bash
./mvnw test
```
