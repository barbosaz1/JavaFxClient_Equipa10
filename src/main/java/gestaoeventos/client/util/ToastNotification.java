package gestaoeventos.client.util;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * Esta classe permite mostrar notificações no canto inferior direito da
 * aplicação,
 * semelhante a como funcionam as notificações em aplicações web modernas.
 * 
 * Suporta três tipos de notificações:
 * - SUCESSO (verde): para operações bem-sucedidas
 * - ERRO (vermelho): para erros e falhas
 * - INFO (azul): para informações gerais
 * - AVISO (amarelo): para avisos importantes
 * 
 */
public class ToastNotification {

    /**
     * Enumeração que define os tipos de notificação disponíveis.
     * Cada tipo tem uma cor e ícone associado.
     */
    public enum ToastType {
        /** Notificação de sucesso */
        SUCESSO,
        /** Notificação de erro */
        ERRO,
        /** Notificação informativa */
        INFO,
        /** Notificação de aviso */
        AVISO
    }


    private static final Queue<Popup> activeToasts = new LinkedList<>();

    /** Número máximo de toasts visíveis ao mesmo tempo */
    private static final int MAX_VISIBLE_TOASTS = 5;

    /** Espaçamento vertical entre toasts */
    private static final double TOAST_SPACING = 10;

    /** Altura aproximada de cada toast */
    private static final double TOAST_HEIGHT = 70;

    /**
     * Mostra uma notificação de sucesso.
     * 
     */
    public static void sucesso(Window owner, String mensagem) {
        mostrar(owner, mensagem, ToastType.SUCESSO, null, 4000);
    }

    /**
     * Mostra uma notificação de erro.
     * 
     */
    public static void erro(Window owner, String mensagem) {
        mostrar(owner, mensagem, ToastType.ERRO, null, 6000);
    }

    /**
     * Mostra uma notificação de erro com código de erro.
     * 
     */
    public static void erro(Window owner, String mensagem, int codigoErro) {
        mostrar(owner, mensagem, ToastType.ERRO, codigoErro, 6000);
    }

    /**
     * Mostra uma notificação informativa.
     * 
     */
    public static void info(Window owner, String mensagem) {
        mostrar(owner, mensagem, ToastType.INFO, null, 4000);
    }

    /**
     * Mostra uma notificação de aviso.
     * 
     */
    public static void aviso(Window owner, String mensagem) {
        mostrar(owner, mensagem, ToastType.AVISO, null, 5000);
    }

    /**
     * Método principal que cria e exibe a notificação toast.
     * 
     */
    public static void mostrar(Window owner, String mensagem, ToastType tipo, Integer codigoErro, int duracaoMs) {
        if (owner == null) {
            System.err.println("ToastNotification: owner é null, não é possível mostrar notificação");
            return;
        }

        while (activeToasts.size() >= MAX_VISIBLE_TOASTS) {
            Popup oldToast = activeToasts.poll();
            if (oldToast != null) {
                oldToast.hide();
            }
        }

        Popup popup = new Popup();

        // Container principal do toast
        HBox toastContainer = new HBox(12);
        toastContainer.setAlignment(Pos.CENTER_LEFT);
        toastContainer.setPadding(new Insets(16, 20, 16, 20));
        toastContainer.setMaxWidth(400);
        toastContainer.setMinWidth(300);

        // Ícone baseado no tipo
        Label icone = new Label(getIcone(tipo));
        icone.setStyle("-fx-font-size: 20px;");

        // Container para texto
        VBox textoContainer = new VBox(4);

        // Título baseado no tipo
        Label titulo = new Label(getTitulo(tipo));
        titulo.setStyle("-fx-font-weight: 700; -fx-font-size: 14px; -fx-text-fill: white;");

        // Mensagem principal
        Label msgLabel = new Label(mensagem);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(320);
        msgLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.9);");

        textoContainer.getChildren().addAll(titulo, msgLabel);

        // Se houver código de erro, adicionar
        if (codigoErro != null) {
            Label codigoLabel = new Label("Código: " + codigoErro);
            codigoLabel.setStyle(
                    "-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.7); -fx-font-family: 'Consolas', monospace;");
            textoContainer.getChildren().add(codigoLabel);
        }

        toastContainer.getChildren().addAll(icone, textoContainer);

        // Estilo do container baseado no tipo
        String corFundo = getCorFundo(tipo);
        toastContainer.setStyle(
                "-fx-background-color: " + corFundo + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 5);" +
                        "-fx-border-color: rgba(255,255,255,0.15);" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;");

        popup.getContent().add(toastContainer);

        // Calcular posição Y baseada nos toasts ativos
        double offsetY = activeToasts.size() * (TOAST_HEIGHT + TOAST_SPACING);

        // Posicionar no canto inferior direito
        double x = owner.getX() + owner.getWidth() - 420;
        double y = owner.getY() + owner.getHeight() - 100 - offsetY;

        popup.show(owner, x, y);
        activeToasts.add(popup);

        // Animação de entrada
        toastContainer.setOpacity(0);
        toastContainer.setTranslateX(50);

        Timeline entradaAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(toastContainer.opacityProperty(), 0),
                        new KeyValue(toastContainer.translateXProperty(), 50)),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(toastContainer.opacityProperty(), 1, Interpolator.EASE_OUT),
                        new KeyValue(toastContainer.translateXProperty(), 0, Interpolator.EASE_OUT)));
        entradaAnimation.play();

        // Programar remoção automática
        PauseTransition pausa = new PauseTransition(Duration.millis(duracaoMs));
        pausa.setOnFinished(e -> fecharToast(popup, toastContainer));
        pausa.play();

        // Permitir fechar ao clicar
        toastContainer.setOnMouseClicked(e -> {
            pausa.stop();
            fecharToast(popup, toastContainer);
        });

        // Efeito hover
        toastContainer.setOnMouseEntered(e -> {
            toastContainer.setStyle(toastContainer.getStyle() + "-fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });
        toastContainer.setOnMouseExited(e -> {
            toastContainer.setStyle(
                    "-fx-background-color: " + corFundo + ";" +
                            "-fx-background-radius: 12;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 5);" +
                            "-fx-border-color: rgba(255,255,255,0.15);" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-width: 1;");
        });
    }

    /**
     * Fecha um toast com animação de saída.
     */
    private static void fecharToast(Popup popup, HBox container) {
        Timeline saidaAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(container.opacityProperty(), 1),
                        new KeyValue(container.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(150),
                        new KeyValue(container.opacityProperty(), 0, Interpolator.EASE_IN),
                        new KeyValue(container.translateXProperty(), 50, Interpolator.EASE_IN)));
        saidaAnimation.setOnFinished(e -> {
            popup.hide();
            activeToasts.remove(popup);
        });
        saidaAnimation.play();
    }

    /**
     * Retorna o ícone baseado no tipo de notificação.
     */
    private static String getIcone(ToastType tipo) {
        return switch (tipo) {
            case SUCESSO -> "✅";
            case ERRO -> "❌";
            case INFO -> "ℹ️";
            case AVISO -> "⚠️";
        };
    }

    /**
     * Retorna o título baseado no tipo de notificação
     */
    private static String getTitulo(ToastType tipo) {
        return switch (tipo) {
            case SUCESSO -> "Sucesso";
            case ERRO -> "Erro";
            case INFO -> "Informação";
            case AVISO -> "Aviso";
        };
    }

    /**
     * Retorna a cor de fundo baseada no tipo de notificação
     */
    private static String getCorFundo(ToastType tipo) {
        return switch (tipo) {
            case SUCESSO -> "linear-gradient(to right, #059669, #10b981)";
            case ERRO -> "linear-gradient(to right, #dc2626, #ef4444)";
            case INFO -> "linear-gradient(to right, #2563eb, #3b82f6)";
            case AVISO -> "linear-gradient(to right, #d97706, #f59e0b)";
        };
    }
}
