# Guía de Instalación y Ejecución - JAVA-xades

Este proyecto implementa firmas electrónicas XAdES-BES utilizando la librería **XAdES4j**.

## Prerrequisitos

Para ejecutar este proyecto, necesitas:

1.  **Java Development Kit (JDK) 11 o superior** (Recomendado Java 17 LTS).
    *   Verificar con: `java -version`
2.  **Apache Maven 3.8 o superior**.
    *   Verificar con: `mvn -version`
3.  **Git** (Opcional, para clonar el repositorio).

---

## Instalación Rápida

1.  **Clonar el repositorio:**

    ```bash
    git clone https://github.com/tu-usuario/java-xades.git
    cd JAVA-xades
    ```

2.  **Configurar Certificado y Claves:**

    El proyecto requiere un certificado digital válido (`.p12` o `.pfx`).
    
    *   Coloca tu archivo `.p12` en: `src/main/key/` (Ejemplo: `mr.p12`).
    *   Asegúrate de actualizar la ruta y la contraseña en el archivo `src/main/java/com/xades/sri/Main.java`:
    
    ```java
    String p12Path = "src/main/key/mr.p12";
    String password = "TU_CONTRASEÑA_AQUI";
    ```

3.  **Preparar Archivo XML:**
    
    *   Coloca el archivo XML que deseas firmar en: `src/main/resources/test.xml`.

---

## Ejecución del Proyecto

Hemos facilitado la ejecución del proyecto mediante scripts automáticos que configuran las variables de entorno necesarias para Java 17+.

### Opción 1: Usando el Script Automático (Recomendado)

Simplemente ejecuta el siguiente comando en tu terminal (CMD o PowerShell):

```powershell
.\run.bat
```

> **Nota:** Este script configura automáticamente `MAVEN_OPTS` para evitar errores de acceso modular (`InaccessibleObjectException`) y ejecuta la limpieza, compilación y firma en un solo paso.

### Opción 2: Ejecución Manual con Maven

Si prefieres ejecutarlo manualmente o estás en un entorno Linux/Mac, usa el siguiente comando:

**En Windows (PowerShell):**
```powershell
$env:MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"
mvn clean package exec:java
```

**En Linux / Mac:**
```bash
export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"
mvn clean package exec:java
```

---

## Verificación

Una vez que el proceso finalice correctamente, verás un mensaje como:

```
[INFO] BUILD SUCCESS
...
Signed file: src/main/resources/test_signed.xml
```

El archivo firmado se generará en: `src/main/resources/test_signed.xml`.

---

## Solución de Problemas Comunes

### 1. Error: `java.lang.reflect.InaccessibleObjectException`
Este error ocurre en versiones recientes de Java (16+) debido a la encapsulación de módulos.
**Solución:** Usa siempre el script `run.bat` o asegúrate de establecer la variable `MAVEN_OPTS` con `--add-opens java.base/java.lang=ALL-UNNAMED` antes de ejecutar.

### 2. Error: "No se puede cargar el archivo run.ps1... no está firmado digitalmente"
PowerShell bloquea scripts no firmados por seguridad.
**Solución:** Usa `.\run.bat` en su lugar, ya que no tiene esta restricción.

### 3. Error: Contraseña incorrecta o KeyStore inválido
Asegúrate de que la contraseña en `Main.java` coincida exactamente con la de tu archivo `.p12`.

---

## Estructura del Proyecto

```
JAVA-xades/
├── src/
│   ├── main/
│   │   ├── java/           # Código fuente Java
│   │   ├── key/            # Certificados digitales (.p12)
│   │   └── resources/      # Archivos XML de prueba (entrada/salida)
├── run.bat                 # Script de ejecución para Windows
├── pom.xml                 # Configuración de Maven
└── README.md               # Información general
```
