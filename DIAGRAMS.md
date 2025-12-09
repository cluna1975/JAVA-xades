# Diagramas de Funcionamiento - JAVA-xades

Este documento detalla el flujo de funcionamiento del firmador electrónico XAdES-BES.

## 1. Flujo del Proceso (Flowchart)

Este diagrama muestra el flujo de alto nivel desde la ejecución hasta la firma del documento.

```mermaid
graph TD
    Start((Inicio)) --> Init[Inicializar Main]
    Init --> LoadConfig[Cargar Configuración\nRutas: XML, P12, Salida]
    
    subgraph Validación
        LoadConfig --> CheckFiles{¿Existen Archivos?}
        CheckFiles -- No --> ErrorFiles[Error: Archivo no encontrado]
        CheckFiles -- Si --> LoadKey[Cargar Keystore .p12]
    end

    subgraph Firma XAdES
        LoadKey --> InitXades[Inicializar XadesSigner\n(XAdES4j Library)]
        InitXades --> ReadXML[Leer XML de Entrada]
        ReadXML --> ApplySign[Aplicar Firma XAdES-BES]
        ApplySign --> Output[Generar Estructura Firmada]
    end

    Output --> WriteFile[Escribir test_signed.xml]
    WriteFile --> Success((Fin Exitoso))
    
    ErrorFiles --> EndError((Fin con Error))
    LoadKey -- Error Password --> EndError
```

## 2. Diagrama de Secuencia

Detalle técnico de las interacciones entre las clases Java y la librería XAdES4j.

```mermaid
sequenceDiagram
    autonumber
    participant User as Usuario / Script
    participant Main as Main.java
    participant XSigner as XadesSigner.java
    participant X4j as Lib: XAdES4j
    participant FS as Sistema de Archivos

    User->>Main: Ejecuta (run.bat)
    activate Main
    Main->>FS: Verifica existencia de XML y P12
    
    Main->>XSigner: signXml(xmlPath, outPath, p12, password)
    activate XSigner
    
    Note over XSigner, X4j: Inicialización del KeyStore
    XSigner->>FS: Carga Archivo .p12
    XSigner->>X4j: Configura FileSystemKeyStoreKeyingDataProvider
    
    Note over XSigner, X4j: Configuración del Perfil
    XSigner->>X4j: Crea XadesBesSigningProfile
    XSigner->>X4j: Obtiene Signer
    
    XSigner->>FS: Parsea XML de entrada (DocumentBuilder)
    FS-->>XSigner: Documento DOM
    
    Note right of XSigner: Proceso de Firma
    XSigner->>X4j: signer.sign(element, dataObjectDesc)
    X4j-->>XSigner: Documento Firmado
    
    XSigner->>FS: Guarda XML firmado (TransformerUtils)
    
    XSigner-->>Main: Retorna éxito
    deactivate XSigner
    
    Main-->>User: Imprime "Process completed successfully"
    deactivate Main
```

## 3. Estructura de Componentes

```mermaid
classDiagram
    class Main {
        +main(args)
        +configurarRutas()
    }
    
    class XadesSigner {
        +signXml(xmlPath, outPath, p12Path, pass)
    }
    
    class XAdES4j_Library {
        +XadesBesSigningProfile
        +Signer
        +KeyingDataProvider
    }
    
    class FileSystem {
        +test.xml
        +mr.p12
        +test_signed.xml
    }

    Main ..> XadesSigner : Usa
    Main ..> FileSystem : Define Rutas
    XadesSigner ..> FileSystem : Lee/Escribe
    XadesSigner ..> XAdES4j_Library : Implementa
```
