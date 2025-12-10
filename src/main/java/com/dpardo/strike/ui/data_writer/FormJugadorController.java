package com.dpardo.strike.ui.data_writer;

import com.dpardo.strike.domain.EquipoComboItem;
import com.dpardo.strike.domain.SexoComboItem;
import com.dpardo.strike.repository.EquipoRepository;
import com.dpardo.strike.repository.JugadorRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class FormJugadorController implements Initializable {

    // --- IDs exactos de tu FXML ---
    @FXML private TextField identificadorField;
    @FXML private TextField nombreField;
    @FXML private TextField fechaNacimientoField; // Es un TextField en tu FXML
    @FXML private ComboBox<SexoComboItem> sexoComboBox;
    @FXML private ComboBox<String> paisComboBox;
    @FXML private ComboBox<String> posicionComboBox; // Es un ComboBox en tu FXML
    @FXML private ComboBox<EquipoComboItem> equipoComboBox;
    @FXML private TextField alturaField;
    @FXML private TextField pesoField;
    @FXML private TextField estadisticaField;

    @FXML private Button guardarJugadorButton;
    @FXML private Button elegirFotoButton;
    @FXML private Label nombreBanderaLabel; // Usado para mostrar el nombre del archivo de foto

    // Repositorios
    private final JugadorRepository jugadorRepo = new JugadorRepository();
    private final EquipoRepository equipoRepo = new EquipoRepository();

    // Variable para almacenar los bytes de la foto seleccionada
    private byte[] fotoBytes = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarCombos();
    }

    private void cargarCombos() {
        try {
            // Sexo
            if (sexoComboBox != null) {
                sexoComboBox.getItems().addAll(
                        new SexoComboItem('M', "Masculino"),
                        new SexoComboItem('F', "Femenino")
                );
            }
            // Equipos
            if (equipoComboBox != null) {
                equipoComboBox.getItems().addAll(equipoRepo.obtenerEquiposParaCombo());
            }
            // Países
            if (paisComboBox != null) {
                paisComboBox.getItems().addAll(equipoRepo.obtenerCodigosPaises());
            }
            // Posiciones (Lista estática común)
            if (posicionComboBox != null) {
                posicionComboBox.getItems().addAll("Arquero", "Defensa", "Mediocampista", "Delantero");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleElegirFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto del Jugador");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        // Obtener la ventana actual para mostrar el diálogo modal
        Stage stage = (Stage) elegirFotoButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                // Leer el archivo a bytes
                fotoBytes = Files.readAllBytes(file.toPath());
                // Actualizar la etiqueta con el nombre del archivo
                if (nombreBanderaLabel != null) {
                    nombreBanderaLabel.setText(file.getName());
                }
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo leer la imagen: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleGuardarJugador(ActionEvent event) {
        try {
            // 1. Validaciones básicas
            if (identificadorField.getText().isEmpty() || nombreField.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", "ID y Nombre son obligatorios.");
                return;
            }

            int id = Integer.parseInt(identificadorField.getText());
            String nombre = nombreField.getText();

            // 2. Parsear fecha (formato YYYY-MM-DD)
            LocalDate nac = null;
            try {
                nac = LocalDate.parse(fechaNacimientoField.getText());
            } catch (DateTimeParseException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Formato Fecha", "Use el formato YYYY-MM-DD (ej: 2000-01-31)");
                return;
            }

            char sexo = 'M';
            if (sexoComboBox.getValue() != null) {
                sexo = sexoComboBox.getValue().getCodigo();
            }

            String pais = paisComboBox.getValue();
            String pos = posicionComboBox.getValue(); // Es combo, no textfield

            int idEquipo = 0;
            if (equipoComboBox.getValue() != null) {
                idEquipo = equipoComboBox.getValue().getId();
            }

            int altura = 0;
            if (!alturaField.getText().isEmpty()) {
                altura = Integer.parseInt(alturaField.getText());
            }

            BigDecimal peso = BigDecimal.ZERO;
            if (!pesoField.getText().isEmpty()) {
                peso = new BigDecimal(pesoField.getText());
            }

            Integer estadisticas = null;
            if (estadisticaField != null && !estadisticaField.getText().isEmpty()) {
                estadisticas = Integer.parseInt(estadisticaField.getText());
            }

            // 3. Guardar en Neo4j
            jugadorRepo.insertarJugador(id, nombre, nac, sexo, pais, pos, idEquipo, estadisticas, altura, peso, fotoBytes);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Jugador registrado correctamente.");
            limpiarFormulario();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Numérico", "ID, Altura o Peso inválidos.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", e.getMessage());
        }
    }

    private void limpiarFormulario() {
        identificadorField.clear();
        nombreField.clear();
        fechaNacimientoField.clear();
        posicionComboBox.getSelectionModel().clearSelection();
        alturaField.clear();
        pesoField.clear();
        estadisticaField.clear();
        fotoBytes = null;
        nombreBanderaLabel.setText("Foto");
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}