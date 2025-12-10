package com.dpardo.strike.ui.data_writer;

import com.dpardo.strike.domain.Pais;
import com.dpardo.strike.repository.PaisRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Optional;

public class FormEditarController {

    @FXML private ComboBox<String> codFifaComboBox; // ID confirmado
    @FXML private TextField nombrePaisEditField; // ID confirmado
    @FXML private Button guardarEditPaisButton; // ID confirmado

    private final PaisRepository paisRepository = new PaisRepository();
    private List<Pais> listaPaisesMemoria;

    @FXML
    public void initialize() {
        cargarCodigosFifa();
    }

    private void cargarCodigosFifa() {
        try {
            listaPaisesMemoria = paisRepository.findAll();
            List<String> codigos = listaPaisesMemoria.stream()
                    .map(Pais::getCodFifa)
                    .toList();

            if (codFifaComboBox != null) {
                codFifaComboBox.setItems(FXCollections.observableArrayList(codigos));
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar países: " + e.getMessage());
        }
    }

    @FXML
    private void handleGuardarEditPais() { // Método confirmado
        String codFifaSeleccionado = codFifaComboBox.getValue();
        String nuevoNombre = nombrePaisEditField.getText();

        if (codFifaSeleccionado == null || nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Validación", "Complete los campos.");
            return;
        }

        try {
            Optional<Pais> paisOriginalOpt = listaPaisesMemoria.stream()
                    .filter(p -> p.getCodFifa().equals(codFifaSeleccionado))
                    .findFirst();

            if (paisOriginalOpt.isEmpty()) return;

            String continenteActual = paisOriginalOpt.get().getContinente();
            Pais paisActualizado = new Pais(codFifaSeleccionado, nuevoNombre, continenteActual);

            if (paisRepository.update(paisActualizado)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "País actualizado.");
                nombrePaisEditField.clear();
                cargarCodigosFifa();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar.");
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}