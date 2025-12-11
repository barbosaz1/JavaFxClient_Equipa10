package gestaoeventos.controller;

import gestaoeventos.dto.EventoCreateDTO;
import gestaoeventos.dto.EventoDTO;
import gestaoeventos.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping("/search")
    public List<EventoDTO> pesquisar(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Integer localId,
            @RequestParam(required = false) Integer organizadorNumero) {
        return eventoService.pesquisar(inicio, fim, tipo, localId, organizadorNumero);
    }

    @GetMapping
    public List<EventoDTO> listar() {
        return eventoService.listarTodos();
    }

    @GetMapping("/{id}")
    public EventoDTO obter(@PathVariable Integer id) {
        return eventoService.obterPorId(id);
    }

    @PostMapping
    public ResponseEntity<EventoDTO> criar(@RequestBody EventoCreateDTO dto) {
        EventoDTO criado = eventoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PostMapping("/{id}/publicar")
    public EventoDTO publicar(@PathVariable Integer id,
            @RequestParam Integer autorNumero) {
        return eventoService.publicar(id, autorNumero);
    }

    @PostMapping("/{id}/cancelar")
    public EventoDTO cancelar(@PathVariable Integer id,
            @RequestParam Integer autorNumero,
            @RequestBody Map<String, String> body) {
        String motivo = body.getOrDefault("motivo", "Sem motivo indicado");
        return eventoService.cancelar(id, autorNumero, motivo);
    }

    @PostMapping("/{id}/inscrever")
    public ResponseEntity<String> inscrever(@PathVariable Integer id,
            @RequestParam Integer utilizadorNumero) {
        String resultado = eventoService.inscreverEmEvento(id, utilizadorNumero);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}/estatisticas")
    public gestaoeventos.dto.EstatisticasEventoDTO obterEstatisticas(@PathVariable Integer id) {
        return eventoService.obterEstatisticas(id);
    }
}
