package com.dpardo.strike.ui.data_writer;

import com.dpardo.strike.domain.Liga;
import com.dpardo.strike.domain.Pais;
import com.dpardo.strike.repository.LigaRepository;
import com.dpardo.strike.repository.PaisRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class FormLigaController implements Initializable {

    // --- IDs EXACTOS de tu Form-liga.fxml ---
    @FXML private TextField identificadorField;   // ID
    @FXML private TextField nombreLigaField;      // Nombre
    @FXML private ComboBox<String> paisComboBox;  // País
    @FXML private ComboBox<String> ligaComboBox;  // Tipo de Liga
    @FXML private Button guardarLigaButton;

    private final LigaRepository ligaRepository = new LigaRepository();
    private final PaisRepository paisRepository = new PaisRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarPaises();
        cargarTiposLiga();
    }

    private void cargarPaises() {
        try {
            // Llenamos el combo solo con los códigos FIFA
            for (Pais p : paisRepository.findAll()) {
                if (paisComboBox != null) paisComboBox.getItems().add(p.getCodFifa());
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar países: " + e.getMessage());
        }
    }

    private void cargarTiposLiga() {
        if (ligaComboBox != null) {
            ligaComboBox.getItems().addAll(
                    "Profesional",
                    "Amateur",
                    "Semi-profesional",
                    "Con playoffs o eliminatorias",
                    "Con sistema de divisiones",
                    "Regional/Local"
            );
        }
    }

    @FXML
    void handleGuardarLiga() {
        try {
            // 1. Obtener valores
            String idStr = (identificadorField != null) ? identificadorField.getText() : "";
            String nombre = (nombreLigaField != null) ? nombreLigaField.getText() : "";
            String pais = (paisComboBox != null) ? paisComboBox.getValue() : null;
            String tipo = (ligaComboBox != null) ? ligaComboBox.getValue() : null;

            // 2. Validar
            if (nombre.isEmpty() || pais == null || tipo == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Complete Nombre, País y Tipo.");
                return;
            }

            // 3. Manejo del ID
            // Si el repo genera ID automático, usamos 0. Si quieres usar el del campo, parsealo.
            int id = 0;
            if (!idStr.isEmpty()) {
                try {
                    id = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Formato ID", "El ID debe ser numérico.");
                    return;
                }
            }

            // 4. Crear objeto y guardar
            // Nota: Si el repo usa autoincremental interno (max + 1), el ID que pases aquí podría ser ignorado
            // dependiendo de cómo esté implementado el save(). Revisaremos eso.
            Liga nuevaLiga = new Liga(id, nombre, tipo, pais);

            if (ligaRepository.save(nuevaLiga)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Liga guardada correctamente.");
                limpiar();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la liga.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", e.getMessage());
        }
    }

    private void limpiar() {
        if(identificadorField != null) identificadorField.clear();
        if(nombreLigaField != null) nombreLigaField.clear();
        if(paisComboBox != null) paisComboBox.getSelectionModel().clearSelection();
        if(ligaComboBox != null) ligaComboBox.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}