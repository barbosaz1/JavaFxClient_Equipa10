package gestaoeventos.client.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class TitleBarController {
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML void close(javafx.event.ActionEvent e) { System.exit(0); }
    
    @FXML void min(javafx.event.ActionEvent e) {
        getStage(e).setIconified(true);
    }
    
    @FXML void max(javafx.event.ActionEvent e) {
        Stage s = getStage(e);
        s.setMaximized(!s.isMaximized());
    }

    @FXML void onPress(MouseEvent e) {
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML void onDrag(MouseEvent e) {
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        if(!s.isMaximized()) {
            s.setX(e.getScreenX() - xOffset);
            s.setY(e.getScreenY() - yOffset);
        }
    }

    private Stage getStage(javafx.event.ActionEvent e) {
        return (Stage) ((Node) e.getSource()).getScene().getWindow();
    }
}