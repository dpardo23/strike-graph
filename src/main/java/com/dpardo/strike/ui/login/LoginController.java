package com.dpardo.strike.ui.login;

import com.dpardo.strike.MainApplication;
import com.dpardo.strike.domain.SessionManager;
import com.dpardo.strike.repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private Button btnLogin; // Verifica si en tu FXML se llama 'btnLogin' o 'ingresarButton'

    @FXML
    private PasswordField pf_password; // Nombre original restaurado

    @FXML
    private TextField tf_username; // Nombre original restaurado

    private final UserRepository userRepository = new UserRepository();

    @FXML
    void handleLoginButtonAction(ActionEvent event) { // Verifica que tu FXML llame a este método
        String username = tf_username.getText();
        String password = pf_password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Campos vacíos", "Por favor ingrese usuario y contraseña.");
            return;
        }

        try {
            if (userRepository.isValidUser(username, password)) {
                int userId = userRepository.getUserId(username);
                String role = userRepository.getUserRole(username);

                if (role == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Sin Rol", "El usuario no tiene un rol activo.");
                    return;
                }

                SessionManager.getInstance().setUserId(userId);
                SessionManager.getInstance().setUsername(username);
                SessionManager.getInstance().setRole(role);

                userRepository.registrarSesion();

                redirigirPorRol(role);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Credenciales inválidas", "Usuario o contraseña incorrectos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Crítico", "Error de conexión", e.getMessage());
        }
    }

    private void redirigirPorRol(String role) {
        try {
            Stage stage = (Stage) tf_username.getScene().getWindow();
            FXMLLoader loader;

            switch (role) {
                case "super_user":
                    loader = new FXMLLoader(MainApplication.class.getResource("ui/super_user/Home-superadmin.fxml"));
                    break;
                case "data_writer":
                    loader = new FXMLLoader(MainApplication.class.getResource("ui/data_writer/Home-admin.fxml"));
                    break;
                case "read_only":
                    loader = new FXMLLoader(MainApplication.class.getResource("ui/read_only/Home-view.fxml"));
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Error", "Rol desconocido", role);
                    return;
            }

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error de carga de vista", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}