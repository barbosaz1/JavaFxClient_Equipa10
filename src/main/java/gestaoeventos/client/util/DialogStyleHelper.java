package gestaoeventos.client.util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

/**
 * Classe utilitária para aplicar estilos consistentes a todos os diálogos.
 * Garante um visual dark premium moderno em toda a aplicação.
 */
public class DialogStyleHelper {

    // Cores do tema
    private static final String COLOR_BG_CARD = "#18181b";
    private static final String COLOR_BG_SURFACE = "#27272a";
    private static final String COLOR_TEXT_PRIMARY = "#fafafa";
    private static final String COLOR_TEXT_SECONDARY = "#a1a1aa";
    private static final String COLOR_TEXT_MUTED = "#71717a";
    private static final String COLOR_BORDER = "#3f3f46";
    private static final String COLOR_ACCENT = "#8b5cf6";
    private static final String COLOR_SUCCESS = "#22c55e";
    private static final String COLOR_DANGER = "#ef4444";
    private static final String COLOR_WARNING = "#f59e0b";

    /**
     * Aplica estilo premium a um Dialog genérico.
     */
    public static void styleDialog(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();

        // Aplicar stylesheet
        pane.getStylesheets().add(
                DialogStyleHelper.class.getResource("/css/app-theme.css").toExternalForm());

        // Estilo base do painel
        pane.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-background-radius: 16;
                -fx-border-color: %s;
                -fx-border-radius: 16;
                -fx-border-width: 1;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 30, 0, 0, 8);
                """, COLOR_BG_CARD, COLOR_BORDER));

        // Estilo do header
        if (pane.getHeader() != null || pane.getHeaderText() != null) {
            pane.lookup(".header-panel");
        }

        // Estilizar botões
        styleDialogButtons(pane);

        // Remover decorações da janela para visual mais limpo
        dialog.initStyle(StageStyle.TRANSPARENT);

        // Tamanho mínimo
        pane.setMinWidth(400);
    }

    /**
     * Aplica estilo premium a um Alert.
     */
    public static void styleAlert(Alert alert) {
        DialogPane pane = alert.getDialogPane();

        pane.getStylesheets().add(
                DialogStyleHelper.class.getResource("/css/app-theme.css").toExternalForm());

        // Cor de fundo baseada no tipo de alert
        String headerColor = switch (alert.getAlertType()) {
            case CONFIRMATION -> COLOR_ACCENT;
            case ERROR -> COLOR_DANGER;
            case WARNING -> COLOR_WARNING;
            case INFORMATION -> COLOR_SUCCESS;
            default -> COLOR_TEXT_SECONDARY;
        };

        pane.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-background-radius: 16;
                -fx-border-color: %s;
                -fx-border-radius: 16;
                -fx-border-width: 1;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 30, 0, 0, 8);
                """, COLOR_BG_CARD, COLOR_BORDER));

        // Estilizar header
        Region header = (Region) pane.lookup(".header-panel");
        if (header != null) {
            header.setStyle(String.format("""
                    -fx-background-color: linear-gradient(to right, %s22, transparent);
                    -fx-background-radius: 16 16 0 0;
                    -fx-padding: 20;
                    """, headerColor));
        }

        // Estilizar labels
        pane.lookupAll(".label").forEach(node -> {
            if (node instanceof Label label) {
                label.setStyle(String.format("-fx-text-fill: %s;", COLOR_TEXT_PRIMARY));
            }
        });

        // Estilizar content
        pane.lookupAll(".content").forEach(node -> {
            node.setStyle("-fx-padding: 20;");
        });

        styleDialogButtons(pane);

        pane.setMinWidth(400);
    }

    /**
     * Estiliza os botões do diálogo.
     */
    private static void styleDialogButtons(DialogPane pane) {
        // Estilizar todos os botões
        pane.getButtonTypes().forEach(buttonType -> {
            Button button = (Button) pane.lookupButton(buttonType);
            if (button != null) {
                if (buttonType == ButtonType.OK || buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    // Botão primário
                    button.setStyle(String.format("""
                            -fx-background-color: linear-gradient(to bottom right, #6b4df7, #8b5cf6);
                            -fx-text-fill: white;
                            -fx-font-weight: 600;
                            -fx-font-size: 13px;
                            -fx-background-radius: 8;
                            -fx-padding: 10 24;
                            -fx-cursor: hand;
                            """));
                } else if (buttonType == ButtonType.CANCEL) {
                    // Botão secundário
                    button.setStyle(String.format("""
                            -fx-background-color: transparent;
                            -fx-text-fill: %s;
                            -fx-font-weight: 500;
                            -fx-font-size: 13px;
                            -fx-border-color: %s;
                            -fx-border-radius: 8;
                            -fx-background-radius: 8;
                            -fx-padding: 10 24;
                            -fx-cursor: hand;
                            """, COLOR_TEXT_SECONDARY, COLOR_BORDER));
                } else {
                    // Outros botões
                    button.setStyle(String.format("""
                            -fx-background-color: %s;
                            -fx-text-fill: %s;
                            -fx-font-weight: 500;
                            -fx-font-size: 13px;
                            -fx-background-radius: 8;
                            -fx-padding: 10 24;
                            -fx-cursor: hand;
                            """, COLOR_BG_SURFACE, COLOR_TEXT_PRIMARY));
                }
            }
        });
    }

    /**
     * Cria um GridPane estilizado para formulários em diálogos.
     */
    public static GridPane createStyledGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setPadding(new Insets(24));
        grid.setStyle(String.format("-fx-background-color: %s;", COLOR_BG_CARD));
        return grid;
    }

    /**
     * Cria uma Label estilizada para formulários.
     */
    public static Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle(String.format("""
                -fx-text-fill: %s;
                -fx-font-weight: 600;
                -fx-font-size: 13px;
                """, COLOR_TEXT_PRIMARY));
        return label;
    }

    /**
     * Cria uma Label secundária/muted.
     */
    public static Label createMutedLabel(String text) {
        Label label = new Label(text);
        label.setStyle(String.format("""
                -fx-text-fill: %s;
                -fx-font-size: 12px;
                """, COLOR_TEXT_MUTED));
        return label;
    }

    /**
     * Aplica estilo premium a um TextField.
     */
    public static void styleTextField(TextField field) {
        field.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-prompt-text-fill: %s;
                -fx-border-color: %s;
                -fx-border-width: 1;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 12 16;
                -fx-font-size: 13px;
                """, COLOR_BG_SURFACE, COLOR_TEXT_PRIMARY, COLOR_TEXT_MUTED, COLOR_BORDER));

        // Efeito hover/focus via CSS
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                field.setStyle(field.getStyle() + String.format("""
                        -fx-border-color: %s;
                        -fx-effect: dropshadow(gaussian, %s44, 8, 0.2, 0, 0);
                        """, COLOR_ACCENT, COLOR_ACCENT));
            } else {
                styleTextField(field);
            }
        });
    }

    /**
     * Aplica estilo premium a um TextArea.
     */
    public static void styleTextArea(TextArea area) {
        area.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-prompt-text-fill: %s;
                -fx-border-color: %s;
                -fx-border-width: 1;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 12;
                -fx-font-size: 13px;
                """, COLOR_BG_SURFACE, COLOR_TEXT_PRIMARY, COLOR_TEXT_MUTED, COLOR_BORDER));
    }

    /**
     * Aplica estilo premium a um PasswordField.
     */
    public static void stylePasswordField(PasswordField field) {
        styleTextField(field);
    }

    /**
     * Aplica estilo premium a um ComboBox.
     */
    public static <T> void styleComboBox(ComboBox<T> combo) {
        combo.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-border-color: %s;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """, COLOR_BG_SURFACE, COLOR_BORDER));
    }

    /**
     * Aplica estilo premium a um DatePicker.
     */
    public static void styleDatePicker(DatePicker picker) {
        picker.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-border-color: %s;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """, COLOR_BG_SURFACE, COLOR_BORDER));
    }

    /**
     * Aplica estilo premium a um Spinner.
     */
    public static <T> void styleSpinner(Spinner<T> spinner) {
        spinner.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-border-color: %s;
                -fx-border-radius: 6;
                """, COLOR_BG_SURFACE, COLOR_BORDER));
    }

    /**
     * Cria um título de seção estilizado.
     */
    public static Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle(String.format("""
                -fx-text-fill: %s;
                -fx-font-weight: 700;
                -fx-font-size: 18px;
                """, COLOR_TEXT_PRIMARY));
        return label;
    }

    /**
     * Cria um contentor VBox estilizado para conteúdo de diálogo.
     */
    public static VBox createStyledContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.setStyle(String.format("-fx-background-color: %s;", COLOR_BG_CARD));
        return content;
    }

    /**
     * Cria um separador estilizado.
     */
    public static Separator createStyledSeparator() {
        Separator sep = new Separator();
        sep.setStyle(String.format("-fx-background-color: %s;", COLOR_BORDER));
        return sep;
    }
}
