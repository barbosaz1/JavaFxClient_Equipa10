package gestaoeventos.controller;

import gestaoeventos.dto.LogAuditoriaDTO;
import gestaoeventos.service.LogAuditoriaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogAuditoriaController {

    private final LogAuditoriaService logAuditoriaService;

    public LogAuditoriaController(LogAuditoriaService logAuditoriaService) {
        this.logAuditoriaService = logAuditoriaService;
    }

    @GetMapping
    public List<LogAuditoriaDTO> listarTodos() {
        return logAuditoriaService.listarTodos();
    }

    @GetMapping("/entidade/{entidade}/{id}")
    public List<LogAuditoriaDTO> listarPorEntidade(@PathVariable String entidade,
                                                   @PathVariable Integer id) {
        return logAuditoriaService.listarPorEntidade(entidade, id);
    }

    @GetMapping("/autor/{numero}")
    public List<LogAuditoriaDTO> listarPorAutor(@PathVariable Integer numero) {
        return logAuditoriaService.listarPorAutor(numero);
    }
}
