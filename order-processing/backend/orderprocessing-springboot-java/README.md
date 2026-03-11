# Sistema de Procesamiento de Pedidos

Este proyecto es una implementación técnica de un Sistema de Procesamiento de Pedidos para HACOM, que utiliza tecnologías reactivas modernas.

## Stack Tecnológico
- **Lenguajes / Frameworks:** Java 17, Spring Boot 3.2, Spring WebFlux
- **Base de Datos:** MongoDB Reactivo (controlador MongoDB)
- **Framework RPC:** gRPC + Protobuf
- **Modelo de Actores:** Actores Clásicos de Akka
- **Monitoreo:** Spring Boot Actuator + Micrometer (Prometheus)
- **Integración SMS:** Cliente Cloudhopper SMPP
- **Registro (Logging):** Log4j2

## Requisitos
- Java 17 instalado
- MongoDB ejecutándose localmente en el puerto `27017` sin autenticación
- Gradle (se puede usar el `gradlew` proporcionado o Gradle 8.x estándar)

## Cómo Construir y Ejecutar
Primero, compila el proyecto para generar las clases gRPC:
```sh
./gradlew clean build -x test
```

Para ejecutar la aplicación:
```sh
./gradlew bootRun
```
*Nota: El servidor gRPC se inicia en el puerto **9090** y la API de Webflux en el puerto **9898** según lo solicitado.*

## API y Endpoints

### 1. Insertar un Pedido (gRPC)
Utilizando una herramienta como `grpcurl`, puedes invocar el endpoint gRPC en `localhost:9090`.
**Definición del servicio dentro de `src/main/proto/order.proto`:**
```sh
grpcurl -plaintext -d '{
  "orderId": "ORD-001",
  "customerId": "CUST-001",
  "customerPhoneNumber": "+1234567890",
  "items": ["Item A", "Item B"]
}' localhost:9090 OrderService/InsertOrder
```
*Cuando se solicita un pedido, un actor de Akka lo procesará, lo guardará en `exampleDb` (MongoDB), emulará el envío de un SMS a través de SMPP y devolverá la respuesta de forma asíncrona.*

### 2. Consultar el Estado del Pedido (REST Webflux)
```sh
curl http://localhost:9898/api/orders/ORD-001/status
```

### 3. Consultar Total de Pedidos por Rango de Fechas (REST Webflux)
```sh
curl "http://localhost:9898/api/orders/count?start=2023-01-01T00:00:00Z&end=2026-12-31T23:59:59Z"
```

### 4. Métricas de Prometheus
```sh
curl http://localhost:8080/actuator/prometheus
```
*Busca el contador `orders_processed_total` dentro de la salida.*

## Nota de Arquitectura
- **Configuración de la Base de Datos:** La configuración de las propiedades de MongoDB se realiza de forma programática en `MongoConfig.java`.
- **Configuración de Webflux:** El puerto de Netty se inicializa en `WebfluxConfig.java`.
- **Registro (Logging):** Un archivo `log4j2.yml` configura Log4j2 con un formato de salida por consola.
