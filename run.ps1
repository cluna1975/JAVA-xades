# Script de PowerShell para ejecutar el firmador XAdES
# Uso: .\run.ps1

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  XAdES-BES Signer for SRI (XAdES4j)    " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que Maven esté instalado
Write-Host "Verificando Maven..." -ForegroundColor Yellow
$mvnVersion = & { mvn -version 2>&1 }
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Maven no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "Por favor instala Maven siguiendo las instrucciones en INSTALLATION.md" -ForegroundColor Red
    exit 1
}
Write-Host "Maven encontrado: OK" -ForegroundColor Green
Write-Host ""

# Verificar que Java esté instalado
Write-Host "Verificando Java..." -ForegroundColor Yellow
$javaVersion = & { java -version 2>&1 }
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Java no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "Por favor instala Java 11+ siguiendo las instrucciones en INSTALLATION.md" -ForegroundColor Red
    exit 1
}
Write-Host "Java encontrado: OK" -ForegroundColor Green
Write-Host ""

# Compilar el proyecto
Write-Host "Compilando el proyecto..." -ForegroundColor Yellow
mvn clean compile
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: La compilación falló" -ForegroundColor Red
    Write-Host "Revisa los errores anteriores" -ForegroundColor Red
    exit 1
}
Write-Host ""
Write-Host "Compilación exitosa" -ForegroundColor Green
Write-Host ""

# Ejecutar el proyecto
Write-Host "Ejecutando el firmador XAdES..." -ForegroundColor Yellow
Write-Host ""
mvn exec:java -Dexec.mainClass="com.xades.sri.Main"

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=========================================" -ForegroundColor Green
    Write-Host "  Proceso completado exitosamente       " -ForegroundColor Green
    Write-Host "=========================================" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "=========================================" -ForegroundColor Red
    Write-Host "  El proceso falló                      " -ForegroundColor Red
    Write-Host "=========================================" -ForegroundColor Red
    exit 1
}
