# JAVA-xades

Proyecto para generar Firma Electrónica SRI bajo estándar XAdES-BES usando **XAdES4j**.

## 📋 Descripción

Este proyecto implementa firmas digitales XAdES-BES (XML Advanced Electronic Signatures - Basic Electronic Signature) compatibles con los requisitos del SRI (Servicio de Rentas Internas de Ecuador) utilizando la biblioteca **XAdES4j**.

### ¿Por qué XAdES4j?

XAdES4j es una biblioteca Java robusta y completa para trabajar con firmas XAdES que ofrece:

- ✅ **Implementación completa** del estándar ETSI TS 101 903
- ✅ **Soporte para múltiples perfiles**: XAdES-BES, XAdES-T, XAdES-C, XAdES-X, XAdES-XL, XAdES-A
- ✅ **Validación de firmas** incluida
- ✅ **Menos código manual** - API de alto nivel
- ✅ **Mantenimiento activo** y buena documentación
- ✅ **Integración con Bouncy Castle** para criptografía avanzada

## 🛠️ Tecnologías

- **Java 11+**
- **Maven** - Gestión de dependencias
- **XAdES4j 2.3.0** - Biblioteca principal para firmas XAdES
- **Bouncy Castle** - Proveedor criptográfico
- **Google Guava** - Utilidades (requerido por XAdES4j)

## 📦 Dependencias Principales

```xml
<dependency>
  <groupId>com.googlecode.xades4j</groupId>
  <artifactId>xades4j</artifactId>
  <version>2.3.0</version>
</dependency>

<dependency>
  <groupId>org.bouncycastle</groupId>
  <artifactId>bcprov-jdk18on</artifactId>
  <version>1.78</version>
</dependency>
```

## 🚀 Instalación

1. **Clonar el repositorio**:
   ```bash
   git clone <repository-url>
   cd JAVA-xades
   ```

2. **Compilar el proyecto**:
   ```bash
   mvn clean compile
   ```

3. **Descargar dependencias**:
   ```bash
   mvn dependency:resolve
   ```

## 📝 Uso

### Configuración Básica

1. **Colocar tu certificado PKCS12** (`.p12`) en `src/main/key/`
2. **Colocar el XML a firmar** en `src/main/resources/test.xml`
3. **Actualizar la contraseña** en `Main.java`:

```java
String password = "TU_CONTRASEÑA_AQUI";
```

### Ejecutar el Firmador

```bash
mvn exec:java -Dexec.mainClass="com.xades.sri.Main"
```

O compilar y ejecutar:

```bash
mvn clean package
java -jar target/xades-sri-signer-1.0-SNAPSHOT.jar
```

### Ejemplo de Código

```java
import com.xades.sri.XadesSigner;

public class Example {
    public static void main(String[] args) throws Exception {
        String xmlPath = "documento.xml";
        String outputPath = "documento_firmado.xml";
        String p12Path = "certificado.p12";
        String password = "mi_password";
        
        // Firmar el documento
        XadesSigner.signXml(xmlPath, outputPath, p12Path, password);
        
        System.out.println("Documento firmado exitosamente!");
    }
}
```

## 🏗️ Estructura del Proyecto

```
JAVA-xades/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── xades/
│       │           └── sri/
│       │               ├── Main.java                    # Punto de entrada
│       │               └── XadesSigner.java            # Implementación con XAdES4j
│       ├── key/
│       │   └── mr.p12                                  # Certificado PKCS12
│       └── resources/
│           ├── test.xml                                # XML de prueba
│           └── test_signed.xml                         # XML firmado (generado)
├── pom.xml                                             # Configuración Maven
└── README.md
```

## 🔐 Características de la Firma XAdES-BES

La implementación genera firmas XAdES-BES que incluyen:

- **SigningTime**: Fecha y hora de la firma
- **SigningCertificate**: Información del certificado usado
- **CertDigest**: Hash del certificado (SHA-1)
- **IssuerSerial**: Emisor y número de serie del certificado
- **Firma Enveloped**: La firma se incluye dentro del documento XML

### Estructura de la Firma Generada

```xml
<Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
  <SignedInfo>
    <CanonicalizationMethod Algorithm="..."/>
    <SignatureMethod Algorithm="..."/>
    <Reference URI="">
      <Transforms>
        <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
      </Transforms>
      <DigestMethod Algorithm="..."/>
      <DigestValue>...</DigestValue>
    </Reference>
  </SignedInfo>
  <SignatureValue>...</SignatureValue>
  <KeyInfo>
    <X509Data>
      <X509Certificate>...</X509Certificate>
    </X509Data>
  </KeyInfo>
  <Object>
    <QualifyingProperties xmlns="http://uri.etsi.org/01903/v1.3.2#">
      <SignedProperties>
        <SignedSignatureProperties>
          <SigningTime>...</SigningTime>
          <SigningCertificate>...</SigningCertificate>
        </SignedSignatureProperties>
      </SignedProperties>
    </QualifyingProperties>
  </Object>
</Signature>
```

## 🔧 Configuración Avanzada

### Cambiar Algoritmos de Firma

```java
XadesBesSigningProfile signingProfile = new XadesBesSigningProfile(keyingProvider);

// Configurar algoritmos personalizados
signingProfile.withSignatureAlgorithms(
    new SignatureAlgorithms()
        .withSignatureAlgorithm("RSA", SignatureAlgorithm.RSA_SHA256)
        .withCanonicalizationAlgorithmForSignature(CanonicalizationMethod.INCLUSIVE)
);
```

### Agregar Propiedades Adicionales

```java
// Agregar propiedades de datos firmados
signingProfile.withDataObjectPropertiesProvider(new DataObjectPropertiesProvider() {
    @Override
    public void provideProperties(DataObjectDesc dataObj) {
        dataObj.withDataObjectFormat(new DataObjectFormatProperty("text/xml"));
    }
});
```

## 📚 Recursos Adicionales

- [XAdES4j GitHub](https://github.com/luisgoncalves/xades4j)
- [XAdES4j Wiki](https://github.com/luisgoncalves/xades4j/wiki)
- [ETSI TS 101 903 - XAdES Standard](https://www.etsi.org/deliver/etsi_ts/101900_101999/101903/)
- [Bouncy Castle](https://www.bouncycastle.org/)

## 🐛 Solución de Problemas

### Error: "No such provider: BC"

Asegúrate de que Bouncy Castle esté registrado:

```java
Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
```

### Error: "Cannot find key with alias"

Verifica que el archivo `.p12` sea válido y la contraseña sea correcta:

```bash
keytool -list -v -keystore src/main/key/mr.p12 -storetype PKCS12
```

### Error de Dependencias Maven

Limpiar y recompilar:

```bash
mvn clean install -U
```

## 📄 Licencia

Este proyecto está bajo licencia MIT.

## 👥 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📧 Contacto

Para preguntas o soporte, por favor abre un issue en el repositorio.

---

**Nota**: Este proyecto está diseñado específicamente para cumplir con los requisitos de firma electrónica del SRI de Ecuador. Asegúrate de validar las firmas generadas con las herramientas oficiales del SRI.
