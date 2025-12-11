package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.AuthService;
import gestaoeventos.dto.LoginResponseDTO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML 
    private TextField txtNumero;
    
    @FXML 
    private PasswordField txtPassword;
    
    @FXML 
    private Label lblError; 
    
    @FXML 
    private Button btnLogin;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Método opcional chamado automaticamente pelo JavaFX
    }

    @FXML
    void handleLogin() {
        // Limpar mensagens anteriores
        if (lblError != null) lblError.setText("");
        
        String numStr = txtNumero.getText().trim();
        String pass = txtPassword.getText().trim();

        // Validação Local
        if (numStr.isEmpty() || pass.isEmpty()) {
            if (lblError != null) lblError.setText("⚠️ Preencha todos os campos.");
            shakeField(txtNumero);
            return;
        }

        // Tentar converter para Inteiro
        int numero;
        try {
            numero = Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            if (lblError != null) lblError.setText("⚠️ O número deve ser numérico.");
            shakeField(txtNumero);
            return;
        }

        //Loading State (bloquear botão)
        if (btnLogin != null) {
            btnLogin.setDisable(true);
            btnLogin.setText("A autenticar...");
        }

        //Chamada Assíncrona (Task)
        Task<LoginResponseDTO> task = new Task<>() {
            @Override
            protected LoginResponseDTO call() throws Exception {
                // Chama o serviço REST
                return authService.login(numero, pass);
            }
        };

        task.setOnSucceeded(e -> {
            // Sucesso: Guardar sessão e mudar de ecrã
            UserSession.getInstance().login(task.getValue());
            goToDashboard();
        });

        task.setOnFailed(e -> {
            // Erro: Restaurar botão e mostrar mensagem
            if (btnLogin != null) {
                btnLogin.setDisable(false);
                btnLogin.setText("Entrar");
            }
            Throwable ex = task.getException();
            if (lblError != null) lblError.setText("❌ " + ex.getMessage());
        });

        new Thread(task).start();
    }

    private void shakeField(Node node) {
        if (node == null) return;
        //borda vermelha
        node.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2px; -fx-border-radius: 6px;");
        
        // Remove borda após 2 segundos
        new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            Platform.runLater(() -> node.setStyle(""));
        }).start();
    }
    
    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainLayout.fxml"));
            Parent root = loader.load();
            
            // Obter a Stage atual
            Stage stage = (Stage) txtNumero.getScene().getWindow();
            Scene scene = new Scene(root);
            
            // Carregar CSS
            scene.getStylesheets().add(getClass().getResource("/css/app-theme.css").toExternalForm());
            
            stage.setScene(scene);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            if (lblError != null) lblError.setText("Erro crítico: Não foi possível carregar o Dashboard.");
        }
    }
}