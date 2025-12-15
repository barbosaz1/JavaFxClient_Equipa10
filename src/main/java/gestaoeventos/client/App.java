package gestaoeventos.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o Login inicialmente
        Parent root = FXMLLoader.load(getClass().getResource("/view/auth/Login.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/app-dark.css").toExternalForm());

        // Remove a barra
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setTitle("Gestão Eventos UPT");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}