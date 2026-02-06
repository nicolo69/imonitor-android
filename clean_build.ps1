# Script per pulire le cartelle build
Write-Host "=== Pulizia Cartelle Build ===" -ForegroundColor Cyan

# Ferma Gradle daemon
Write-Host "`n1. Fermando Gradle daemon..." -ForegroundColor Yellow
try {
    & .\gradlew.bat --stop 2>&1 | Out-Null
    Start-Sleep -Seconds 2
} catch {
    Write-Host "   Gradle daemon non risponde, continuo..." -ForegroundColor Gray
}

# Termina processi Java
Write-Host "`n2. Terminando processi Java..." -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | ForEach-Object {
    Write-Host "   Terminando processo Java (PID: $($_.Id))" -ForegroundColor Gray
    Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
}
Start-Sleep -Seconds 2

# Lista cartelle da eliminare
$folders = @(
    "app\build",
    "csrestlib\build",
    "build",
    ".gradle"
)

# Elimina cartelle
Write-Host "`n3. Eliminando cartelle build..." -ForegroundColor Yellow
foreach ($folder in $folders) {
    if (Test-Path $folder) {
        Write-Host "   Eliminando: $folder" -ForegroundColor Gray
        try {
            Remove-Item -Path $folder -Recurse -Force -ErrorAction Stop
            Write-Host "   ✓ $folder eliminata" -ForegroundColor Green
        } catch {
            Write-Host "   ✗ Impossibile eliminare $folder (file bloccati)" -ForegroundColor Red
            Write-Host "     Prova a chiudere Android Studio e rieseguire lo script" -ForegroundColor Yellow
        }
    } else {
        Write-Host "   - $folder non esiste" -ForegroundColor Gray
    }
}

Write-Host "`n=== Pulizia Completata ===" -ForegroundColor Cyan
Write-Host "`nOra puoi eseguire:" -ForegroundColor White
Write-Host "  gradlew.bat :app:assembleDebug" -ForegroundColor Green
Write-Host ""
