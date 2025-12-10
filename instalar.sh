#!/bin/bash

# --- CONFIGURACIÓN ---
APP_NAME="Strike Graph"
# Nota: Maven Shade sobrescribió el jar original, así que usamos este nombre:
JAR_NAME="strike-1.0-SNAPSHOT.jar"
ICON_SOURCE="src/main/resources/images/icons/equipo.png"
INSTALL_DIR="$HOME/StrikeApp"
DESKTOP_FILE="$HOME/.local/share/applications/strike-graph.desktop"

# Detectar la ruta absoluta de Java (Vital para lanzadores .desktop)
JAVA_EXEC=$(which java)

if [ -z "$JAVA_EXEC" ]; then
    echo "❌ Error: No se encontró Java en el sistema."
    exit 1
fi

echo "🚀 Iniciando instalación de $APP_NAME..."
echo "☕ Usando Java en: $JAVA_EXEC"

# 1. Verificar el JAR generado
TARGET_JAR="target/$JAR_NAME"

if [ ! -f "$TARGET_JAR" ]; then
    echo "❌ Error: No se encontró $TARGET_JAR"
    echo "   Asegúrate de haber ejecutado 'mvn clean package' antes."
    exit 1
fi

# 2. Crear directorio de instalación y copiar archivos
echo "📂 Creando directorio en $INSTALL_DIR..."
mkdir -p "$INSTALL_DIR"
cp "$TARGET_JAR" "$INSTALL_DIR/$JAR_NAME"

# 3. Copiar icono
if [ -f "$ICON_SOURCE" ]; then
    cp "$ICON_SOURCE" "$INSTALL_DIR/icon.png"
    ICON_PATH="$INSTALL_DIR/icon.png"
else
    echo "⚠️ Icono no encontrado en $ICON_SOURCE, usando genérico."
    ICON_PATH="utilities-terminal"
fi

# 4. Crear lanzador .desktop
# NOTA: Usamos $JAVA_EXEC para la ruta absoluta y definimos Path=$INSTALL_DIR
# para que la app encuentre sus recursos relativos si los hubiera.
echo "📝 Creando acceso directo..."
cat > "$DESKTOP_FILE" <<EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=$APP_NAME
Comment=Sistema de Gestión de Fútbol con Neo4j
Exec="$JAVA_EXEC" -jar "$INSTALL_DIR/$JAR_NAME"
Icon=$ICON_PATH
Path=$INSTALL_DIR
Terminal=false
Categories=Office;Database;Java;
StartupNotify=true
EOF

# 5. Dar permisos de ejecución
chmod +x "$DESKTOP_FILE"
chmod +x "$INSTALL_DIR/$JAR_NAME"

# 6. Actualizar base de datos de escritorio (para que aparezca en el buscador)
update-desktop-database "$HOME/.local/share/applications" 2>/dev/null

echo "✅ ¡Instalación Completada!"
echo "   El lanzador está en: $DESKTOP_FILE"
echo "👉 PRUEBA FINAL: Busca 'Strike Graph' en tus aplicaciones."
echo "   Si no abre, ejecuta esto en terminal para ver el error:"
echo "   $JAVA_EXEC -jar $INSTALL_DIR/$JAR_NAME"