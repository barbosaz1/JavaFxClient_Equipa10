package gestaoeventos.client.util;

import gestaoeventos.dto.EventoCreateDTO;
import gestaoeventos.dto.LocalDTO;
import gestaoeventos.entity.TipoEvento;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Classe utilitária para criar diálogos modais de criação/edição de eventos.
 * 
 * Fornece um diálogo moderno e estilizado para recolher todos os dados
 * necessários para criar um novo evento no sistema.
 * 
 */
public class EventoDialogHelper {

    /**
     * Mostra um diálogo para criar um novo evento.
     */
    public static Optional<EventoCreateDTO> mostrarDialogoCriarEvento(
            List<LocalDTO> locais, Integer criadorNumero) {

        Dialog<EventoCreateDTO> dialog = new Dialog<>();
        dialog.setTitle("Criar Novo Evento");
        dialog.setHeaderText("Preencha os dados do evento");

        // Aplicar estilo ao diálogo
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
                EventoDialogHelper.class.getResource("/css/app-theme.css").toExternalForm());
        dialogPane.setStyle("-fx-background-color: #1E1E1E;");

        // Botões
        ButtonType btnCriar = new ButtonType("Criar Evento", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnCriar, ButtonType.CANCEL);

        // Estilizar botão Criar
        Button criarButton = (Button) dialogPane.lookupButton(btnCriar);
        criarButton.getStyleClass().add("btn-primary");

        TextField tfTitulo = new TextField();
        tfTitulo.setPromptText("Ex: Workshop de JavaFX");
        tfTitulo.setStyle("-fx-background-color: #2A2A2A; -fx-text-fill: white; -fx-prompt-text-fill: #6B7280;");

        TextArea taDescricao = new TextArea();
        taDescricao.setPromptText("Descrição detalhada do evento...");
        taDescricao.setPrefRowCount(3);
        taDescricao.setStyle("-fx-background-color: #2A2A2A; -fx-text-fill: white; -fx-prompt-text-fill: #6B7280;");

        DatePicker dpDataInicio = new DatePicker(LocalDate.now().plusDays(7));
        dpDataInicio.setStyle("-fx-background-color: #2A2A2A;");

        Spinner<Integer> spHoraInicio = new Spinner<>(0, 23, 9);
        spHoraInicio.setEditable(true);
        spHoraInicio.setPrefWidth(70);

        Spinner<Integer> spMinutoInicio = new Spinner<>(0, 59, 0, 15);
        spMinutoInicio.setEditable(true);
        spMinutoInicio.setPrefWidth(70);

        DatePicker dpDataFim = new DatePicker(LocalDate.now().plusDays(7));
        dpDataFim.setStyle("-fx-background-color: #2A2A2A;");

        Spinner<Integer> spHoraFim = new Spinner<>(0, 23, 17);
        spHoraFim.setEditable(true);
        spHoraFim.setPrefWidth(70);

        Spinner<Integer> spMinutoFim = new Spinner<>(0, 59, 0, 15);
        spMinutoFim.setEditable(true);
        spMinutoFim.setPrefWidth(70);

        Spinner<Integer> spMaxParticipantes = new Spinner<>(1, 1000, 50);
        spMaxParticipantes.setEditable(true);
        spMaxParticipantes.setPrefWidth(100);

        ComboBox<TipoEvento> cbTipo = new ComboBox<>(
                FXCollections.observableArrayList(TipoEvento.values()));
        cbTipo.setValue(TipoEvento.WORKSHOP);
        cbTipo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoEvento t) {
                return t != null ? formatTipo(t) : "";
            }

            @Override
            public TipoEvento fromString(String s) {
                return null;
            }
        });

        TextField tfAreaTematica = new TextField();
        tfAreaTematica.setPromptText("Ex: Programação, Web, IA...");
        tfAreaTematica.setStyle("-fx-background-color: #2A2A2A; -fx-text-fill: white; -fx-prompt-text-fill: #6B7280;");

        ComboBox<LocalDTO> cbLocal = new ComboBox<>(
                FXCollections.observableArrayList(locais));
        cbLocal.setPromptText("Selecione um local...");
        cbLocal.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDTO l) {
                return l != null ? l.getNome() + " (Cap: " + l.getCapacidade() + ")" : "";
            }

            @Override
            public LocalDTO fromString(String s) {
                return null;
            }
        });

        // Layout do formulário
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        // Labels
        String labelStyle = "-fx-text-fill: #E0E0E0; -fx-font-weight: bold;";

        int row = 0;
        grid.add(createLabel("Título *", labelStyle), 0, row);
        grid.add(tfTitulo, 1, row++);

        grid.add(createLabel("Descrição", labelStyle), 0, row);
        grid.add(taDescricao, 1, row++);

        grid.add(createLabel("Data e Hora de Início *", labelStyle), 0, row);
        grid.add(createDateTimeBox(dpDataInicio, spHoraInicio, spMinutoInicio), 1, row++);

        grid.add(createLabel("Data e Hora de Fim *", labelStyle), 0, row);
        grid.add(createDateTimeBox(dpDataFim, spHoraFim, spMinutoFim), 1, row++);

        grid.add(createLabel("Máximo de Participantes", labelStyle), 0, row);
        grid.add(spMaxParticipantes, 1, row++);

        grid.add(createLabel("Tipo de Evento *", labelStyle), 0, row);
        grid.add(cbTipo, 1, row++);

        grid.add(createLabel("Área Temática", labelStyle), 0, row);
        grid.add(tfAreaTematica, 1, row++);

        grid.add(createLabel("Local *", labelStyle), 0, row);
        grid.add(cbLocal, 1, row++);

        // Scroll
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1E1E1E; -fx-background-color: #1E1E1E;");
        scrollPane.setPrefHeight(450);

        dialogPane.setContent(scrollPane);

        // Validação e conversão do resultado
        dialog.setResultConverter(buttonType -> {
            if (buttonType == btnCriar) {
                // Validação 
                if (tfTitulo.getText().isBlank()) {
                    return null;
                }
                if (dpDataInicio.getValue() == null || dpDataFim.getValue() == null) {
                    return null;
                }
                if (cbLocal.getValue() == null) {
                    return null;
                }

                EventoCreateDTO dto = new EventoCreateDTO();
                dto.setTitulo(tfTitulo.getText().trim());
                dto.setDescricao(taDescricao.getText().trim());

                LocalDateTime inicio = LocalDateTime.of(
                        dpDataInicio.getValue(),
                        LocalTime.of(spHoraInicio.getValue(), spMinutoInicio.getValue()));
                LocalDateTime fim = LocalDateTime.of(
                        dpDataFim.getValue(),
                        LocalTime.of(spHoraFim.getValue(), spMinutoFim.getValue()));

                dto.setDataInicio(inicio);
                dto.setDataFim(fim);
                dto.setMaxParticipantes(spMaxParticipantes.getValue());
                dto.setTipo(cbTipo.getValue());
                dto.setAreaTematica(tfAreaTematica.getText().trim());
                dto.setLocalId(cbLocal.getValue().getId());
                dto.setCriadorNumero(criadorNumero);

                return dto;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Cria um Label
     */
    private static Label createLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    /**
     * Cria um HBox com DatePicker e Spinners
     */
    private static javafx.scene.layout.HBox createDateTimeBox(
            DatePicker dp, Spinner<Integer> hora, Spinner<Integer> minuto) {

        Label separador = new Label(":");
        separador.setStyle("-fx-text-fill: #E0E0E0;");

        javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(8);
        box.getChildren().addAll(dp, hora, separador, minuto);
        return box;
    }

    /**
     * Formata o tipo de evento
     */
    private static String formatTipo(TipoEvento tipo) {
        return switch (tipo) {
            case PALESTRA -> "📢 Palestra";
            case WORKSHOP -> "🔧 Workshop";
            case SEMINARIO -> "📚 Seminário";
            case OUTRO -> "📋 Outro";
        };
    }
}
