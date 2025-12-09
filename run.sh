#!/bin/bash
# Script de Bash para ejecutar el firmador XAdES
# Uso: ./run.sh

echo "========================================="
echo "  XAdES-BES Signer for SRI (XAdES4j)    "
echo "========================================="
echo ""

# Verificar que Maven esté instalado
echo "Verificando Maven..."
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven no está instalado o no está en el PATH"
    echo "Por favor instala Maven siguiendo las instrucciones en INSTALLATION.md"
    exit 1
fi
echo "Maven encontrado: OK"
echo ""

# Verificar que Java esté instalado
echo "Verificando Java..."
if ! command -v java &> /dev/null; then
    echo "ERROR: Java no está instalado o no está en el PATH"
    echo "Por favor instala Java 11+ siguiendo las instrucciones en INSTALLATION.md"
    exit 1
fi
echo "Java encontrado: OK"
echo ""

# Compilar el proyecto
echo "Compilando el proyecto..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: La compilación falló"
    echo "Revisa los errores anteriores"
    exit 1
fi
echo ""
echo "Compilación exitosa"
echo ""

# Ejecutar el proyecto
echo "Ejecutando el firmador XAdES..."
echo ""
mvn exec:java -Dexec.mainClass="com.xades.sri.Main"

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================="
    echo "  Proceso completado exitosamente       "
    echo "========================================="
else
    echo ""
    echo "========================================="
    echo "  El proceso falló                      "
    echo "========================================="
    exit 1
fi
