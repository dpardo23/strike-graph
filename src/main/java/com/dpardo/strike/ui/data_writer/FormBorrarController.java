package com.dpardo.strike.ui.data_writer;

import com.dpardo.strike.domain.Pais;
import com.dpardo.strike.repository.JugadorRepository;
import com.dpardo.strike.repository.PaisRepository;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.util.List;

public class FormBorrarController {

    // --- IDs EXACTOS según tu FXML ---
    @FXML private ComboBox<String> paisComboBox;
    @FXML private Button borrarPaisButton;

    @FXML private ComboBox<String> jugadorCombBox; // Ojo: en tu FXML está escrito 'CombBox'
    @FXML private Button borrarJugadorButton;

    // --- Repositorios ---
    private final PaisRepository paisRepo = new PaisRepository();
    private final JugadorRepository jugadorRepo = new JugadorRepository();

    @FXML
    public void initialize() {
        cargarPaises();
        cargarJugadores();
    }

    private void cargarPaises() {
        try {
            // Obtenemos la lista de objetos Pais y extraemos solo el código FIFA
            List<String> codigos = paisRepo.findAll().stream()
                    .map(Pais::getCodFifa)
                    .toList();

            if (paisComboBox != null) {
                paisComboBox.setItems(FXCollections.observableArrayList(codigos));
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Carga", "No se pudieron cargar los países: " + e.getMessage());
        }
    }

    private void cargarJugadores() {
        try {
            // Obtenemos los nombres de los jugadores
            List<String> nombres = jugadorRepo.obtenerTodosLosNombresDeJugadores();

            if (jugadorCombBox != null) {
                jugadorCombBox.setItems(FXCollections.observableArrayList(nombres));
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Carga", "No se pudieron cargar los jugadores: " + e.getMessage());
        }
    }

    // --- EVENTO 1: Borrar País ---
    @FXML
    void handleBorrarPais(ActionEvent event) {
        String codigoFifa = paisComboBox.getValue();

        if (codigoFifa == null || codigoFifa.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Por favor seleccione un país para eliminar.");
            return;
        }

        try {
            if (paisRepo.delete(codigoFifa)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "País eliminado correctamente de Neo4j.");
                // Recargamos la lista para que desaparezca el eliminado
                cargarPaises();
                paisComboBox.getSelectionModel().clearSelection();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo encontrar o eliminar el país.");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "Fallo al eliminar país: " + e.getMessage());
        }
    }

    // --- EVENTO 2: Borrar Jugador ---
    @FXML
    void handleBorrarJugador(ActionEvent event) {
        String nombreJugador = jugadorCombBox.getValue();

        if (nombreJugador == null || nombreJugador.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Por favor seleccione un jugador para eliminar.");
            return;
        }

        try {
            // El repositorio maneja la eliminación y sus relaciones
            jugadorRepo.eliminarJugadorPorNombre(nombreJugador);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Jugador eliminado correctamente de Neo4j.");

            // Recargamos la lista
            cargarJugadores();
            jugadorCombBox.getSelectionModel().clearSelection();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "Fallo al eliminar jugador: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}