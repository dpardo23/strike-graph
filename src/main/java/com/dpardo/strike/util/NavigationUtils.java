package com.dpardo.strike.util;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class NavigationUtils {

    public static void navigate(Node sourceNode, String fxmlPath, String titleIgnored) {
        try {
            Stage currentStage = (Stage) sourceNode.getScene().getWindow();
            double x = currentStage.getX();
            double y = currentStage.getY();

            Parent root = FXMLLoader.load(Objects.requireNonNull(NavigationUtils.class.getResource(fxmlPath)));
            root.setOpacity(0.0);
            Stage newStage = new Stage();
            newStage.setTitle("strike");
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            newStage.sizeToScene();
            newStage.setResizable(false);
            newStage.setX(x);
            newStage.setY(y);

            Node currentRoot = currentStage.getScene().getRoot();
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(event -> {
                currentStage.close();
                newStage.show();
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}