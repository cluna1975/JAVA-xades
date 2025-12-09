# Guía de Instalación - JAVA-xades

## Prerrequisitos

Antes de usar este proyecto, necesitas tener instalado:

### 1. Java Development Kit (JDK) 11 o superior

**Verificar instalación:**
```bash
java -version
javac -version
```

**Si no está instalado:**
- Descargar desde: https://adoptium.net/ (recomendado)
- O desde: https://www.oracle.com/java/technologies/downloads/

**Configurar JAVA_HOME:**

En Windows (PowerShell como Administrador):
```powershell
# Establecer JAVA_HOME (ajusta la ruta según tu instalación)
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-11", "Machine")

# Agregar Java al PATH
$path = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
[System.Environment]::SetEnvironmentVariable("Path", "$path;%JAVA_HOME%\bin", "Machine")
```

En Linux/Mac:
```bash
# Agregar a ~/.bashrc o ~/.zshrc
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

### 2. Apache Maven

**Verificar instalación:**
```bash
mvn -version
```

**Si no está instalado:**

#### Windows:

**Opción 1: Usando Chocolatey (recomendado)**
```powershell
# Instalar Chocolatey primero si no lo tienes
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Instalar Maven
choco install maven
```

**Opción 2: Manual**
1. Descargar Maven desde: https://maven.apache.org/download.cgi
2. Extraer a `C:\Program Files\Apache\maven`
3. Configurar variables de entorno:

```powershell
# Establecer M2_HOME
[System.Environment]::SetEnvironmentVariable("M2_HOME", "C:\Program Files\Apache\maven", "Machine")

# Agregar Maven al PATH
$path = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
[System.Environment]::SetEnvironmentVariable("Path", "$path;%M2_HOME%\bin", "Machine")
```

4. Reiniciar PowerShell y verificar:
```powershell
mvn -version
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install maven
```

#### Linux (Fedora/RHEL):
```bash
sudo dnf install maven
```

#### macOS:
```bash
# Usando Homebrew
brew install maven
```

### 3. Git (Opcional)

Para clonar el repositorio:
```bash
git --version
```

Si no está instalado:
- Windows: https://git-scm.com/download/win
- Linux: `sudo apt install git` o `sudo dnf install git`
- macOS: `brew install git`

## Instalación del Proyecto

### 1. Clonar o Descargar el Proyecto

**Con Git:**
```bash
git clone <repository-url>
cd JAVA-xades
```

**Sin Git:**
- Descargar el ZIP del repositorio
- Extraer en una carpeta
- Abrir terminal en esa carpeta

### 2. Descargar Dependencias

```bash
mvn clean install
```

Este comando:
- Descarga todas las dependencias (XAdES4j, Bouncy Castle, Guava, etc.)
- Compila el proyecto
- Ejecuta tests (si existen)
- Genera el JAR en `target/`

### 3. Verificar la Instalación

```bash
mvn compile
```

Si todo está correcto, verás:
```
[INFO] BUILD SUCCESS
```

## Configuración del Certificado

### 1. Obtener un Certificado PKCS12 (.p12)

Para firmar documentos necesitas un certificado digital en formato PKCS12.

**Si tienes un certificado del SRI:**
- Colócalo en: `src/main/key/mr.p12`

**Para pruebas, puedes generar uno auto-firmado:**

```bash
keytool -genkeypair -alias testkey -keyalg RSA -keysize 2048 \
  -validity 365 -keystore src/main/key/mr.p12 -storetype PKCS12 \
  -dname "CN=Test User, OU=Test, O=Test Org, L=Quito, ST=Pichincha, C=EC"
```

Te pedirá una contraseña. Usa la misma en el código.

### 2. Verificar el Certificado

```bash
keytool -list -v -keystore src/main/key/mr.p12 -storetype PKCS12
```

## Estructura de Directorios

Asegúrate de que existan estos directorios:

```bash
mkdir -p src/main/key
mkdir -p src/main/resources
```

## Compilación y Ejecución

### Compilar:
```bash
mvn clean compile
```

### Ejecutar:
```bash
mvn exec:java -Dexec.mainClass="com.xades.sri.Main"
```

### Generar JAR ejecutable:
```bash
mvn clean package
java -jar target/xades-sri-signer-1.0-SNAPSHOT.jar
```

## Solución de Problemas Comunes

### Error: "JAVA_HOME not set"

**Windows:**
```powershell
# Ver JAVA_HOME actual
echo $env:JAVA_HOME

# Establecer temporalmente
$env:JAVA_HOME = "C:\Program Files\Java\jdk-11"
```

**Linux/Mac:**
```bash
# Ver JAVA_HOME actual
echo $JAVA_HOME

# Establecer temporalmente
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
```

### Error: "mvn command not found"

Reinicia la terminal después de instalar Maven, o verifica el PATH:

**Windows:**
```powershell
echo $env:Path
```

**Linux/Mac:**
```bash
echo $PATH
```

### Error: "Cannot resolve dependencies"

```bash
# Limpiar caché de Maven
mvn dependency:purge-local-repository

# Forzar actualización
mvn clean install -U
```

### Error: "Unsupported class file major version"

Tu JDK es muy antiguo. Este proyecto requiere Java 11+.

```bash
# Verificar versión
java -version

# Debe mostrar version 11 o superior
```

## Configuración del IDE

### IntelliJ IDEA:
1. File → Open → Seleccionar carpeta del proyecto
2. IntelliJ detectará automáticamente el proyecto Maven
3. Esperar a que descargue dependencias
4. Run → Edit Configurations → Add New → Application
5. Main class: `com.xades.sri.Main`

### Eclipse:
1. File → Import → Maven → Existing Maven Projects
2. Seleccionar carpeta del proyecto
3. Finish
4. Click derecho en proyecto → Maven → Update Project

### VS Code:
1. Instalar extensiones:
   - Extension Pack for Java
   - Maven for Java
2. Abrir carpeta del proyecto
3. VS Code detectará automáticamente el proyecto Maven

## Próximos Pasos

Una vez instalado todo:

1. ✅ Coloca tu certificado `.p12` en `src/main/key/`
2. ✅ Coloca un XML de prueba en `src/main/resources/test.xml`
3. ✅ Actualiza la contraseña en `Main.java`
4. ✅ Ejecuta: `mvn exec:java -Dexec.mainClass="com.xades.sri.Main"`
5. ✅ Verifica el XML firmado en `src/main/resources/test_signed.xml`

## Recursos Adicionales

- [Maven Getting Started](https://maven.apache.org/guides/getting-started/)
- [XAdES4j Documentation](https://github.com/luisgoncalves/xades4j/wiki)
- [Java Keytool Guide](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html)

## Soporte

Si encuentras problemas:
1. Verifica que Java 11+ esté instalado
2. Verifica que Maven esté instalado
3. Revisa los logs de error
4. Abre un issue en el repositorio con los detalles del error
