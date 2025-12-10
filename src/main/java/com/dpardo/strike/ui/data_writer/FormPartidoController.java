package com.dpardo.strike.ui.data_writer;

import com.dpardo.strike.domain.EquipoComboItem;
import com.dpardo.strike.domain.Liga; // Importante
import com.dpardo.strike.repository.EquipoRepository;
import com.dpardo.strike.repository.LigaRepository;
import com.dpardo.strike.repository.PartidoRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter; // Para mostrar solo el nombre en el combo

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class FormPartidoController implements Initializable {

    // --- IDs EXACTOS de tu FXML ---
    @FXML private ComboBox<EquipoComboItem> equipoLocalComboBox;
    @FXML private ComboBox<EquipoComboItem> equipoVisitanteComboBox;
    @FXML private TextField fechaField; // Es TextField
    @FXML private TextField HoraField;  // Ojo con la mayúscula inicial
    @FXML private ComboBox<Liga> ligaComboBox; // Es ComboBox
    @FXML private TextField historialField;
    @FXML private Button guardarPartidoButton;

    private final PartidoRepository partidoRepo = new PartidoRepository();
    private final EquipoRepository equipoRepo = new EquipoRepository();
    private final LigaRepository ligaRepo = new LigaRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarCombos();
    }

    private void cargarCombos() {
        try {
            // Equipos
            var equipos = equipoRepo.obtenerEquiposParaCombo();
            if (equipoLocalComboBox != null) equipoLocalComboBox.getItems().addAll(equipos);
            if (equipoVisitanteComboBox != null) equipoVisitanteComboBox.getItems().addAll(equipos);

            // Ligas (Aquí estaba el fallo: no se cargaban)
            if (ligaComboBox != null) {
                ligaComboBox.getItems().addAll(ligaRepo.findAll());

                // Configurar para que muestre el nombre de la liga en el combo
                ligaComboBox.setConverter(new StringConverter<Liga>() {
                    @Override
                    public String toString(Liga liga) {
                        return (liga != null) ? liga.getNombre() : "";
                    }

                    @Override
                    public Liga fromString(String string) {
                        return null; // No necesitamos esto para un combo de solo lectura
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleGuardarPartido() {
        try {
            // 1. Validaciones
            if (equipoLocalComboBox.getValue() == null || equipoVisitanteComboBox.getValue() == null ||
                    ligaComboBox.getValue() == null || fechaField.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Seleccione equipos, liga y fecha.");
                return;
            }

            int localId = equipoLocalComboBox.getValue().getId();
            int visitaId = equipoVisitanteComboBox.getValue().getId();
            int ligaId = ligaComboBox.getValue().getId();

            // 2. Parsear Fecha
            LocalDate fecha = null;
            try {
                fecha = LocalDate.parse(fechaField.getText());
            } catch (DateTimeParseException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Formato Fecha", "Use YYYY-MM-DD (ej: 2025-10-25)");
                return;
            }

            // 3. Parsear Hora
            LocalTime hora = LocalTime.NOON; // Valor por defecto
            String horaTexto = HoraField.getText();
            if (horaTexto != null && !horaTexto.trim().isEmpty()) {
                if (horaTexto.length() == 5) horaTexto += ":00"; // Agregar segundos si falta
                try {
                    hora = LocalTime.parse(horaTexto);
                } catch (DateTimeParseException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Formato Hora", "Use HH:mm:ss o HH:mm (ej: 18:30)");
                    return;
                }
            }

            // 4. Historial (ID único del partido)
            int historialId = 0;
            try {
                historialId = Integer.parseInt(historialField.getText());
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Historial", "El número de historial debe ser un entero.");
                return;
            }

            // 5. Guardar en Neo4j
            partidoRepo.insertarPartido(localId, visitaId, fecha, hora, ligaId, historialId);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Partido programado correctamente.");
            limpiar();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", e.getMessage());
        }
    }

    private void limpiar() {
        equipoLocalComboBox.getSelectionModel().clearSelection();
        equipoVisitanteComboBox.getSelectionModel().clearSelection();
        ligaComboBox.getSelectionModel().clearSelection();
        fechaField.clear();
        HoraField.clear();
        historialField.clear();
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}