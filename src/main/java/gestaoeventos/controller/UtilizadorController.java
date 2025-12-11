package gestaoeventos.controller;

import gestaoeventos.dto.UtilizadorCreateDTO;
import gestaoeventos.dto.UtilizadorDTO;
import gestaoeventos.service.UtilizadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilizadores")
public class UtilizadorController {

    private final UtilizadorService utilizadorService;

    public UtilizadorController(UtilizadorService utilizadorService) {
        this.utilizadorService = utilizadorService;
    }
    
    @PatchMapping("/{numero}/ativar")
    public UtilizadorDTO ativar(@PathVariable Integer numero) {
        return utilizadorService.alterarEstadoAtivo(numero, true);
    }

    @PatchMapping("/{numero}/desativar")
    public UtilizadorDTO desativar(@PathVariable Integer numero) {
        return utilizadorService.alterarEstadoAtivo(numero, false);
    }

    @GetMapping
    public List<UtilizadorDTO> listar() {
        return utilizadorService.listarTodos();
    }

    @GetMapping("/{numero}")
    public UtilizadorDTO obter(@PathVariable Integer numero) {
        return utilizadorService.obterPorNumero(numero);
    }

    @PostMapping
    public ResponseEntity<UtilizadorDTO> criar(@RequestBody UtilizadorCreateDTO dto) {
        UtilizadorDTO criado = utilizadorService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{numero}")
    public UtilizadorDTO atualizar(@PathVariable Integer numero,
                                   @RequestBody UtilizadorCreateDTO dto) {
        return utilizadorService.atualizar(numero, dto);
    }

    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> apagar(@PathVariable Integer numero) {
        utilizadorService.apagar(numero);
        return ResponseEntity.noContent().build();
    }
}
