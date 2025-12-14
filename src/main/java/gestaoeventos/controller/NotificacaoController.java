package gestaoeventos.controller;

import gestaoeventos.dto.NotificacaoDTO;
import gestaoeventos.service.NotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping("/utilizador/{numero}")
    public List<NotificacaoDTO> listarPorUtilizador(@PathVariable Integer numero) {
        return notificacaoService.listarPorDestinatario(numero);
    }

    @GetMapping("/anuncios/visiveis")
    public List<NotificacaoDTO> listarAnunciosVisiveis() {
        return notificacaoService.listarAnunciosVisiveis();
    }

    @GetMapping("/anuncios")
    public List<NotificacaoDTO> listarTodosAnuncios() {
        return notificacaoService.listarTodosAnuncios();
    }

    @PostMapping("/{id}/lida")
    public NotificacaoDTO marcarComoLida(@PathVariable Integer id) {
        return notificacaoService.marcarComoLida(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificacaoDTO> atualizar(@PathVariable Integer id,
            @RequestBody java.util.Map<String, Object> body) {
        String conteudo = (String) body.get("conteudo");

        LocalDateTime dataInicio = null;
        LocalDateTime dataFim = null;

        if (body.get("dataInicioExibicao") != null) {
            dataInicio = LocalDateTime.parse((String) body.get("dataInicioExibicao"));
        }
        if (body.get("dataFimExibicao") != null) {
            dataFim = LocalDateTime.parse((String) body.get("dataFimExibicao"));
        }

        NotificacaoDTO atualizado = notificacaoService.atualizarAnuncio(id, conteudo, dataInicio, dataFim);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Integer id) {
        notificacaoService.apagar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> enviarBroadcast(@RequestBody java.util.Map<String, Object> body) {
        String conteudo = (String) body.get("conteudo");
        Integer autorNumero = (Integer) body.get("autorNumero");

        LocalDateTime dataInicio = null;
        LocalDateTime dataFim = null;

        if (body.get("dataInicioExibicao") != null) {
            dataInicio = LocalDateTime.parse((String) body.get("dataInicioExibicao"));
        }
        if (body.get("dataFimExibicao") != null) {
            dataFim = LocalDateTime.parse((String) body.get("dataFimExibicao"));
        }

        int count = notificacaoService.enviarAnuncioBroadcast(conteudo, autorNumero, dataInicio, dataFim);
        return ResponseEntity.ok("Anuncio enviado para " + count + " utilizadores");
    }
}
