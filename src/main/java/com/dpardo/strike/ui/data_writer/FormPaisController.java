package com.dpardo.strike.ui.data_writer;

import com.dpardo.strike.domain.Pais;
import com.dpardo.strike.repository.PaisRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;

public class FormPaisController {

    // --- IDs EXACTOS de tu Form-pais.fxml ---
    @FXML private TextField codigoFifaField; // <--- ESTE ERA EL CULPABLE
    @FXML private TextField nombrePaisField;
    @FXML private ComboBox<String> continenteComboBox;
    @FXML private Button elegirBanderaButton;
    @FXML private Label nombreBanderaLabel;
    @FXML private Button guardarPaisButton;

    private final PaisRepository paisRepo = new PaisRepository();

    @FXML
    void initialize() {
        if (continenteComboBox != null) {
            continenteComboBox.getItems().addAll("Sudamérica (CONMEBOL)", "Europa (UEFA)", "Norteamérica (CONCACAF)", "África (CAF)", "Asia (AFC)");
        }
    }

    @FXML
    void handleElegirBandera(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(null);
        if (f != null && nombreBanderaLabel != null) {
            nombreBanderaLabel.setText(f.getName());
        }
    }

    @FXML
    void handleGuardarPais(ActionEvent event) {
        try {
            // 1. Obtención de datos directa (sin getters complejos)
            String cod = codigoFifaField.getText();
            String nom = nombrePaisField.getText();
            String cont = continenteComboBox.getValue();

            // Depuración para confirmar
            System.out.println("DEBUG: Guardando -> Cod=" + cod + ", Nom=" + nom + ", Cont=" + cont);

            // 2. Validación
            if (cod == null || cod.trim().isEmpty() ||
                    nom == null || nom.trim().isEmpty() ||
                    cont == null || cont.trim().isEmpty()) {

                showAlert(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor llene: Código, Nombre y Continente.");
                return;
            }

            // 3. Guardado
            paisRepo.save(new Pais(cod, nom, cont));

            showAlert(Alert.AlertType.INFORMATION, "Éxito", "País guardado correctamente.");
            limpiarCampos();

        } catch (Exception e) {
            e.printStackTrace(); // Ver el error completo en consola
            showAlert(Alert.AlertType.ERROR, "Error al Guardar", e.getMessage());
        }
    }

    private void limpiarCampos() {
        codigoFifaField.clear();
        nombrePaisField.clear();
        continenteComboBox.getSelectionModel().clearSelection();
        if(nombreBanderaLabel != null) nombreBanderaLabel.setText("Bandera");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}