package gestaoeventos.controller;

import gestaoeventos.dto.CertificadoDTO;
import gestaoeventos.service.CertificadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    private final CertificadoService certificadoService;

    public CertificadoController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    @GetMapping("/utilizador/{numero}")
    public List<CertificadoDTO> listarPorUtilizador(@PathVariable Integer numero) {
        return certificadoService.listarPorUtilizador(numero);
    }

    @GetMapping("/evento/{eventoId}")
    public List<CertificadoDTO> listarPorEvento(@PathVariable Integer eventoId) {
        return certificadoService.listarPorEvento(eventoId);
    }

    @GetMapping("/verificar/{codigo}")
    public CertificadoDTO verificarCertificado(@PathVariable String codigo) {
        return certificadoService.obterPorCodigo(codigo);
    }

    @PostMapping("/emitir")
    public ResponseEntity<CertificadoDTO> emitirCertificado(@RequestBody Map<String, Integer> body) {
        Integer inscricaoId = body.get("inscricaoId");
        Integer emitidoPorNumero = body.get("emitidoPorNumero");
        CertificadoDTO certificado = certificadoService.emitirCertificado(inscricaoId, emitidoPorNumero);
        return ResponseEntity.status(HttpStatus.CREATED).body(certificado);
    }

    @PostMapping("/emitir-em-massa/{eventoId}")
    public ResponseEntity<String> emitirCertificadosEmMassa(
            @PathVariable Integer eventoId,
            @RequestParam Integer emitidoPorNumero) {
        certificadoService.emitirCertificadosEmMassa(eventoId, emitidoPorNumero);
        return ResponseEntity.ok("Certificados emitidos com sucesso para todos os participantes com check-in");
    }
}
