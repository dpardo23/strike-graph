package com.dpardo.strike.ui.super_user;

import com.dpardo.strike.domain.SessionManager;
import com.dpardo.strike.domain.SessionViewModel;
import com.dpardo.strike.domain.UiComboItem;
import com.dpardo.strike.repository.SuperAdminRepository;
import com.dpardo.strike.util.NavigationUtils; // IMPORTANTE
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SuperAdminController implements Initializable {

    // --- IDs CORREGIDOS ---
    @FXML private TableView<SessionViewModel> sesionesTableView;

    @FXML private TableColumn<SessionViewModel, Integer> pidColumn;
    @FXML private TableColumn<SessionViewModel, String> userColumn;
    @FXML private TableColumn<SessionViewModel, String> correoColumn;
    @FXML private TableColumn<SessionViewModel, Timestamp> fecCreacionColumn;
    @FXML private TableColumn<SessionViewModel, String> rolColumn;
    @FXML private TableColumn<SessionViewModel, String> uiColumn;
    @FXML private TableColumn<SessionViewModel, String> direccionIpColumn;
    @FXML private TableColumn<SessionViewModel, Integer> puertoColumn;
    @FXML private TableColumn<SessionViewModel, Timestamp> fecAsignacionColumn;

    // --- Header Components ---
    @FXML private ComboBox<UiComboItem> viewSelectorComboBox;
    @FXML private Button userInfoButton;

    // --- Lógica ---
    private final SuperAdminRepository repo = new SuperAdminRepository();
    private final Map<String, String> uiPathMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        cargarDatos();
        setupComboBox();
    }

    private void configurarColumnas() {
        if (pidColumn != null) pidColumn.setCellValueFactory(new PropertyValueFactory<>("pid"));
        if (userColumn != null) userColumn.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        if (correoColumn != null) correoColumn.setCellValueFactory(new PropertyValueFactory<>("correo"));
        if (fecCreacionColumn != null) fecCreacionColumn.setCellValueFactory(new PropertyValueFactory<>("fecCreacionUsuario"));
        if (rolColumn != null) rolColumn.setCellValueFactory(new PropertyValueFactory<>("nombreRol"));
        if (uiColumn != null) uiColumn.setCellValueFactory(new PropertyValueFactory<>("codComponenteUi"));
        if (direccionIpColumn != null) direccionIpColumn.setCellValueFactory(new PropertyValueFactory<>("direccionIp"));
        if (puertoColumn != null) puertoColumn.setCellValueFactory(new PropertyValueFactory<>("puerto"));
        if (fecAsignacionColumn != null) fecAsignacionColumn.setCellValueFactory(new PropertyValueFactory<>("fechaAsignacionRol"));
    }

    private void cargarDatos() {
        try {
            if (sesionesTableView != null) {
                List<SessionViewModel> lista = repo.obtenerSesionesActivas();
                ObservableList<SessionViewModel> datos = FXCollections.observableArrayList(lista);
                sesionesTableView.setItems(datos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar datos: " + e.getMessage());
        }
    }

    // --- Lógica del Header ---

    private void setupComboBox() {
        uiPathMap.put("homeBorderPane", "/com/dpardo/strike/ui/read_only/Home-view.fxml");
        uiPathMap.put("adminBorderPane", "/com/dpardo/strike/ui/data_writer/Home-admin.fxml");
        uiPathMap.put("superadminBorderPane", "/com/dpardo/strike/ui/super_user/Home-superadmin.fxml");

        try {
            SessionManager sm = SessionManager.getInstance();
            if (sm.getUserId() != 0 && viewSelectorComboBox != null) {
                List<UiComboItem> uis = repo.obtenerUis(sm.getUserId());
                viewSelectorComboBox.setItems(FXCollections.observableArrayList(uis));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(viewSelectorComboBox != null) {
            viewSelectorComboBox.setOnAction(event -> handleViewSelection());
        }
    }

    @FXML
    private void handleViewSelection() {
        UiComboItem selectedUi = viewSelectorComboBox.getValue();
        if (selectedUi != null) {
            String fxmlPath = uiPathMap.get(selectedUi.getCodigoComponente());
            if (fxmlPath != null) {
                // CAMBIO: Usamos NavigationUtils para cerrar esta ventana y abrir la siguiente
                NavigationUtils.navigate(viewSelectorComboBox, fxmlPath, selectedUi.getDescripcion());
            }
        }
    }

    @FXML
    private void handleUserInfoClick() {
        SessionManager sm = SessionManager.getInstance();
        mostrarAlerta("Usuario: " + sm.getUsername() + "\nRol: " + sm.getRole() + "\nPID: " + sm.getPid());
    }

    @FXML
    void onActualizarClick() {
        cargarDatos();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}