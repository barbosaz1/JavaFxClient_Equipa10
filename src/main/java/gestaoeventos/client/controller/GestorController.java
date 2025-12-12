package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.*;
import gestaoeventos.client.util.EventoDialogHelper;
import gestaoeventos.client.util.ToastNotification;
import gestaoeventos.dto.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

import java.net.URL;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador do painel do Gestor de Eventos.
 */
public class GestorController implements Initializable {

    private final LocalClientService localService = new LocalClientService();
    private final EventoService eventoService = new EventoService();
    private final NotificacaoClientService notificacaoService = new NotificacaoClientService();
    private final InscricaoService inscricaoService = new InscricaoService();

    @FXML
    private TableView<LocalDTO> tblLocais;
    @FXML
    private TableColumn<LocalDTO, String> colLocalNome;
    @FXML
    private TableColumn<LocalDTO, Integer> colLocalCapacidade;
    @FXML
    private TableColumn<LocalDTO, String> colLocalMorada;

    @FXML
    private ComboBox<EventoDTO> cmbEventos;
    @FXML
    private GridPane gridEstatisticas;
    @FXML
    private Label lblTotalInscricoes, lblInscricoesAtivas, lblCheckIns, lblVagas, lblOcupacao;

    @FXML
    private TextArea txtAnuncio;
    @FXML
    private Label lblResultadoAnuncio;
    @FXML
    private DatePicker dpDataInicio;
    @FXML
    private DatePicker dpDataFim;
    @FXML
    private Spinner<Integer> spHoraInicio;
    @FXML
    private Spinner<Integer> spMinutoInicio;
    @FXML
    private Spinner<Integer> spHoraFim;
    @FXML
    private Spinner<Integer> spMinutoFim;

    @FXML
    private TableView<EventoDTO> tblEventos;
    @FXML
    private TableColumn<EventoDTO, String> colEventoTitulo;
    @FXML
    private TableColumn<EventoDTO, String> colEventoData;
    @FXML
    private TableColumn<EventoDTO, String> colEventoEstado;
    @FXML
    private TableColumn<EventoDTO, Void> colEventoAcoes;

    @FXML
    private TextField txtTokenCheckin;
    @FXML
    private Button btnValidarCheckin;
    @FXML
    private Label lblResultadoCheckin;

    @FXML
    private TableView<NotificacaoDTO> tblAnuncios;
    @FXML
    private TableColumn<NotificacaoDTO, String> colAnuncioConteudo;
    @FXML
    private TableColumn<NotificacaoDTO, String> colAnuncioInicio;
    @FXML
    private TableColumn<NotificacaoDTO, String> colAnuncioFim;
    @FXML
    private TableColumn<NotificacaoDTO, Void> colAnuncioAcoes;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupLocaisTable();
        setupEventosTable();
        setupAnunciosTable();
        setupSpinners();
        carregarLocais();
        carregarEventos();
        carregarComboEventos();
        carregarAnuncios();
    }

    private void setupSpinners() {
        if (spHoraInicio != null) {
            spHoraInicio.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        }
        if (spMinutoInicio != null) {
            spMinutoInicio.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15));
        }
        if (spHoraFim != null) {
            spHoraFim.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 23));
        }
        if (spMinutoFim != null) {
            spMinutoFim.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 59, 15));
        }
    }

    private void setupLocaisTable() {
        colLocalNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colLocalCapacidade.setCellValueFactory(new PropertyValueFactory<>("capacidade"));
        colLocalMorada.setCellValueFactory(new PropertyValueFactory<>("morada"));
    }

    private void setupEventosTable() {
        colEventoTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colEventoData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataInicio() != null ? c.getValue().getDataInicio().format(DTF) : ""));
        colEventoEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() != null ? c.getValue().getEstado().toString() : ""));

        if (colEventoAcoes != null) {
            colEventoAcoes.setCellFactory(col -> new TableCell<>() {
                private final Button btnEditar = new Button("✏️");
                private final Button btnApagar = new Button("🗑️");
                private final MenuButton btnEstado = new MenuButton("📋");
                private final HBox hbox = new HBox(5, btnEditar, btnEstado, btnApagar);

                {
                    btnEditar.getStyleClass().add("btn-icon");
                    btnApagar.getStyleClass().add("btn-icon");
                    btnEstado.getStyleClass().add("btn-icon");
                    btnApagar.setStyle("-fx-text-fill: #ef4444;");
                    btnEstado.setTooltip(new Tooltip("Alterar estado"));

                    MenuItem itemRascunho = new MenuItem("📝 Rascunho");
                    MenuItem itemPublicar = new MenuItem("🟢 Publicar");
                    MenuItem itemCancelar = new MenuItem("🔴 Cancelar");
                    MenuItem itemConcluir = new MenuItem("✅ Concluir");

                    itemRascunho.setOnAction(e -> alterarEstadoEvento(getTableView().getItems().get(getIndex()),
                            gestaoeventos.entity.EstadoEvento.RASCUNHO));
                    itemPublicar.setOnAction(e -> alterarEstadoEvento(getTableView().getItems().get(getIndex()),
                            gestaoeventos.entity.EstadoEvento.PUBLICADO));
                    itemCancelar.setOnAction(e -> alterarEstadoEvento(getTableView().getItems().get(getIndex()),
                            gestaoeventos.entity.EstadoEvento.CANCELADO));
                    itemConcluir.setOnAction(e -> alterarEstadoEvento(getTableView().getItems().get(getIndex()),
                            gestaoeventos.entity.EstadoEvento.CONCLUIDO));

                    btnEstado.getItems().addAll(itemRascunho, itemPublicar, itemCancelar, itemConcluir);

                    btnEditar.setOnAction(e -> editarEvento(getTableView().getItems().get(getIndex())));
                    btnApagar.setOnAction(e -> apagarEvento(getTableView().getItems().get(getIndex())));
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : hbox);
                }
            });
        }
    }

    private void setupAnunciosTable() {
        if (colAnuncioConteudo != null) {
            colAnuncioConteudo.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getConteudo() != null ? (c.getValue().getConteudo().length() > 50
                            ? c.getValue().getConteudo().substring(0, 50) + "..."
                            : c.getValue().getConteudo()) : ""));
        }
        if (colAnuncioInicio != null) {
            colAnuncioInicio.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getDataInicioExibicao() != null ? c.getValue().getDataInicioExibicao().format(DTF)
                            : "Imediato"));
        }
        if (colAnuncioFim != null) {
            colAnuncioFim.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getDataFimExibicao() != null ? c.getValue().getDataFimExibicao().format(DTF)
                            : "Indefinido"));
        }

        if (colAnuncioAcoes != null) {
            colAnuncioAcoes.setCellFactory(col -> new TableCell<>() {
                private final Button btnEditar = new Button("Editar");
                private final Button btnApagar = new Button("Apagar");
                private final HBox hbox = new HBox(5, btnEditar, btnApagar);

                {
                    btnEditar.getStyleClass().add("btn-secondary");
                    btnEditar.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                    btnApagar.getStyleClass().add("btn-secondary");
                    btnApagar.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-text-fill: #ef4444;");

                    btnEditar.setOnAction(e -> editarAnuncio(getTableView().getItems().get(getIndex())));
                    btnApagar.setOnAction(e -> apagarAnuncio(getTableView().getItems().get(getIndex())));
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : hbox);
                }
            });
        }
    }

    @FXML
    public void carregarAnuncios() {
        if (tblAnuncios == null)
            return;
        try {
            List<NotificacaoDTO> anuncios = notificacaoService.listarTodosAnuncios();
            tblAnuncios.setItems(FXCollections.observableArrayList(anuncios));
        } catch (Exception e) {
            mostrarErro("Erro ao carregar anuncios: " + e.getMessage());
        }
    }

    private void editarAnuncio(NotificacaoDTO anuncio) {
        Dialog<NotificacaoDTO> dialog = new Dialog<>();
        dialog.setTitle("Editar Anuncio");
        dialog.setHeaderText("Editar conteudo e periodo de exibicao");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20;");

        TextArea txtConteudo = new TextArea(anuncio.getConteudo());
        txtConteudo.setPrefRowCount(3);
        txtConteudo.setPrefWidth(400);

        DatePicker dpInicio = new DatePicker();
        if (anuncio.getDataInicioExibicao() != null) {
            dpInicio.setValue(anuncio.getDataInicioExibicao().toLocalDate());
        }

        DatePicker dpFim = new DatePicker();
        if (anuncio.getDataFimExibicao() != null) {
            dpFim.setValue(anuncio.getDataFimExibicao().toLocalDate());
        }

        Spinner<Integer> spHora1 = new Spinner<>(0, 23,
                anuncio.getDataInicioExibicao() != null ? anuncio.getDataInicioExibicao().getHour() : 0);
        Spinner<Integer> spMin1 = new Spinner<>(0, 59,
                anuncio.getDataInicioExibicao() != null ? anuncio.getDataInicioExibicao().getMinute() : 0);
        Spinner<Integer> spHora2 = new Spinner<>(0, 23,
                anuncio.getDataFimExibicao() != null ? anuncio.getDataFimExibicao().getHour() : 23);
        Spinner<Integer> spMin2 = new Spinner<>(0, 59,
                anuncio.getDataFimExibicao() != null ? anuncio.getDataFimExibicao().getMinute() : 59);

        spHora1.setPrefWidth(60);
        spMin1.setPrefWidth(60);
        spHora2.setPrefWidth(60);
        spMin2.setPrefWidth(60);

        grid.add(new Label("Conteudo:"), 0, 0);
        grid.add(txtConteudo, 1, 0, 3, 1);
        grid.add(new Label("Data Inicio:"), 0, 1);
        grid.add(dpInicio, 1, 1);
        grid.add(spHora1, 2, 1);
        grid.add(spMin1, 3, 1);
        grid.add(new Label("Data Fim:"), 0, 2);
        grid.add(dpFim, 1, 2);
        grid.add(spHora2, 2, 2);
        grid.add(spMin2, 3, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == btnGuardar) {
                String conteudo = txtConteudo.getText();
                if (conteudo == null || conteudo.isBlank()) {
                    return null;
                }

                LocalDateTime dataInicio = null;
                LocalDateTime dataFim = null;

                if (dpInicio.getValue() != null) {
                    dataInicio = LocalDateTime.of(dpInicio.getValue(),
                            LocalTime.of(spHora1.getValue(), spMin1.getValue()));
                }
                if (dpFim.getValue() != null) {
                    dataFim = LocalDateTime.of(dpFim.getValue(),
                            LocalTime.of(spHora2.getValue(), spMin2.getValue()));
                }

                NotificacaoDTO dto = notificacaoService.atualizarAnuncio(
                        anuncio.getId(), conteudo, dataInicio, dataFim);
                return dto;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(dto -> {
            if (dto != null) {
                mostrarSucesso("Anuncio atualizado com sucesso!");
                carregarAnuncios();
            } else {
                mostrarErro("Falha ao atualizar o anuncio.");
            }
        });
    }

    private void apagarAnuncio(NotificacaoDTO anuncio) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminacao");
        confirm.setHeaderText("Apagar anuncio");
        confirm.setContentText("Tem a certeza que pretende apagar este anuncio?\n\n" +
                (anuncio.getConteudo().length() > 100 ? anuncio.getConteudo().substring(0, 100) + "..."
                        : anuncio.getConteudo()));

        confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            if (notificacaoService.apagarAnuncio(anuncio.getId())) {
                mostrarSucesso("Anuncio eliminado com sucesso!");
                carregarAnuncios();
            } else {
                mostrarErro("Falha ao eliminar o anuncio.");
            }
        });
    }

    @FXML
    public void carregarLocais() {
        try {
            List<LocalDTO> locais = localService.listarTodos();
            tblLocais.setItems(FXCollections.observableArrayList(locais));
        } catch (Exception e) {
            mostrarErro("Erro ao carregar locais: " + e.getMessage());
        }
    }

    @FXML
    public void novoLocal() {
        mostrarInfo("Para criar novos locais, utilize o painel Admin.");
    }

    @FXML
    public void carregarEventos() {
        if (!UserSession.getInstance().isLoggedIn())
            return;
        try {
            Integer numero = UserSession.getInstance().getUser().getNumero();
            List<EventoDTO> eventos = eventoService.listarPorOrganizador(numero);
            tblEventos.setItems(FXCollections.observableArrayList(eventos));
        } catch (Exception e) {
            mostrarErro("Erro ao carregar eventos: " + e.getMessage());
        }
    }

    private void carregarComboEventos() {
        try {
            List<EventoDTO> eventos = eventoService.listarTodos();
            cmbEventos.setItems(FXCollections.observableArrayList(eventos));
            cmbEventos.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(EventoDTO e) {
                    return e != null ? e.getTitulo() : "";
                }

                @Override
                public EventoDTO fromString(String s) {
                    return null;
                }
            });
        } catch (Exception e) {
            mostrarErro("Erro ao carregar lista de eventos: " + e.getMessage());
        }
    }

    @FXML
    public void criarEvento() {
        if (!UserSession.getInstance().isLoggedIn()) {
            mostrarErro("Deve estar autenticado para criar eventos.");
            return;
        }

        try {
            List<LocalDTO> locais = localService.listarTodos();
            if (locais.isEmpty()) {
                mostrarAviso("Não existem locais disponíveis. Contacte o administrador.");
                return;
            }

            Integer criadorNumero = UserSession.getInstance().getUser().getNumero();
            Optional<EventoCreateDTO> resultado = EventoDialogHelper.mostrarDialogoCriarEvento(locais, criadorNumero);

            resultado.ifPresent(dto -> {
                EventoDTO eventoCriado = eventoService.criar(dto);
                if (eventoCriado != null) {
                    mostrarSucesso("Evento '" + eventoCriado.getTitulo() + "' criado com sucesso!");
                    carregarEventos();
                    carregarComboEventos();
                } else {
                    mostrarErro("Falha ao criar o evento. Verifique os dados e tente novamente.");
                }
            });
        } catch (Exception e) {
            mostrarErro("Erro ao criar evento: " + e.getMessage());
        }
    }

    private void editarEvento(EventoDTO evento) {
        if (!UserSession.getInstance().isLoggedIn())
            return;

        try {
            List<LocalDTO> locais = localService.listarTodos();
            Integer gestorNumero = UserSession.getInstance().getUser().getNumero();

            Optional<EventoCreateDTO> resultado = EventoDialogHelper.mostrarDialogoEditarEvento(
                    evento, locais, gestorNumero);

            resultado.ifPresent(dto -> {
                EventoDTO atualizado = eventoService.atualizar(evento.getId(), dto);
                if (atualizado != null) {
                    mostrarSucesso("Evento '" + atualizado.getTitulo() + "' atualizado com sucesso!");
                    carregarEventos();
                    carregarComboEventos();
                } else {
                    mostrarErro("Falha ao atualizar o evento.");
                }
            });
        } catch (Exception e) {
            mostrarErro("Erro ao editar evento: " + e.getMessage());
        }
    }

    private void alterarEstadoEvento(EventoDTO evento, gestaoeventos.entity.EstadoEvento novoEstado) {
        if (evento.getEstado() == novoEstado) {
            mostrarInfo("O evento já está no estado " + novoEstado + ".");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Alteração de Estado");
        confirm.setHeaderText("Alterar estado do evento: " + evento.getTitulo());
        confirm.setContentText("O estado atual é " + evento.getEstado() + ". Deseja alterar para " + novoEstado + "?");

        confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            try {
                EventoCreateDTO dto = new EventoCreateDTO();
                dto.setTitulo(evento.getTitulo());
                dto.setDescricao(evento.getDescricao());
                dto.setDataInicio(evento.getDataInicio());
                dto.setDataFim(evento.getDataFim());
                dto.setTipo(evento.getTipo());
                dto.setMaxParticipantes(evento.getMaxParticipantes());
                dto.setLocalId(evento.getLocalId());
                dto.setCriadorNumero(evento.getCriadorNumero());
                dto.setEstado(novoEstado);

                EventoDTO atualizado = eventoService.atualizar(evento.getId(), dto);
                if (atualizado != null) {
                    mostrarSucesso("Estado alterado para " + novoEstado + " com sucesso!");
                    carregarEventos();
                    carregarComboEventos();
                } else {
                    mostrarErro("Falha ao alterar o estado do evento.");
                }
            } catch (Exception e) {
                mostrarErro("Erro ao alterar estado: " + e.getMessage());
            }
        });
    }

    private void apagarEvento(EventoDTO evento) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminação");
        confirm.setHeaderText("Apagar evento: " + evento.getTitulo());
        confirm.setContentText("Esta ação não pode ser revertida. Tem a certeza?");

        confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            if (eventoService.apagar(evento.getId())) {
                mostrarSucesso("Evento eliminado com sucesso!");
                carregarEventos();
                carregarComboEventos();
            } else {
                mostrarErro("Falha ao eliminar o evento.");
            }
        });
    }

    @FXML
    public void verEstatisticas() {
        EventoDTO selected = cmbEventos.getValue();
        if (selected == null) {
            mostrarAviso("Selecione um evento.");
            return;
        }

        try {
            EstatisticasEventoDTO stats = eventoService.obterEstatisticas(selected.getId());
            if (stats != null) {
                gridEstatisticas.setVisible(true);
                lblTotalInscricoes.setText(String.valueOf(stats.getTotalInscricoes()));
                lblInscricoesAtivas.setText(String.valueOf(stats.getInscricoesAtivas()));
                lblCheckIns.setText(String.valueOf(stats.getCheckInsRealizados()));
                lblVagas.setText(
                        stats.getVagasDisponiveis() >= 0 ? String.valueOf(stats.getVagasDisponiveis()) : "Ilimitado");
                lblOcupacao.setText(String.format("%.1f%%", stats.getPercentualOcupacao()));
                mostrarSucesso("Estatísticas carregadas com sucesso!");
            } else {
                mostrarErro("Não foi possível obter estatísticas.");
            }
        } catch (Exception e) {
            mostrarErro("Erro ao obter estatísticas: " + e.getMessage());
        }
    }

    @FXML
    public void enviarAnuncio() {
        String conteudo = txtAnuncio.getText();
        if (conteudo == null || conteudo.isBlank()) {
            mostrarAviso("Escreva o conteúdo do anúncio.");
            return;
        }

        if (!UserSession.getInstance().isLoggedIn())
            return;

        try {
            Integer autorNumero = UserSession.getInstance().getUser().getNumero();

            LocalDateTime dataInicio = null;
            LocalDateTime dataFim = null;

            if (dpDataInicio != null && dpDataInicio.getValue() != null) {
                int hora = spHoraInicio != null ? spHoraInicio.getValue() : 0;
                int minuto = spMinutoInicio != null ? spMinutoInicio.getValue() : 0;
                dataInicio = LocalDateTime.of(dpDataInicio.getValue(), LocalTime.of(hora, minuto));
            }

            if (dpDataFim != null && dpDataFim.getValue() != null) {
                int hora = spHoraFim != null ? spHoraFim.getValue() : 23;
                int minuto = spMinutoFim != null ? spMinutoFim.getValue() : 59;
                dataFim = LocalDateTime.of(dpDataFim.getValue(), LocalTime.of(hora, minuto));
            }

            String resultado = notificacaoService.enviarAnuncioBroadcast(conteudo, autorNumero, dataInicio, dataFim);
            lblResultadoAnuncio.setText(resultado);
            txtAnuncio.clear();

            if (dpDataInicio != null)
                dpDataInicio.setValue(null);
            if (dpDataFim != null)
                dpDataFim.setValue(null);

            mostrarSucesso("Anúncio enviado com sucesso!");
        } catch (Exception e) {
            mostrarErro("Erro ao enviar anúncio: " + e.getMessage());
        }
    }

    @FXML
    void handleValidarCheckin() {
        if (txtTokenCheckin == null)
            return;

        String token = txtTokenCheckin.getText().trim();
        if (token.isEmpty()) {
            lblResultadoCheckin.setText("Por favor introduza o código do QR.");
            lblResultadoCheckin.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        btnValidarCheckin.setDisable(true);
        lblResultadoCheckin.setText("A validar...");
        lblResultadoCheckin.setStyle("-fx-text-fill: #9CA3AF;");

        Task<InscricaoDTO> task = new Task<>() {
            @Override
            protected InscricaoDTO call() throws Exception {
                return inscricaoService.checkinPorQrCode(token);
            }
        };

        task.setOnSucceeded(e -> {
            InscricaoDTO dto = task.getValue();
            if (dto != null) {
                lblResultadoCheckin.setText("✅ Check-in confirmado com sucesso!\nInscrição #" + dto.getId());
                lblResultadoCheckin.setStyle("-fx-text-fill: #22c55e;");
                txtTokenCheckin.clear();
                mostrarSucesso("Check-in validado com sucesso!");
            } else {
                lblResultadoCheckin.setText("❌ Token inválido ou já utilizado.");
                lblResultadoCheckin.setStyle("-fx-text-fill: #ef4444;");
            }
            btnValidarCheckin.setDisable(false);
        });

        task.setOnFailed(e -> {
            lblResultadoCheckin.setText("❌ Erro: " + task.getException().getMessage());
            lblResultadoCheckin.setStyle("-fx-text-fill: #ef4444;");
            btnValidarCheckin.setDisable(false);
        });

        new Thread(task).start();
    }

    private Window getWindow() {
        return tblLocais.getScene() != null ? tblLocais.getScene().getWindow() : null;
    }

    private void mostrarSucesso(String mensagem) {
        Window window = getWindow();
        if (window != null)
            ToastNotification.sucesso(window, mensagem);
    }

    private void mostrarErro(String mensagem) {
        Window window = getWindow();
        if (window != null)
            ToastNotification.erro(window, mensagem);
    }

    private void mostrarAviso(String mensagem) {
        Window window = getWindow();
        if (window != null)
            ToastNotification.aviso(window, mensagem);
    }

    private void mostrarInfo(String mensagem) {
        Window window = getWindow();
        if (window != null)
            ToastNotification.info(window, mensagem);
    }
}
