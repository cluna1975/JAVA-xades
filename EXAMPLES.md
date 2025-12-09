# Ejemplos de Uso - XAdES4j

Este documento contiene ejemplos prácticos de cómo usar el firmador XAdES con diferentes configuraciones.

## Tabla de Contenidos

1. [Firma Básica XAdES-BES](#firma-básica-xades-bes)
2. [Firma con Propiedades Avanzadas](#firma-con-propiedades-avanzadas)
3. [Firma con SHA-256](#firma-con-sha-256)
4. [Firma de Múltiples Referencias](#firma-de-múltiples-referencias)
5. [Integración en tu Aplicación](#integración-en-tu-aplicación)
6. [Validación de Firmas](#validación-de-firmas)

---

## Firma Básica XAdES-BES

La forma más simple de firmar un documento XML.

### Código:

```java
import com.xades.sri.XadesSigner;

public class BasicSigningExample {
    public static void main(String[] args) {
        try {
            String xmlPath = "factura.xml";
            String outputPath = "factura_firmada.xml";
            String p12Path = "certificado.p12";
            String password = "mi_password";
            
            XadesSigner.signXml(xmlPath, outputPath, p12Path, password);
            
            System.out.println("Documento firmado exitosamente!");
            
        } catch (Exception e) {
            System.err.println("Error al firmar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### Ejecutar:

```bash
# Compilar
javac -cp "target/classes:target/dependency/*" BasicSigningExample.java

# Ejecutar
java -cp ".:target/classes:target/dependency/*" BasicSigningExample
```

---

## Firma con Propiedades Avanzadas

Incluye información adicional como rol del firmante y lugar de producción.

### Código:

```java
import com.xades.sri.XadesSignerAdvanced;

public class AdvancedSigningExample {
    public static void main(String[] args) {
        try {
            String xmlPath = "factura.xml";
            String outputPath = "factura_firmada_avanzada.xml";
            String p12Path = "certificado.p12";
            String password = "mi_password";
            
            // Información adicional
            String signerRole = "Emisor de Factura Electrónica";
            String productionPlace = "Quito";
            
            XadesSignerAdvanced.signXmlAdvanced(
                xmlPath, 
                outputPath, 
                p12Path, 
                password,
                signerRole,
                productionPlace
            );
            
            System.out.println("Documento firmado con propiedades avanzadas!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### Resultado:

El XML firmado incluirá:

```xml
<SignedSignatureProperties>
  <SigningTime>2024-12-08T20:15:30-05:00</SigningTime>
  <SigningCertificate>...</SigningCertificate>
  <SignatureProductionPlace>
    <City>Quito</City>
    <CountryName>Ecuador</CountryName>
  </SignatureProductionPlace>
  <SignerRole>
    <ClaimedRoles>
      <ClaimedRole>Emisor de Factura Electrónica</ClaimedRole>
    </ClaimedRoles>
  </SignerRole>
</SignedSignatureProperties>
```

---

## Firma con SHA-256

Usar SHA-256 en lugar de SHA-1 para mayor seguridad.

### Código:

```java
import com.xades.sri.XadesSignerAdvanced;

public class SHA256SigningExample {
    public static void main(String[] args) {
        try {
            String xmlPath = "documento.xml";
            String outputPath = "documento_firmado_sha256.xml";
            String p12Path = "certificado.p12";
            String password = "mi_password";
            
            XadesSignerAdvanced.signXmlWithSHA256(
                xmlPath, 
                outputPath, 
                p12Path, 
                password
            );
            
            System.out.println("Documento firmado con SHA-256!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### ¿Cuándo usar SHA-256?

- ✅ **Recomendado** para nuevas implementaciones
- ✅ Mayor seguridad criptográfica
- ⚠️ Verifica que el SRI acepte SHA-256 (algunos sistemas legacy solo aceptan SHA-1)

---

## Firma de Múltiples Referencias

Firmar múltiples elementos dentro del mismo documento.

### Código:

```java
import com.xades.sri.XadesSignerAdvanced;

public class MultipleReferencesExample {
    public static void main(String[] args) {
        try {
            String xmlPath = "documento_complejo.xml";
            String outputPath = "documento_firmado_multi.xml";
            String p12Path = "certificado.p12";
            String password = "mi_password";
            
            // IDs de los elementos a firmar
            String[] elementIds = {
                "comprobante",
                "infoTributaria",
                "infoFactura"
            };
            
            XadesSignerAdvanced.signMultipleReferences(
                xmlPath, 
                outputPath, 
                p12Path, 
                password,
                elementIds
            );
            
            System.out.println("Múltiples referencias firmadas!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### XML de Entrada:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<autorizacion>
  <comprobante id="comprobante">
    <infoTributaria id="infoTributaria">
      <ruc>1234567890001</ruc>
    </infoTributaria>
    <infoFactura id="infoFactura">
      <totalSinImpuestos>100.00</totalSinImpuestos>
    </infoFactura>
  </comprobante>
</autorizacion>
```

---

## Integración en tu Aplicación

### Ejemplo: Servicio de Firma

```java
package com.miempresa.servicios;

import com.xades.sri.XadesSigner;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServicioFirmaElectronica {
    
    private final String certificadoPath;
    private final String certificadoPassword;
    
    public ServicioFirmaElectronica(String certificadoPath, String password) {
        this.certificadoPath = certificadoPath;
        this.certificadoPassword = password;
    }
    
    /**
     * Firma un documento XML y retorna el contenido firmado
     */
    public String firmarDocumento(String xmlContent) throws Exception {
        // Crear archivo temporal para el XML
        String tempInput = "temp_input_" + System.currentTimeMillis() + ".xml";
        String tempOutput = "temp_output_" + System.currentTimeMillis() + ".xml";
        
        try {
            // Guardar contenido en archivo temporal
            Files.write(Paths.get(tempInput), xmlContent.getBytes("UTF-8"));
            
            // Firmar
            XadesSigner.signXml(tempInput, tempOutput, certificadoPath, certificadoPassword);
            
            // Leer resultado
            String xmlFirmado = new String(Files.readAllBytes(Paths.get(tempOutput)), "UTF-8");
            
            return xmlFirmado;
            
        } finally {
            // Limpiar archivos temporales
            new File(tempInput).delete();
            new File(tempOutput).delete();
        }
    }
    
    /**
     * Firma un archivo XML
     */
    public void firmarArchivo(String inputPath, String outputPath) throws Exception {
        XadesSigner.signXml(inputPath, outputPath, certificadoPath, certificadoPassword);
    }
    
    /**
     * Firma múltiples documentos en lote
     */
    public void firmarLote(String[] archivos, String directorioSalida) throws Exception {
        for (String archivo : archivos) {
            String nombreArchivo = new File(archivo).getName();
            String nombreSalida = nombreArchivo.replace(".xml", "_firmado.xml");
            String rutaSalida = directorioSalida + File.separator + nombreSalida;
            
            System.out.println("Firmando: " + archivo);
            firmarArchivo(archivo, rutaSalida);
            System.out.println("Guardado en: " + rutaSalida);
        }
    }
}
```

### Uso del Servicio:

```java
public class Main {
    public static void main(String[] args) {
        try {
            // Inicializar servicio
            ServicioFirmaElectronica servicio = new ServicioFirmaElectronica(
                "certificado.p12",
                "mi_password"
            );
            
            // Ejemplo 1: Firmar contenido XML
            String xml = "<?xml version=\"1.0\"?><factura>...</factura>";
            String xmlFirmado = servicio.firmarDocumento(xml);
            System.out.println("XML firmado: " + xmlFirmado);
            
            // Ejemplo 2: Firmar archivo
            servicio.firmarArchivo("factura.xml", "factura_firmada.xml");
            
            // Ejemplo 3: Firmar lote
            String[] archivos = {
                "factura1.xml",
                "factura2.xml",
                "factura3.xml"
            };
            servicio.firmarLote(archivos, "facturas_firmadas");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## Validación de Firmas

### Verificar Firma Básica:

```java
import com.xades.sri.XadesSigner;

public class ValidacionExample {
    public static void main(String[] args) {
        try {
            String xmlFirmado = "factura_firmada.xml";
            
            boolean esValida = XadesSigner.validateSignature(xmlFirmado);
            
            if (esValida) {
                System.out.println("✓ La firma es válida");
            } else {
                System.out.println("✗ La firma NO es válida");
            }
            
        } catch (Exception e) {
            System.err.println("Error al validar: " + e.getMessage());
        }
    }
}
```

### Validación Avanzada con XAdES4j:

```java
import xades4j.verification.*;
import xades4j.providers.CertificateValidationProvider;
import xades4j.providers.impl.PKIXCertificateValidationProvider;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class ValidacionAvanzada {
    
    public static void validarFirma(String xmlPath) throws Exception {
        // Parsear el documento
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new File(xmlPath));
        
        // Encontrar el elemento Signature
        NodeList signatures = doc.getElementsByTagNameNS(
            "http://www.w3.org/2000/09/xmldsig#", 
            "Signature"
        );
        
        if (signatures.getLength() == 0) {
            System.out.println("No se encontró firma en el documento");
            return;
        }
        
        Element signatureElement = (Element) signatures.item(0);
        
        // Configurar validador
        CertificateValidationProvider certValidator = 
            new PKIXCertificateValidationProvider(
                null,  // trustAnchors (null = usar del sistema)
                false, // revocationEnabled
                null   // certPathBuilderProvider
            );
        
        XadesVerificationProfile verificationProfile = 
            new XadesVerificationProfile(certValidator);
        
        XadesVerifier verifier = verificationProfile.newVerifier();
        
        // Verificar
        XAdESVerificationResult result = verifier.verify(signatureElement, null);
        
        // Mostrar resultados
        System.out.println("=== Resultado de Validación ===");
        System.out.println("Formato: " + result.getSignatureForm());
        System.out.println("Algoritmo: " + result.getSignatureAlgorithmUri());
        
        System.out.println("\nPropiedades firmadas:");
        for (PropertyDataObject prop : result.getPropertiesFilter().getSignedProperties()) {
            System.out.println("  - " + prop.getClass().getSimpleName());
        }
        
        System.out.println("\n✓ Firma válida!");
    }
    
    public static void main(String[] args) {
        try {
            validarFirma("factura_firmada.xml");
        } catch (Exception e) {
            System.err.println("✗ Error en validación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

---

## Casos de Uso Comunes

### 1. Facturación Electrónica SRI

```java
// Firmar factura para el SRI
XadesSigner.signXml(
    "factura_001-001-000000123.xml",
    "factura_001-001-000000123_firmada.xml",
    "certificado_sri.p12",
    "password"
);
```

### 2. Firma de Comprobantes de Retención

```java
XadesSignerAdvanced.signXmlAdvanced(
    "retencion.xml",
    "retencion_firmada.xml",
    "certificado.p12",
    "password",
    "Agente de Retención",
    "Guayaquil"
);
```

### 3. Firma de Guías de Remisión

```java
XadesSigner.signXml(
    "guia_remision.xml",
    "guia_remision_firmada.xml",
    "certificado.p12",
    "password"
);
```

---

## Mejores Prácticas

### 1. Manejo de Contraseñas

❌ **No hacer:**
```java
String password = "mi_password_en_codigo"; // ¡Nunca!
```

✅ **Hacer:**
```java
// Leer desde variable de entorno
String password = System.getenv("CERT_PASSWORD");

// O desde archivo de configuración cifrado
String password = ConfigManager.getSecurePassword();
```

### 2. Manejo de Errores

```java
try {
    XadesSigner.signXml(xmlPath, outputPath, p12Path, password);
} catch (FileNotFoundException e) {
    System.err.println("Archivo no encontrado: " + e.getMessage());
} catch (KeyStoreException e) {
    System.err.println("Error con el certificado: " + e.getMessage());
} catch (Exception e) {
    System.err.println("Error general: " + e.getMessage());
    e.printStackTrace();
}
```

### 3. Validación de Entrada

```java
public void firmarConValidacion(String xmlPath, String outputPath) throws Exception {
    // Validar que los archivos existan
    if (!new File(xmlPath).exists()) {
        throw new FileNotFoundException("XML no encontrado: " + xmlPath);
    }
    
    // Validar que el XML sea válido
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse(new File(xmlPath));
    
    // Proceder con la firma
    XadesSigner.signXml(xmlPath, outputPath, p12Path, password);
}
```

---

## Recursos Adicionales

- [Documentación XAdES4j](https://github.com/luisgoncalves/xades4j/wiki)
- [Especificación XAdES](https://www.etsi.org/deliver/etsi_ts/101900_101999/101903/)
- [Guía SRI Ecuador](https://www.sri.gob.ec/)

---

**Nota**: Estos ejemplos son para propósitos educativos. Asegúrate de probar exhaustivamente en un ambiente de desarrollo antes de usar en producción.
