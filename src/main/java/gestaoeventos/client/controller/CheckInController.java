package gestaoeventos.client.controller;

import gestaoeventos.client.service.InscricaoService;
import gestaoeventos.dto.InscricaoDTO;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CheckInController {

    @FXML private TextField txtToken;
    @FXML private Label lblResultado;
    @FXML private Button btnValidar;

    private final InscricaoService inscricaoService = new InscricaoService();

    @FXML
    void handleCheckIn() {
        String token = txtToken.getText().trim();
        if (token.isEmpty()) {
            lblResultado.setText("Por favor introduza o código.");
            lblResultado.getStyleClass().setAll("msg-error");
            return;
        }

        btnValidar.setDisable(true);
        lblResultado.setText("A validar...");
        lblResultado.getStyleClass().clear();

        Task<InscricaoDTO> task = new Task<>() {
            @Override
            protected InscricaoDTO call() throws Exception {
                return inscricaoService.checkinPorQrCode(token);
            }
        };

        task.setOnSucceeded(e -> {
            InscricaoDTO dto = task.getValue();
            lblResultado.setText("✅ Sucesso! Check-in confirmado.\nInscrição #" + dto.getId());
            lblResultado.getStyleClass().setAll("msg-success");
            btnValidar.setDisable(false);
            txtToken.clear();
        });

        task.setOnFailed(e -> {
            lblResultado.setText("❌ Erro: " + task.getException().getMessage());
            lblResultado.getStyleClass().setAll("msg-error");
            btnValidar.setDisable(false);
        });

        new Thread(task).start();
    }
}