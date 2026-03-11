# Prueba Técnica Hacom: Sistema de Procesamiento de Pedidos

Este repositorio contiene la implementación de la prueba técnica de Procesamiento de Pedidos. El proyecto está organizado en una estructura modular con componentes separados para el backend y el frontend.

## Estructura del Proyecto

El proyecto sigue una organización modular para garantizar la claridad y la separación de responsabilidades:

- `backend/orderprocessing-springboot-java`: El servicio principal del backend construido con Spring Boot, WebFlux, gRPC y Akka.
- `frontend/orderprocessing-angular-frontend`: Espacio reservado para la aplicación frontend basada en Angular.
- `REQUIREMENTS.md`: Los requisitos técnicos originales y las especificaciones para este caso de estudio.

---

## Backend: orderprocessing-springboot-java

El backend es un sistema reactivo diseñado para procesar pedidos de manera eficiente utilizando una arquitectura basada en actores y comunicación gRPC.

### Tecnologías Clave
- **Java 17** y **Spring Boot 3.2**
- **Spring WebFlux**: Para endpoints REST no bloqueantes.
- **gRPC y Protobuf**: RPC de alto rendimiento para la inserción de pedidos.
- **Actores Clásicos de Akka**: Para el procesamiento asíncrono de pedidos y desacoplamiento de la lógica.
- **MongoDB Reactivo**: Persistencia de datos utilizando controladores reactivos.
- **Log4j2**: Configurado a través de `log4j2.yml`.
- **Spring Actuator**: Exponiendo métricas de Prometheus en el puerto 8080.
- **Cloudhopper SMPP**: Integración para notificaciones por SMS.

### Configuración Programática
Como se solicitó, los siguientes elementos están configurados programáticamente:
- **Conexión a MongoDB**: Gestionada en `MongoConfig.java` utilizando propiedades personalizadas.
- **Puerto del Servidor WebFlux**: Establecido en `WebfluxConfig.java` a través de la propiedad `apiPort`.

### Cómo Construir y Ejecutar
1. Navega al directorio del backend:
   ```sh
   cd backend/orderprocessing-springboot-java
   ```
2. Construye el proyecto (genera las clases gRPC):
   ```sh
   ./gradlew clean build -x test
   ```
3. Ejecuta la aplicación:
   ```sh
   ./gradlew bootRun
   ```

### Comandos de Prueba Rápida
- **Inserción de Pedido por gRPC (Puerto 9090)**:
  ```sh
  grpcurl -plaintext -d '{"orderId": "ORD-001", "customerId": "CUST-001", "customerPhoneNumber": "+123456789", "items": ["Item1"]}' localhost:9090 OrderService/InsertOrder
  ```
- **Estado del Pedido por REST (Puerto 9898)**:
  ```sh
  curl http://localhost:9898/api/orders/ORD-001/status
  ```
- **Conteo de Pedidos por Rango de Fechas por REST**:
  ```sh
  curl "http://localhost:9898/api/orders/count?start=2024-01-01T00:00:00Z&end=2024-12-31T23:59:59Z"
  ```
- **Métricas de Prometheus (Puerto 8080)**:
  ```sh
  curl http://localhost:8080/actuator/prometheus
  ```

---

## Aspectos Destacados de los Criterios de Evaluación

Esta implementación aborda los criterios solicitados de la siguiente manera:
- **Claridad y Estructura**: Organización modular con una separación clara entre comunicación (gRPC), procesamiento (Akka) y almacenamiento (Mongo).
- **Entendimiento**: Uso integral de patrones reactivos en todo el stack.
- **Integración de Librerías**: Integración fluida de Cloudhopper SMPP dentro de un flujo de actores de Akka.
- **Buenas Prácticas**: Uso de YAML para el registro (logging) y la configuración de la aplicación, definiciones de beans programáticas y modelos de dominio limpios.
