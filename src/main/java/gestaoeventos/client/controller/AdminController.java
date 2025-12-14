package gestaoeventos.client.controller;

import gestaoeventos.client.service.*;
import gestaoeventos.client.util.ToastNotification;
import gestaoeventos.dto.*;
import gestaoeventos.entity.PerfilUtilizador;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    private final UtilizadorClientService utilizadorService = new UtilizadorClientService();
    private final EventoService eventoService = new EventoService();
    private final LocalClientService localService = new LocalClientService();
    private final InscricaoService inscricaoService = new InscricaoService();
    private final LogAuditoriaClientService logService = new LogAuditoriaClientService();
    private final CertificadoClientService certificadoService = new CertificadoClientService();

    @FXML
    private TableView<UtilizadorDTO> tblUtilizadores;
    @FXML
    private TableColumn<UtilizadorDTO, Integer> colNumero;
    @FXML
    private TableColumn<UtilizadorDTO, String> colNome;
    @FXML
    private TableColumn<UtilizadorDTO, String> colEmail;
    @FXML
    private TableColumn<UtilizadorDTO, String> colPerfil;
    @FXML
    private TableColumn<UtilizadorDTO, String> colAtivo;
    @FXML
    private TableColumn<UtilizadorDTO, Void> colAcoesUtilizador;
    @FXML
    private TextField txtPesquisaUtilizador;

    @FXML
    private TableView<EventoDTO> tblEventos;
    @FXML
    private TableColumn<EventoDTO, Integer> colEventoId;
    @FXML
    private TableColumn<EventoDTO, String> colEventoTitulo;
    @FXML
    private TableColumn<EventoDTO, String> colEventoData;
    @FXML
    private TableColumn<EventoDTO, String> colEventoEstado;
    @FXML
    private TableColumn<EventoDTO, String> colEventoLocal;
    @FXML
    private TableColumn<EventoDTO, Void> colAcoesEvento;

    @FXML
    private TableView<LocalDTO> tblLocais;
    @FXML
    private TableColumn<LocalDTO, Integer> colLocalId;
    @FXML
    private TableColumn<LocalDTO, String> colLocalNome;
    @FXML
    private TableColumn<LocalDTO, Integer> colLocalCapacidade;
    @FXML
    private TableColumn<LocalDTO, String> colLocalMorada;
    @FXML
    private TableColumn<LocalDTO, Void> colAcoesLocal;

    @FXML
    private TableView<InscricaoDTO> tblInscricoes;
    @FXML
    private TableColumn<InscricaoDTO, Integer> colInscId;
    @FXML
    private TableColumn<InscricaoDTO, String> colInscEvento;
    @FXML
    private TableColumn<InscricaoDTO, String> colInscUtilizador;
    @FXML
    private TableColumn<InscricaoDTO, String> colInscData;
    @FXML
    private TableColumn<InscricaoDTO, String> colInscEstado;
    @FXML
    private TableColumn<InscricaoDTO, String> colInscCheckIn;
    @FXML
    private TableColumn<InscricaoDTO, Void> colAcoesInscricao;
    @FXML
    private ComboBox<String> cmbFiltroEvento;

    @FXML
    private TableView<LogAuditoriaDTO> tblLogs;
    @FXML
    private TableColumn<LogAuditoriaDTO, Integer> colLogId;
    @FXML
    private TableColumn<LogAuditoriaDTO, String> colLogAcao;
    @FXML
    private TableColumn<LogAuditoriaDTO, String> colLogEntidade;
    @FXML
    private TableColumn<LogAuditoriaDTO, Integer> colLogEntidadeId;
    @FXML
    private TableColumn<LogAuditoriaDTO, String> colLogAutor;
    @FXML
    private TableColumn<LogAuditoriaDTO, String> colLogData;
    @FXML
    private TableColumn<LogAuditoriaDTO, String> colLogMotivo;
    @FXML
    private DatePicker dpDataInicio;
    @FXML
    private DatePicker dpDataFim;

    @FXML
    private TextField txtTokenCheckin;
    @FXML
    private Button btnValidarCheckin;
    @FXML
    private Label lblResultadoCheckin;

    // Certificados
    @FXML
    private ComboBox<EventoDTO> cmbEventosCertAdmin;
    @FXML
    private Label lblResultadoCertificadosAdmin;
    @FXML
    private TableView<CertificadoDTO> tblCertificadosAdmin;
    @FXML
    private TableColumn<CertificadoDTO, String> colCertAdminUtilizador;
    @FXML
    private TableColumn<CertificadoDTO, String> colCertAdminEvento;
    @FXML
    private TableColumn<CertificadoDTO, String> colCertAdminTipo;
    @FXML
    private TableColumn<CertificadoDTO, String> colCertAdminData;
    @FXML
    private TableColumn<CertificadoDTO, String> colCertAdminCodigo;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUtilizadoresTable();
        setupEventosTable();
        setupLocaisTable();
        setupInscricoesTable();
        setupLogsTable();
        setupCertificadosTable();
        carregarUtilizadores();
    }

    private void setupUtilizadoresTable() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPerfil.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPerfil() != null ? c.getValue().getPerfil().toString() : ""));
        colAtivo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isAtivo() ? "✓ Ativo" : "✗ Inativo"));

        colAcoesUtilizador.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnToggle = new Button("🔄");
            private final HBox hbox = new HBox(5, btnEdit, btnToggle);

            {
                btnEdit.setTooltip(new Tooltip("Editar utilizador"));
                btnToggle.setTooltip(new Tooltip("Ativar/Desativar"));
                btnEdit.getStyleClass().add("btn-icon");
                btnToggle.getStyleClass().add("btn-icon");

                btnEdit.setOnAction(e -> editarUtilizador(getTableView().getItems().get(getIndex())));
                btnToggle.setOnAction(e -> toggleAtivo(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    @FXML
    public void carregarUtilizadores() {
        try {
            List<UtilizadorDTO> utilizadores = utilizadorService.listarTodos();
            tblUtilizadores.setItems(FXCollections.observableArrayList(utilizadores));
        } catch (Exception e) {
            mostrarErro("Erro ao carregar utilizadores: " + e.getMessage());
        }
    }

    @FXML
    public void novoUtilizador() {
        Dialog<UtilizadorCreateDTO> dialog = createUtilizadorDialog(null);
        Optional<UtilizadorCreateDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            try {
                UtilizadorDTO criado = utilizadorService.criar(dto);
                if (criado != null) {
                    mostrarSucesso("Utilizador '" + criado.getNome() + "' criado com sucesso!");
                    carregarUtilizadores();
                } else {
                    mostrarErro("Falha ao criar utilizador.");
                }
            } catch (Exception e) {
                mostrarErro("Erro ao criar utilizador: " + e.getMessage());
            }
        });
    }

    private void editarUtilizador(UtilizadorDTO user) {
        Dialog<UtilizadorCreateDTO> dialog = createUtilizadorDialog(user);
        Optional<UtilizadorCreateDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            try {
                UtilizadorDTO atualizado = utilizadorService.atualizar(user.getNumero(), dto);
                if (atualizado != null) {
                    mostrarSucesso("Utilizador atualizado com sucesso!");
                    carregarUtilizadores();
                } else {
                    mostrarErro("Falha ao atualizar utilizador.");
                }
            } catch (Exception e) {
                mostrarErro("Erro ao atualizar utilizador: " + e.getMessage());
            }
        });
    }

    private void toggleAtivo(UtilizadorDTO user) {
        try {
            String acao = user.isAtivo() ? "desativar" : "ativar";
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Ação");
            confirm.setHeaderText(user.isAtivo() ? "Desativar utilizador?" : "Ativar utilizador?");
            confirm.setContentText("Utilizador: " + user.getNome() + " (#" + user.getNumero() + ")");

            confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
                try {
                    UtilizadorDTO resultado;
                    if (user.isAtivo()) {
                        resultado = utilizadorService.desativar(user.getNumero());
                    } else {
                        resultado = utilizadorService.ativar(user.getNumero());
                    }

                    if (resultado != null) {
                        String estado = resultado.isAtivo() ? "ativado" : "desativado";
                        mostrarSucesso("Utilizador " + estado + " com sucesso!");
                        carregarUtilizadores();
                    } else {
                        mostrarErro("Falha ao " + acao + " utilizador.");
                    }
                } catch (Exception ex) {
                    mostrarErro("Erro ao " + acao + " utilizador: " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            mostrarErro("Erro: " + e.getMessage());
        }
    }

    private Dialog<UtilizadorCreateDTO> createUtilizadorDialog(UtilizadorDTO existing) {
        Dialog<UtilizadorCreateDTO> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Novo Utilizador" : "Editar Utilizador");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1E1E1E;");

        TextField tfNumero = new TextField();
        tfNumero.setPromptText("Número do utilizador");
        TextField tfNome = new TextField();
        tfNome.setPromptText("Nome completo");
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("email@exemplo.com");
        PasswordField tfPassword = new PasswordField();
        tfPassword.setPromptText("Password");
        ComboBox<PerfilUtilizador> cbPerfil = new ComboBox<>(
                FXCollections.observableArrayList(PerfilUtilizador.values()));

        if (existing != null) {
            tfNumero.setText(String.valueOf(existing.getNumero()));
            tfNumero.setDisable(true);
            tfNome.setText(existing.getNome());
            tfEmail.setText(existing.getEmail());
            cbPerfil.setValue(existing.getPerfil());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        String labelStyle = "-fx-text-fill: #E0E0E0; -fx-font-weight: bold;";
        String inputStyle = "-fx-background-color: #2A2A2A; -fx-text-fill: white; -fx-prompt-text-fill: #6B7280;";

        tfNumero.setStyle(inputStyle);
        tfNome.setStyle(inputStyle);
        tfEmail.setStyle(inputStyle);
        tfPassword.setStyle(inputStyle);

        Label lblNumero = new Label("Número:");
        lblNumero.setStyle(labelStyle);
        Label lblNome = new Label("Nome:");
        lblNome.setStyle(labelStyle);
        Label lblEmail = new Label("Email:");
        lblEmail.setStyle(labelStyle);
        Label lblPassword = new Label("Password:");
        lblPassword.setStyle(labelStyle);
        Label lblPerfil = new Label("Perfil:");
        lblPerfil.setStyle(labelStyle);

        grid.add(lblNumero, 0, 0);
        grid.add(tfNumero, 1, 0);
        grid.add(lblNome, 0, 1);
        grid.add(tfNome, 1, 1);
        grid.add(lblEmail, 0, 2);
        grid.add(tfEmail, 1, 2);
        grid.add(lblPassword, 0, 3);
        grid.add(tfPassword, 1, 3);
        grid.add(lblPerfil, 0, 4);
        grid.add(cbPerfil, 1, 4);

        dialogPane.setContent(grid);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    UtilizadorCreateDTO dto = new UtilizadorCreateDTO();
                    dto.setNumero(Integer.parseInt(tfNumero.getText()));
                    dto.setNome(tfNome.getText());
                    dto.setEmail(tfEmail.getText());
                    dto.setPassword(tfPassword.getText());
                    dto.setPerfil(cbPerfil.getValue());
                    return dto;
                } catch (NumberFormatException e) {
                    mostrarErro("Número inválido.");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private void setupEventosTable() {
        colEventoId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEventoTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colEventoData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataInicio() != null ? c.getValue().getDataInicio().format(DTF) : ""));
        colEventoEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() != null ? c.getValue().getEstado().toString() : ""));
        colEventoLocal.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getLocalId() != null ? "Local #" + c.getValue().getLocalId() : ""));
    }

    @FXML
    public void carregarEventos() {
        try {
            List<EventoDTO> eventos = eventoService.listarTodos();
            tblEventos.setItems(FXCollections.observableArrayList(eventos));
        } catch (Exception e) {
            mostrarErro("Erro ao carregar eventos: " + e.getMessage());
        }
    }

    @FXML
    public void novoEvento() {
        mostrarInfo("Para criar eventos, utilize o Painel Docente ou Painel Gestor.");
    }

    private void setupLocaisTable() {
        colLocalId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLocalNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colLocalCapacidade.setCellValueFactory(new PropertyValueFactory<>("capacidade"));
        colLocalMorada.setCellValueFactory(new PropertyValueFactory<>("morada"));

        colAcoesLocal.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");
            private final HBox hbox = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setTooltip(new Tooltip("Editar local"));
                btnDelete.setTooltip(new Tooltip("Apagar local"));
                btnEdit.getStyleClass().add("btn-icon");
                btnDelete.getStyleClass().add("btn-icon");
                btnDelete.setStyle("-fx-text-fill: #ef4444;");

                btnEdit.setOnAction(e -> editarLocal(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> apagarLocal(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
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
        Dialog<LocalCreateDTO> dialog = createLocalDialog(null);
        Optional<LocalCreateDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            try {
                LocalDTO criado = localService.criar(dto);
                if (criado != null) {
                    mostrarSucesso("Local '" + criado.getNome() + "' criado com sucesso!");
                    carregarLocais();
                } else {
                    mostrarErro("Falha ao criar local.");
                }
            } catch (Exception e) {
                mostrarErro("Erro ao criar local: " + e.getMessage());
            }
        });
    }

    private void editarLocal(LocalDTO local) {
        Dialog<LocalCreateDTO> dialog = createLocalDialog(local);
        Optional<LocalCreateDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            try {
                LocalDTO atualizado = localService.atualizar(local.getId(), dto);
                if (atualizado != null) {
                    mostrarSucesso("Local atualizado com sucesso!");
                    carregarLocais();
                } else {
                    mostrarErro("Falha ao atualizar local.");
                }
            } catch (Exception e) {
                mostrarErro("Erro ao atualizar local: " + e.getMessage());
            }
        });
    }

    private void apagarLocal(LocalDTO local) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminação");
        confirm.setHeaderText("Apagar local: " + local.getNome());
        confirm.setContentText("Esta ação não pode ser revertida. Tem a certeza?");

        confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            try {
                if (localService.apagar(local.getId())) {
                    mostrarSucesso("Local eliminado com sucesso!");
                    carregarLocais();
                } else {
                    mostrarErro("Falha ao eliminar local.");
                }
            } catch (Exception e) {
                mostrarErro("Erro ao eliminar local: " + e.getMessage());
            }
        });
    }

    private Dialog<LocalCreateDTO> createLocalDialog(LocalDTO existing) {
        Dialog<LocalCreateDTO> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Novo Local" : "Editar Local");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1E1E1E;");

        TextField tfNome = new TextField();
        tfNome.setPromptText("Nome do local");
        TextField tfCapacidade = new TextField();
        tfCapacidade.setPromptText("Capacidade máxima");
        TextField tfMorada = new TextField();
        tfMorada.setPromptText("Morada completa");

        String inputStyle = "-fx-background-color: #2A2A2A; -fx-text-fill: white; -fx-prompt-text-fill: #6B7280;";
        tfNome.setStyle(inputStyle);
        tfCapacidade.setStyle(inputStyle);
        tfMorada.setStyle(inputStyle);

        if (existing != null) {
            tfNome.setText(existing.getNome());
            tfCapacidade.setText(String.valueOf(existing.getCapacidade()));
            tfMorada.setText(existing.getMorada());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        String labelStyle = "-fx-text-fill: #E0E0E0; -fx-font-weight: bold;";
        Label lblNome = new Label("Nome:");
        lblNome.setStyle(labelStyle);
        Label lblCapacidade = new Label("Capacidade:");
        lblCapacidade.setStyle(labelStyle);
        Label lblMorada = new Label("Morada:");
        lblMorada.setStyle(labelStyle);

        grid.add(lblNome, 0, 0);
        grid.add(tfNome, 1, 0);
        grid.add(lblCapacidade, 0, 1);
        grid.add(tfCapacidade, 1, 1);
        grid.add(lblMorada, 0, 2);
        grid.add(tfMorada, 1, 2);

        dialogPane.setContent(grid);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    LocalCreateDTO dto = new LocalCreateDTO();
                    dto.setNome(tfNome.getText());
                    dto.setCapacidade(Integer.parseInt(tfCapacidade.getText()));
                    dto.setMorada(tfMorada.getText());
                    return dto;
                } catch (NumberFormatException e) {
                    mostrarErro("Capacidade deve ser um número.");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private void setupInscricoesTable() {
        colInscId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colInscEvento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEventoTitulo() != null ? c.getValue().getEventoTitulo()
                        : "Evento #" + c.getValue().getEventoId()));
        colInscUtilizador.setCellValueFactory(c -> new SimpleStringProperty(
                "User #" + c.getValue().getUtilizadorNumero()));
        colInscData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataInscricao() != null ? c.getValue().getDataInscricao().format(DTF) : ""));
        colInscEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() != null ? c.getValue().getEstado().toString() : ""));
        colInscCheckIn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isCheckIn() ? "Sim" : "Nao"));
    }

    @FXML
    public void carregarInscricoes() {
        mostrarInfo("Selecione um evento no filtro para ver as inscrições.");
    }

    private void setupLogsTable() {
        colLogId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLogAcao.setCellValueFactory(new PropertyValueFactory<>("acao"));
        colLogEntidade.setCellValueFactory(new PropertyValueFactory<>("entidade"));
        colLogEntidadeId.setCellValueFactory(new PropertyValueFactory<>("entidadeId"));
        colLogAutor.setCellValueFactory(c -> new SimpleStringProperty(
                "User #" + c.getValue().getAutorNumero()));
        colLogData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataHora() != null ? c.getValue().getDataHora().format(DTF) : ""));
        colLogMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
    }

    private void setupCertificadosTable() {
        // Configurar combo de eventos para certificados
        if (cmbEventosCertAdmin != null) {
            try {
                List<EventoDTO> eventos = eventoService.listarTodos();
                cmbEventosCertAdmin.setItems(FXCollections.observableArrayList(eventos));
                cmbEventosCertAdmin.setConverter(new javafx.util.StringConverter<>() {
                    @Override
                    public String toString(EventoDTO e) {
                        return e != null ? e.getTitulo() : "";
                    }

                    @Override
                    public EventoDTO fromString(String s) {
                        return null;
                    }
                });

                // Ao selecionar evento, carregar certificados
                cmbEventosCertAdmin.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        carregarCertificadosEvento(newVal.getId());
                    }
                });
            } catch (Exception e) {
                System.err.println("Erro ao configurar combo certificados: " + e.getMessage());
            }
        }

        // Configurar colunas da tabela
        if (colCertAdminUtilizador != null) {
            colCertAdminUtilizador.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getUtilizadorNome() != null ? c.getValue().getUtilizadorNome()
                            : "User #" + c.getValue().getUtilizadorNumero()));
        }
        if (colCertAdminEvento != null) {
            colCertAdminEvento.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getEventoTitulo() != null ? c.getValue().getEventoTitulo() : ""));
        }
        if (colCertAdminTipo != null) {
            colCertAdminTipo.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getTipoDescricao() != null ? c.getValue().getTipoDescricao()
                            : (c.getValue().getTipo() != null ? c.getValue().getTipo().getDescricao() : "Presenca")));
        }
        if (colCertAdminData != null) {
            colCertAdminData.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getDataEmissao() != null ? c.getValue().getDataEmissao().format(DTF) : ""));
        }
        if (colCertAdminCodigo != null) {
            colCertAdminCodigo.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getCodigoVerificacao() != null ? c.getValue().getCodigoVerificacao() : ""));
        }
    }

    private void carregarCertificadosEvento(Integer eventoId) {
        if (tblCertificadosAdmin == null)
            return;
        try {
            List<CertificadoDTO> certs = certificadoService.listarPorEvento(eventoId);
            tblCertificadosAdmin.setItems(FXCollections.observableArrayList(certs));
        } catch (Exception e) {
            mostrarErro("Erro ao carregar certificados: " + e.getMessage());
        }
    }

    @FXML
    public void emitirCertificadosEmMassa() {
        EventoDTO selected = cmbEventosCertAdmin != null ? cmbEventosCertAdmin.getValue() : null;
        if (selected == null) {
            mostrarAviso("Selecione um evento.");
            return;
        }
        if (!gestaoeventos.client.model.UserSession.getInstance().isLoggedIn())
            return;

        try {
            Integer emitidoPor = gestaoeventos.client.model.UserSession.getInstance().getUser().getNumero();
            // Admin emite certificado tipo PRESENCA (basico)
            String resultado = certificadoService.emitirEmMassaComTipo(
                    selected.getId(), emitidoPor, gestaoeventos.entity.TipoCertificado.PRESENCA);

            if (lblResultadoCertificadosAdmin != null) {
                lblResultadoCertificadosAdmin.setText(resultado);
            }
            mostrarSucesso("Certificados de Presenca emitidos!");
            carregarCertificadosEvento(selected.getId());
        } catch (Exception e) {
            mostrarErro("Erro ao emitir certificados: " + e.getMessage());
        }
    }

    @FXML
    public void carregarLogs() {
        try {
            List<LogAuditoriaDTO> logs = logService.listarTodos();
            tblLogs.setItems(FXCollections.observableArrayList(logs));
            mostrarSucesso("Logs carregados: " + logs.size() + " registos.");
        } catch (Exception e) {
            mostrarErro("Erro ao carregar logs: " + e.getMessage());
        }
    }

    @FXML
    public void filtrarLogs() {
        carregarLogs();
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
        return tblUtilizadores.getScene() != null ? tblUtilizadores.getScene().getWindow() : null;
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
