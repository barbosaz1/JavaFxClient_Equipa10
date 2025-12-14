package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.util.PageNavigator;
import gestaoeventos.entity.PerfilUtilizador;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane contentArea;
    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox sidebarMenu;

    // Botões comuns
    @FXML
    private Button btnOverview, btnEventos, btnInscricoes, btnCalendario, btnCertificados;

    // Botões staff (Docente, Gestor)
    @FXML
    private Separator separatorStaff;
    @FXML
    private Label lblStaff;
    @FXML
    private Button btnCheckIn, btnDocentePanel, btnGestorPanel;

    // Botões admin
    @FXML
    private Separator separatorAdmin;
    @FXML
    private Label lblAdmin;
    @FXML
    private Button btnAdminPanel;

    @FXML
    private Label lblUserName, lblUserRole;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PageNavigator.setContentArea(contentArea);
        setupPermissions();
        navOverview();
    }

    private void setupPermissions() {
        if (!UserSession.getInstance().isLoggedIn())
            return;

        var user = UserSession.getInstance().getUser();
        lblUserName.setText(user.getNome());
        lblUserRole.setText(user.getPerfil().toString());

        PerfilUtilizador role = user.getPerfil();

        // Verificar perfis
        boolean isAdmin = (role == PerfilUtilizador.ADMIN);
        boolean isGestor = (role == PerfilUtilizador.GESTOR_EVENTOS);
        boolean isDocente = (role == PerfilUtilizador.DOCENTE);
        boolean isStaff = isAdmin || isGestor || isDocente;

        // Mostrar secção staff se for staff
        setVisibilidade(separatorStaff, isStaff);
        setVisibilidade(lblStaff, isStaff);
        setVisibilidade(btnCheckIn, isStaff);

        // Botão Docente (apenas para Docentes e Admin)
        setVisibilidade(btnDocentePanel, isDocente || isAdmin);

        // Botão Gestor (apenas para Gestores e Admin)
        setVisibilidade(btnGestorPanel, isGestor || isAdmin);

        // Secção Admin (apenas para Admin)
        setVisibilidade(separatorAdmin, isAdmin);
        setVisibilidade(lblAdmin, isAdmin);
        setVisibilidade(btnAdminPanel, isAdmin);
    }

    private void setVisibilidade(javafx.scene.Node node, boolean visivel) {
        if (node == null)
            return;
        node.setVisible(visivel);
        node.setManaged(visivel);
    }

    // --- Navegação Principal ---
    @FXML
    void navOverview() {
        setActive(btnOverview);
        PageNavigator.loadPage("Overview.fxml");
    }

    @FXML
    void navEventos() {
        setActive(btnEventos);
        PageNavigator.loadPage("Eventos.fxml");
    }

    @FXML
    void navInscricoes() {
        setActive(btnInscricoes);
        PageNavigator.loadPage("Inscricoes.fxml");
    }

    @FXML
    void navCalendario() {
        setActive(btnCalendario);
        PageNavigator.loadPage("Calendario.fxml");
    }

    @FXML
    void navCertificados() {
        setActive(btnCertificados);
        PageNavigator.loadPage("Certificados.fxml");
    }

    // --- Navegação Staff ---
    @FXML
    void navCheckIn() {
        setActive(btnCheckIn);
        PageNavigator.loadPage("CheckIn.fxml");
    }

    @FXML
    void navDocentePanel() {
        setActive(btnDocentePanel);
        PageNavigator.loadPage("DocentePanel.fxml");
    }

    @FXML
    void navGestorPanel() {
        setActive(btnGestorPanel);
        PageNavigator.loadPage("GestorPanel.fxml");
    }

    // --- Navegação Admin ---
    @FXML
    void navAdminPanel() {
        setActive(btnAdminPanel);
        PageNavigator.loadPage("AdminPanel.fxml");
    }

    private void setActive(Button btn) {
        // Remover active de todos os botões
        Button[] allButtons = { btnOverview, btnEventos, btnInscricoes, btnCalendario, btnCertificados,
                btnCheckIn, btnDocentePanel, btnGestorPanel, btnAdminPanel };
        for (Button b : allButtons) {
            if (b != null)
                b.getStyleClass().remove("active");
        }
        if (btn != null)
            btn.getStyleClass().add("active");
    }

    // --- Logout ---
    @FXML
    void logout() {
        UserSession.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();

            Scene scene = new Scene(root);
            if (getClass().getResource("/css/app-theme.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/css/app-theme.css").toExternalForm());
            }
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}