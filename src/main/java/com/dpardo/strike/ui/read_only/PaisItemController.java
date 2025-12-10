package com.dpardo.strike.ui.read_only;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PaisItemController {

    @FXML
    private ImageView imagenBandera;

    @FXML
    private Label nombrePaisLabel;

    public void setData(String nombre, Image bandera) {
        if (nombrePaisLabel != null) {
            nombrePaisLabel.setText(nombre);
        }
        if (imagenBandera != null && bandera != null) {
            imagenBandera.setImage(bandera);
        }
    }
}