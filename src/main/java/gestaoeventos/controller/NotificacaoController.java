package gestaoeventos.controller;

import gestaoeventos.dto.NotificacaoDTO;
import gestaoeventos.service.NotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{id}/lida")
    public NotificacaoDTO marcarComoLida(@PathVariable Integer id) {
        return notificacaoService.marcarComoLida(id);
    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> enviarBroadcast(@RequestBody java.util.Map<String, Object> body) {
        String conteudo = (String) body.get("conteudo");
        Integer autorNumero = (Integer) body.get("autorNumero");
        int count = notificacaoService.enviarAnuncioBroadcast(conteudo, autorNumero);
        return ResponseEntity.ok("Anúncio enviado para " + count + " utilizadores");
    }
}
