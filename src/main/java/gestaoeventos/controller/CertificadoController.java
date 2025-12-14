package gestaoeventos.controller;

import gestaoeventos.dto.CertificadoDTO;
import gestaoeventos.entity.TipoCertificado;
import gestaoeventos.service.CertificadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<CertificadoDTO> emitirCertificado(
            @RequestParam Integer inscricaoId,
            @RequestParam Integer emitidoPorNumero,
            @RequestParam(required = false) String tipo) {

        TipoCertificado tipoCert = TipoCertificado.PRESENCA;
        if (tipo != null && !tipo.isEmpty()) {
            try {
                tipoCert = TipoCertificado.valueOf(tipo);
            } catch (IllegalArgumentException e) {
                // mantém PRESENCA como default
            }
        }

        CertificadoDTO certificado = certificadoService.emitirCertificadoComTipo(inscricaoId, emitidoPorNumero,
                tipoCert);
        return ResponseEntity.status(HttpStatus.CREATED).body(certificado);
    }

    @PostMapping("/emitir-em-massa/{eventoId}")
    public ResponseEntity<String> emitirCertificadosEmMassa(
            @PathVariable Integer eventoId,
            @RequestParam Integer emitidoPorNumero,
            @RequestParam(required = false) String tipo) {

        TipoCertificado tipoCert = TipoCertificado.PRESENCA;
        if (tipo != null && !tipo.isEmpty()) {
            try {
                tipoCert = TipoCertificado.valueOf(tipo);
            } catch (IllegalArgumentException e) {
                // mantém PRESENCA como default
            }
        }

        certificadoService.emitirCertificadosEmMassaComTipo(eventoId, emitidoPorNumero, tipoCert);
        return ResponseEntity.ok("Certificados emitidos com sucesso para todos os participantes com check-in");
    }
}
