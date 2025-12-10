package com.dpardo.strike.ui.data_writer;

import com.dpardo.strike.domain.SessionManager;
import com.dpardo.strike.domain.UiComboItem;
import com.dpardo.strike.repository.SuperAdminRepository;
import com.dpardo.strike.util.NavigationUtils; // IMPORTANTE: Nueva utilidad
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeAdminController {

    //--- Componentes FXML ---
    @FXML private Button paisButton;
    @FXML private Button jugadorButton;
    @FXML private Button equiposButton;
    @FXML private Button partidoButton;
    @FXML private Button ligaButton;
    @FXML private Button editarButton;
    @FXML private Button borrarButton;

    @FXML private StackPane formContainer;

    @FXML private Button userInfoadminButton;
    @FXML private Tooltip usernameAdminTooltip;
    @FXML private ComboBox<UiComboItem> viewSelectorAdminComboBox;

    private final SuperAdminRepository repository = new SuperAdminRepository();
    private final Map<String, String> uiPathMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Cargar vista por defecto
        loadForm("/com/dpardo/strike/ui/data_writer/Form-pais.fxml");
        setupUserInfo();
        setupComboBox();
    }

    @FXML
    void handleMenuClick(ActionEvent event) {
        Object source = event.getSource();
        if (source == paisButton) loadForm("/com/dpardo/strike/ui/data_writer/Form-pais.fxml");
        else if (source == equiposButton) loadForm("/com/dpardo/strike/ui/data_writer/Form-equipo.fxml");
        else if (source == ligaButton) loadForm("/com/dpardo/strike/ui/data_writer/Form-liga.fxml");
        else if (source == partidoButton) loadForm("/com/dpardo/strike/ui/data_writer/Form-partido.fxml");
        else if (source == jugadorButton) loadForm("/com/dpardo/strike/ui/data_writer/Form-jugador.fxml");
        else if (source == editarButton) loadForm("/com/dpardo/strike/ui/data_writer/Form-editar.fxml");
        else if (source == borrarButton) loadForm("/com/dpardo/strike/ui/data_writer/Form-borrar.fxml");
    }

    @FXML
    private void handleUserInfoClick() {
        SessionManager sm = SessionManager.getInstance();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Sesión Neo4j");
        alert.setContentText("Usuario: " + sm.getUsername() + "\nRol: " + sm.getRole());
        alert.showAndWait();
    }

    @FXML
    private void handleViewSelection() {
        UiComboItem selectedUi = viewSelectorAdminComboBox.getValue();
        if (selectedUi != null) {
            String fxmlPath = uiPathMap.get(selectedUi.getCodigoComponente());
            if (fxmlPath != null) {
                // CAMBIO: Usamos NavigationUtils para cambiar de ventana limpiamente
                NavigationUtils.navigate(viewSelectorAdminComboBox, fxmlPath, selectedUi.getDescripcion());
            }
        }
    }

    private void setupUserInfo() {
        SessionManager sm = SessionManager.getInstance();
        if (sm.getUserId() != 0 && usernameAdminTooltip != null) {
            usernameAdminTooltip.setText("Usuario: " + sm.getUsername());
        }
    }

    private void setupComboBox() {
        uiPathMap.put("homeBorderPane", "/com/dpardo/strike/ui/read_only/Home-view.fxml");
        uiPathMap.put("adminBorderPane", "/com/dpardo/strike/ui/data_writer/Home-admin.fxml");
        uiPathMap.put("superadminBorderPane", "/com/dpardo/strike/ui/super_user/Home-superadmin.fxml");

        try {
            SessionManager sm = SessionManager.getInstance();
            if (sm.getUserId() != 0 && viewSelectorAdminComboBox != null) {
                viewSelectorAdminComboBox.setItems(FXCollections.observableArrayList(repository.obtenerUis(sm.getUserId())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewSelectorAdminComboBox.setOnAction(event -> handleViewSelection());
    }

    private void loadForm(String fxmlPath) {
        try {
            Node newFormNode = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            if (!formContainer.getChildren().isEmpty()) {
                Node oldFormNode = formContainer.getChildren().get(0);
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), oldFormNode);
                fadeOut.setFromValue(1.0); fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    formContainer.getChildren().setAll(newFormNode);
                    newFormNode.setOpacity(0.0);
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(200), newFormNode);
                    fadeIn.setFromValue(0.0); fadeIn.setToValue(1.0);
                    fadeIn.play();
                });
                fadeOut.play();
            } else {
                formContainer.getChildren().add(newFormNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}