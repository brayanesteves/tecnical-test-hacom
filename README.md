# Technical Test HACOM

Bienvenido al repositorio de la prueba técnica para HACOM. Este repositorio ha sido diseñado como un monorepositorio que puede alojar múltiples proyectos y componentes relacionados.

## 🚀 Proyectos Disponibles

### 1. Sistema de Procesamiento de Pedidos (`order-processing`)
El proyecto principal de este repositorio es un sistema reactivo y asíncrono diseñado para procesar y gestionar pedidos de telecomunicaciones de manera eficiente.

🔗 **[Ir a la documentación completa de Order Processing](./order-processing/README.md)**

#### 🎯 ¿Qué contiene?
Este proyecto abarca una solución backend completa separada conceptualmente para futuras integraciones:
- **`backend/orderprocessing-springboot-java`**: El núcleo del sistema que maneja la lógica de negocio, bases de datos y APIs.
- **`frontend/orderprocessing-angular-frontend`**: Directorio preparado para la futura interfaz de usuario.
- **`REQUIREMENTS.md`**: El caso práctico original y los objetivos de la prueba técnica.

#### ⚙️ Stack Tecnológico Principal
El backend se construyó utilizando tecnologías reactivas y arquitecturas robustas:
- **Lenguaje:** Java 17
- **Framework Core:** Spring Boot 3.2 (WebFlux para programación reactiva no bloqueante)
- **Base de Datos:** MongoDB (Spring Data MongoDB Reactive)
- **Comunicación RPC:** gRPC + Protobuf (para ingesta de pedidos)
- **Procesamiento Asíncrono:** Akka Classic Actors
- **Integraciones:** Cloudhopper SMPP (para notificaciones SMS)
- **Observabilidad:** Log4j2 (YAML) y Spring Actuator (Métricas Prometheus)

---

> *Para instrucciones detalladas sobre cómo compilar, ejecutar y probar los endpoints (gRPC y REST), por favor visita el **[README.md de order-processing](./order-processing/README.md)***.
