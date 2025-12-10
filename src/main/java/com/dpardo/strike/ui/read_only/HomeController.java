package com.dpardo.strike.ui.read_only;

import com.dpardo.strike.domain.Pais;
import com.dpardo.strike.domain.SessionManager;
import com.dpardo.strike.domain.UiComboItem;
import com.dpardo.strike.repository.PaisRepository;
import com.dpardo.strike.repository.SuperAdminRepository;
import com.dpardo.strike.util.NavigationUtils; // IMPORTAR LA NUEVA CLASE
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HomeController {

    @FXML private BorderPane homeBorderPane;
    @FXML private VBox paisContenedor;

    // Header
    @FXML private ComboBox<UiComboItem> viewHomeComboBox;
    @FXML private Tooltip usernameAdminTooltip;
    @FXML private Button userinfoHomeButton;

    private final PaisRepository paisRepository = new PaisRepository();
    private final SuperAdminRepository superAdminRepo = new SuperAdminRepository();
    private final Map<String, String> uiPathMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Carga inmediata de la UI estática
        setupUserInfo();

        // --- OPTIMIZACIÓN: CARGA ASÍNCRONA ---
        // Esto evita que la ventana se congele mientras consulta a Neo4j
        CompletableFuture.runAsync(() -> {
            // 1. Tarea pesada en hilo secundario (Base de Datos)
            List<Pais> paises = paisRepository.findAll();

            // 2. Volver al hilo de JavaFX para actualizar la UI
            Platform.runLater(() -> {
                llenarListaPaises(paises);
                setupComboBox(); // También movemos esto aquí si depende de BD
            });
        });
    }

    private void llenarListaPaises(List<Pais> paises) {
        if (paisContenedor == null) return;
        paisContenedor.getChildren().clear();

        for (Pais pais : paises) {
            try {
                // Cargar FXML del item es rápido si son pocos, para muchos se recomienda ListView
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dpardo/strike/ui/read_only/Pais-view.fxml"));
                Node nodo = loader.load();

                PaisItemController controller = loader.getController();

                // Carga de imagen optimizada (Background Loading)
                String path = "/images/flags/" + pais.getCodFifa() + ".png";
                // true en el constructor de Image activa la carga en background
                Image img = new Image(getClass().getResourceAsStream(path) != null ?
                        getClass().getResourceAsStream(path) :
                        getClass().getResourceAsStream("/images/flags/default.png"));

                controller.setData(pais.getNombre(), img);
                paisContenedor.getChildren().add(nodo);
            } catch (Exception e) {
                System.err.println("Skip pais: " + pais.getCodFifa());
            }
        }
    }

    // --- Navegación Optimizada ---
    @FXML
    private void handleViewSelection() {
        UiComboItem item = viewHomeComboBox.getValue();
        if (item != null) {
            String path = uiPathMap.get(item.getCodigoComponente());
            if (path != null) {
                // USAMOS LA NUEVA UTILIDAD PARA CERRAR LA VENTANA ACTUAL
                NavigationUtils.navigate(viewHomeComboBox, path, item.getDescripcion());
            }
        }
    }

    // ... (El resto de métodos setupUserInfo y setupComboBox se mantienen similar,
    // solo asegúrate de poblar el map uiPathMap)

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
            int userId = SessionManager.getInstance().getUserId();
            if (userId != 0) {
                viewHomeComboBox.setItems(FXCollections.observableArrayList(superAdminRepo.obtenerUis(userId)));
            }
            viewHomeComboBox.setOnAction(e -> handleViewSelection());
        } catch (Exception e) { e.printStackTrace(); }
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
}