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
import javafx.scene.control.CheckBox;
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
    private TextField txtPasswordVisible;
    @FXML
    private CheckBox chkMostrarPassword;
    @FXML
    private Label lblFeedback;
    @FXML
    private Button btnLogin;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        if (txtPasswordVisible != null && txtPassword != null) {
            txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
        }
    }

    @FXML
    void togglePasswordVisibility() {
        if (chkMostrarPassword == null)
            return;

        boolean mostrar = chkMostrarPassword.isSelected();
        txtPassword.setVisible(!mostrar);
        txtPassword.setManaged(!mostrar);
        txtPasswordVisible.setVisible(mostrar);
        txtPasswordVisible.setManaged(mostrar);

        if (mostrar) {
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
    }

    @FXML
    void handleLogin() {
        if (lblFeedback != null)
            lblFeedback.setText("");

        String numStr = txtNumero.getText().trim();
        String pass = txtPassword.getText().trim();

        if (numStr.isEmpty() || pass.isEmpty()) {
            mostrarErro("⚠️ Preencha todos os campos.");
            shakeField(txtNumero);
            return;
        }

        int numero;
        try {
            numero = Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            mostrarErro("⚠️ O número deve ser numérico.");
            shakeField(txtNumero);
            return;
        }

        if (btnLogin != null) {
            btnLogin.setDisable(true);
            btnLogin.setText("A autenticar...");
        }

        Task<LoginResponseDTO> task = new Task<>() {
            @Override
            protected LoginResponseDTO call() throws Exception {
                return authService.login(numero, pass);
            }
        };

        task.setOnSucceeded(e -> {
            UserSession.getInstance().login(task.getValue());
            goToDashboard();
        });

        task.setOnFailed(e -> {
            if (btnLogin != null) {
                btnLogin.setDisable(false);
                btnLogin.setText("Entrar no Portal");
            }

            Throwable ex = task.getException();
            String mensagem = ex.getMessage();

            if (mensagem != null && mensagem.toLowerCase().contains("inativo")) {
                mostrarErro("❌ " + mensagem);
                shakeField(txtNumero);
            } else if (mensagem != null && mensagem.toLowerCase().contains("credenciais")) {
                mostrarErro("❌ Credenciais inválidas. Verifique os seus dados.");
                shakeField(txtPassword);
            } else {
                mostrarErro("❌ Erro de autenticação: " + (mensagem != null ? mensagem : "Verifique a ligação."));
            }
        });

        new Thread(task).start();
    }

    private void mostrarErro(String mensagem) {
        if (lblFeedback != null) {
            lblFeedback.setText(mensagem);
            lblFeedback.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    private void shakeField(Node node) {
        if (node == null)
            return;
        node.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2px; -fx-border-radius: 6px;");

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            Platform.runLater(() -> node.setStyle(""));
        }).start();
    }

    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainLayout.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) txtNumero.getScene().getWindow();
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/css/app-theme.css").toExternalForm());

            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarErro("Erro crítico: Não foi possível carregar o Dashboard.");
        }
    }
}