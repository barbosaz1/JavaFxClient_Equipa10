package gestaoeventos.client.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class BaseController {
    // Guarda referência ao layout principal
    protected static BorderPane mainLayout;

    public static void setMainLayout(BorderPane layout) {
        mainLayout = layout;
    }

    protected void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent view = loader.load();
            if (mainLayout != null) {
                mainLayout.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Implementar alerta de erro aqui
        }
    }
}