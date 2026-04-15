# 🚀 NEXUS-AI: Smart Log System

### 💡 ¿Qué es Nexus AI?
Nexus AI es una aplicación software destinada a facilitar el manejo de errores en aplicaciones externas, transformando logs técnicos en información accionable y legible.

### 🎯 Problema y Solución
* **El Problema:** Los logs tradicionales suelen tener nombres técnicos engorrosos y descripciones crípticas que retrasan el diagnóstico y la resolución.
* **La Solución:** Nexus AI facilita el manejo de errores gracias a su conversión de términos técnicos a descripciones fáciles de leer, ofreciendo incluso soluciones automáticas para los mismos a través de inteligencia artificial.

### 🤖 ¿Por qué el uso de una LLM (GitCopilot)?
El uso de un Modelo de Lenguaje permite analizar trazas de error en tiempo real, traduciendo semánticas complejas a una explicación descriptiva y sencilla en un periodo corto de tiempo, eliminando la búsqueda manual en documentación o foros.

---

### 🛠️ Stack Tecnológico
* **Java 21:** Uso de **Virtual Threads** para procesar múltiples errores de forma concurrente sin bloquear el sistema.
* **Spring Boot 3.4:** Framework base para la construcción de la API REST.
* **PostgreSQL:** Persistencia robusta para asegurar la trazabilidad y auditoría de los fallos.
* **GITHUB COPILOT:** Motor de IA para el diagnóstico, clasificación y generación de soluciones.

---

### ⚙️ Flujo del Sistema
1. **Ingesta:** Recepción del JSON vía REST Controller.
2. **Persistencia Inicial:** Registro inmediato en **PostgreSQL** para asegurar la trazabilidad (**Atomicidad**).
3. **Procesamiento Concurrente:** Disparo de un **Virtual Thread** por cada petición para gestionar la latencia del LLM de forma eficiente.
4. **Enriquecimiento con IA:** Clasificación (Back/Front), Priorización y Generación de Soluciones.
5. **Cierre de Ciclo:** Actualización del registro en DB con la solución y respuesta en tiempo real al cliente.
   * *Nota:* En caso de indisponibilidad del LLM, el registro se marca con estado `SOLUCIÓN PENDIENTE`.

---

### 🔌 Contrato de la API

#### Datos de Entrada (Request JSON)
Los siguientes campos son necesarios para que la IA realice un diagnóstico preciso:

| Campo | Descripción |
| :--- | :--- |
| `application_name` | Nombre de la aplicación origen del error. |
| `error_message` | Descripción corta o tipo de excepción. |
| `stack_trace` | El contexto técnico profundo (traza completa) para la IA. |
| `component` | Pista para clasificar el origen (Frontend / Backend).


Infraestructura y Despliegue

El proyecto utiliza Docker para garantizar un entorno de desarrollo idéntico y aislado.

Servicios:

PostgreSQL: Base de datos relacional (Puerto 5432).

pgAdmin4: Administrador visual de base de datos (Puerto 5050).

Configuración de Entorno (.env)

Es necesario crear un archivo .env en la raíz del proyecto con las siguientes claves:

Bash
# Credenciales de Base de Datos
DB_USER=admin
DB_PASSWORD=tu_contraseña_segura
DB_NAME=nexus_ai_db

# Integración con IA
GEMINI_API_KEY=tu_clave_de_google_studio
📝 Estrategia de Desarrollo

Commits: Se realizará un commit después de cada pieza de código funcional importante para mantener la trazabilidad del desarrollo.

Persistencia de Datos: Se utiliza un volumen local de Docker (./postgres-data) para que los datos de la base de datos no se pierdan al reiniciar los contenedores.

🚀 Quick Start (Cómo desplegar)

Clona el repositorio.

Crea y configura tu archivo .env siguiendo el ejemplo anterior.

Levanta la infraestructura con Docker Compose:

Bash
docker-compose up -d
Ejecuta la aplicación de Spring Boot (mvn spring-boot:run).