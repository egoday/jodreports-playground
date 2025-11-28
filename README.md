# JODReports Playground 🎯

Un pequeño laboratorio para jugar con plantillas JODReports: subes tu ODT/ODS, metes datos en JSON y el sistema te escupe el documento listo. Perfecto para probar, romper y afinar plantillas sin pelearte con proyectos enormes.

## Características

- ✅ **Endpoint REST** para subir plantillas ODT y datos JSON
- ✅ **Interfaz web** sencilla para probar plantillas
- ✅ **Servicio JODReports** integrado para generación de documentos
- ✅ **Plantillas de ejemplo** listas para usar
- ✅ **Validación** de plantillas y datos JSON

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior

## Ejecutar la aplicación

### Opción 1: Usando Maven

```bash
# Clonar el repositorio
git clone https://github.com/egoday/jodreports-playground.git
cd jodreports-playground

# Ejecutar la aplicación
./mvnw spring-boot:run
```

### Opción 2: Construir y ejecutar JAR

```bash
# Construir el JAR
./mvnw clean package

# Ejecutar
java -jar target/jodreports-playground-1.0.0-SNAPSHOT.jar
```

La aplicación estará disponible en: **http://localhost:8080**

## Uso

### Interfaz Web

1. Abre http://localhost:8080 en tu navegador
2. Sube una plantilla ODT o selecciona una de ejemplo
3. Introduce los datos JSON
4. Haz clic en "Generar Documento"
5. ¡Descarga tu documento generado!

### API REST

#### Generar documento

```bash
curl -X POST http://localhost:8080/api/generate \
  -F "template=@mi-plantilla.odt" \
  -F "data={\"nombre\":\"Juan\",\"fecha\":\"2024-01-15\"}" \
  --output documento-generado.odt
```

#### Validar plantilla

```bash
curl -X POST http://localhost:8080/api/validate \
  -F "template=@mi-plantilla.odt"
```

#### Health check

```bash
curl http://localhost:8080/api/health
```

## Crear plantillas JODReports

Las plantillas son archivos ODT (OpenDocument Text) que contienen marcadores FreeMarker para ser reemplazados con datos.

### Sintaxis básica

| Sintaxis | Descripción |
|----------|-------------|
| `${variable}` | Imprime el valor de una variable |
| `${objeto.propiedad}` | Accede a propiedades de objetos |
| `[#list items as item]...[/#list]` | Itera sobre una lista |
| `[#if condicion]...[/#if]` | Condicional |

### Ejemplo de plantilla

```
Estimado/a ${nombre},

Bienvenido a ${empresa}. Tu puesto será ${puesto}.

[#list productos as producto]
- ${producto.descripcion}: ${producto.precio} €
[/#list]

Total: ${total} €
```

### Datos JSON correspondientes

```json
{
  "nombre": "María García",
  "empresa": "Tech Corp",
  "puesto": "Desarrolladora",
  "productos": [
    {"descripcion": "Consultoría", "precio": 100},
    {"descripcion": "Desarrollo", "precio": 500}
  ],
  "total": 600
}
```

## Plantillas de ejemplo incluidas

### 1. Carta de Bienvenida (`carta-bienvenida.odt`)

Plantilla simple para cartas de bienvenida a nuevos empleados.

**Datos de ejemplo:**
```json
{
  "nombre": "María García López",
  "empresa": "Tecnología Avanzada S.L.",
  "puesto": "Desarrolladora Senior",
  "fecha_inicio": "1 de enero de 2024",
  "departamento": "Desarrollo de Software",
  "responsable": "Juan Pérez Martínez",
  "firma": "Recursos Humanos"
}
```

### 2. Factura Simple (`factura-simple.odt`)

Plantilla con tabla para facturas, demuestra el uso de listas.

**Datos de ejemplo:**
```json
{
  "factura": {
    "numero": "FAC-2024-001",
    "fecha": "15 de enero de 2024",
    "vencimiento": "15 de febrero de 2024"
  },
  "empresa": {
    "nombre": "Mi Empresa S.L.",
    "direccion": "Calle Principal 123",
    "ciudad": "Madrid",
    "cp": "28001",
    "nif": "B12345678"
  },
  "cliente": {
    "nombre": "Cliente Ejemplo S.A."
  },
  "productos": [
    {"descripcion": "Servicio", "cantidad": 10, "precio": 100, "total": 1000}
  ],
  "subtotal": 1000,
  "iva": 21,
  "iva_importe": 210,
  "total": 1210
}
```

## Estructura del proyecto

```
jodreports-playground/
├── src/
│   ├── main/
│   │   ├── java/com/jodreports/playground/
│   │   │   ├── JodReportsPlaygroundApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── DocumentController.java      # API REST
│   │   │   │   ├── SampleController.java        # Archivos de ejemplo
│   │   │   │   └── WebController.java           # Interfaz web
│   │   │   ├── service/
│   │   │   │   └── JodReportsService.java       # Lógica JODReports
│   │   │   └── exception/
│   │   │       └── DocumentGenerationException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── templates/
│   │       │   └── index.html                   # Interfaz web
│   │       ├── static/
│   │       │   ├── css/style.css
│   │       │   └── js/app.js
│   │       └── samples/
│   │           ├── templates/                   # Plantillas ODT
│   │           └── data/                        # Datos JSON
│   └── test/
│       └── java/
│           └── com/jodreports/playground/
│               └── JodReportsPlaygroundApplicationTests.java
├── pom.xml
└── README.md
```

## Tecnologías utilizadas

- **Spring Boot 3.2** - Framework web
- **JODReports 2.4** - Generación de documentos
- **Thymeleaf** - Motor de plantillas web
- **Jackson** - Procesamiento JSON
- **Apache Commons IO** - Utilidades de archivos

## Documentación adicional

- [JODReports Wiki](https://github.com/jodreports/jodreports/wiki)
- [FreeMarker Manual](https://freemarker.apache.org/docs/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)

## Licencia

GPL-3.0 - Ver archivo [LICENSE](LICENSE) para más detalles.
