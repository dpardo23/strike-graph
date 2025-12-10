package com.dpardo.strike;

import com.dpardo.strike.repository.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // --- OPTIMIZACIÓN: PRE-CALENTAMIENTO ---
        // Iniciamos la conexión en un hilo separado mientras carga la interfaz.
        // Cuando el usuario haga clic en "Login", la conexión ya estará lista.
        new Thread(() -> {
            System.out.println("🔥 Pre-calentando conexión a Neo4j...");
            try {
                // Esto fuerza la conexión inicial (Handshake TLS)
                DatabaseConnection.getDriver().verifyConnectivity();
                System.out.println("✅ Conexión lista en segundo plano.");
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo pre-conectar (se reintentará en el uso): " + e.getMessage());
            }
        }).start();
        // ---------------------------------------

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("ui/login/Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("strike");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Cerrando aplicación y liberando recursos...");
        DatabaseConnection.closeDriver();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}