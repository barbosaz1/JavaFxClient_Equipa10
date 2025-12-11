package gestaoeventos.client.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.animation.FadeTransition;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

/**
 * Classe utilitária para navegação entre páginas na aplicação.
 * 
 * O PageNavigator é responsável por carregar diferentes views FXML
 * para a área de conteúdo principal, com animações suaves de transição.
 * 
 */
public class PageNavigator {

    /** Área de conteúdo principal onde as páginas são carregadas */
    private static StackPane mainContentArea;

    /**
     * Define a área de conteúdo onde as páginas serão carregadas.
     * Deve ser chamado uma vez durante a inicialização da aplicação.
     * 
     */
    public static void setContentArea(StackPane area) {
        mainContentArea = area;
    }

    /**
     * Carrega um ficheiro FXML para a área de conteúdo principal.
     * Inclui uma animação de fade-in para transições suaves.
     * 
     */
    public static void loadPage(String fxmlName) {
        if (mainContentArea == null) {
            System.err.println("[PageNavigator] Área de conteúdo não definida!");
            return;
        }

        try {
            URL resource = PageNavigator.class.getResource("/view/" + fxmlName);
            if (resource == null) {
                throw new IOException("Ficheiro FXML não encontrado: " + fxmlName);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            // Animação
            view.setOpacity(0);
            mainContentArea.getChildren().setAll(view);

            FadeTransition fade = new FadeTransition(Duration.millis(200), view);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (IOException e) {
            System.err.println("[PageNavigator] Erro ao carregar página: " + fxmlName);
            e.printStackTrace();

            // Notificar utilizador se a window existir
            if (mainContentArea.getScene() != null && mainContentArea.getScene().getWindow() != null) {
                ToastNotification.erro(mainContentArea.getScene().getWindow(),
                        "Não foi possível carregar a página: " + fxmlName);
            }
        }
    }

    /**
     * Obtém um FXMLLoader para carregar views manualmente.
     * Útil para abrir modais ou obter acesso ao controller.
     * 
     */
    public static FXMLLoader getLoader(String fxmlName) throws IOException {
        URL resource = PageNavigator.class.getResource("/view/" + fxmlName);
        if (resource == null) {
            throw new IOException("Ficheiro FXML não encontrado: " + fxmlName);
        }
        return new FXMLLoader(resource);
    }
}