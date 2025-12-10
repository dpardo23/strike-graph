package com.dpardo.strike.ui.data_writer;

import com.dpardo.strike.repository.EquipoRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class FormEquipoController implements Initializable {

    // --- IDs EXACTOS de tu FXML ---
    @FXML private TextField identificadorField;   // Coincide con tu FXML
    @FXML private TextField nombreEquipoField;
    @FXML private ComboBox<String> paisComboBox;
    @FXML private TextField ciudadField;
    @FXML private TextField fechaFundacionField;  // Es TextField en tu FXML, no DatePicker
    @FXML private TextField directorTecnicoField;
    @FXML private Button guardarEquipoButton;

    private final EquipoRepository equipoRepository = new EquipoRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarPaises();
    }

    private void cargarPaises() {
        try {
            if (paisComboBox != null) {
                paisComboBox.getItems().addAll(equipoRepository.obtenerCodigosPaises());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleGuardarEquipo() {
        try {
            // 1. Validar campos obligatorios
            if (identificadorField.getText().isEmpty() || nombreEquipoField.getText().isEmpty() || paisComboBox.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Complete ID, Nombre y País.");
                return;
            }

            int id = Integer.parseInt(identificadorField.getText());
            String nombre = nombreEquipoField.getText();
            String pais = paisComboBox.getValue();
            String ciudad = ciudadField.getText();
            String dt = directorTecnicoField.getText();

            // 2. Parsear fecha (formato YYYY-MM-DD)
            LocalDate fundacion = null;
            String fechaTexto = fechaFundacionField.getText();
            if (fechaTexto != null && !fechaTexto.trim().isEmpty()) {
                try {
                    fundacion = LocalDate.parse(fechaTexto);
                } catch (DateTimeParseException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Formato Fecha", "Use el formato YYYY-MM-DD (ej: 1902-03-06)");
                    return;
                }
            }

            // 3. Guardar en Neo4j
            equipoRepository.insertarEquipo(id, nombre, pais, ciudad, fundacion, dt);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Equipo registrado correctamente.");
            limpiar();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Numérico", "El ID debe ser un número entero.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", e.getMessage());
        }
    }

    private void limpiar() {
        identificadorField.clear();
        nombreEquipoField.clear();
        ciudadField.clear();
        directorTecnicoField.clear();
        fechaFundacionField.clear();
        paisComboBox.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}