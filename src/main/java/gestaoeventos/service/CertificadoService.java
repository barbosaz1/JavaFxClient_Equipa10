package gestaoeventos.service;

import gestaoeventos.dto.CertificadoDTO;
import gestaoeventos.entity.Certificado;
import gestaoeventos.entity.Inscricao;
import gestaoeventos.entity.TipoCertificado;
import gestaoeventos.exception.BusinessException;
import gestaoeventos.exception.NotFoundException;
import gestaoeventos.repository.CertificadoRepository;
import gestaoeventos.repository.InscricaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CertificadoService {

    private final CertificadoRepository certificadoRepository;
    private final InscricaoRepository inscricaoRepository;

    public CertificadoService(CertificadoRepository certificadoRepository,
            InscricaoRepository inscricaoRepository) {
        this.certificadoRepository = certificadoRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    public List<CertificadoDTO> listarPorUtilizador(Integer utilizadorNumero) {
        return certificadoRepository.findByUtilizadorNumero(utilizadorNumero)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CertificadoDTO> listarPorEvento(Integer eventoId) {
        return certificadoRepository.findByEventoId(eventoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CertificadoDTO obterPorCodigo(String codigoVerificacao) {
        Certificado cert = certificadoRepository.findByCodigoVerificacao(codigoVerificacao)
                .orElseThrow(() -> new NotFoundException("Certificado nao encontrado"));
        return toDTO(cert);
    }

    /**
     * Emite um certificado basico de presenca
     */
    @Transactional
    public CertificadoDTO emitirCertificado(Integer inscricaoId, Integer emitidoPorNumero) {
        return emitirCertificadoComTipo(inscricaoId, emitidoPorNumero, TipoCertificado.PRESENCA);
    }

    /**
     * Emite um certificado com tipo especifico (DOCENTE tem mais valor)
     */
    @Transactional
    public CertificadoDTO emitirCertificadoComTipo(Integer inscricaoId, Integer emitidoPorNumero,
            TipoCertificado tipo) {
        // Verifica se ja existe certificado para esta inscricao
        if (certificadoRepository.existsByInscricaoId(inscricaoId)) {
            throw new BusinessException("Ja existe um certificado emitido para esta inscricao");
        }

        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new NotFoundException("Inscricao nao encontrada"));

        // Verifica se o participante fez check-in
        if (!inscricao.isCheckIn()) {
            throw new BusinessException("O participante nao fez check-in neste evento");
        }

        Certificado certificado = new Certificado();
        certificado.setInscricao(inscricao);
        certificado.setDataEmissao(LocalDateTime.now());
        certificado.setCodigoVerificacao(gerarCodigoVerificacao());
        certificado.setEmitidoPorNumero(emitidoPorNumero);
        certificado.setTipo(tipo != null ? tipo : TipoCertificado.PRESENCA);

        certificado = certificadoRepository.save(certificado);
        return toDTO(certificado);
    }

    @Transactional
    public void emitirCertificadosEmMassa(Integer eventoId, Integer emitidoPorNumero) {
        emitirCertificadosEmMassaComTipo(eventoId, emitidoPorNumero, TipoCertificado.PRESENCA);
    }

    @Transactional
    public void emitirCertificadosEmMassaComTipo(Integer eventoId, Integer emitidoPorNumero, TipoCertificado tipo) {
        List<Inscricao> inscricoesComCheckin = inscricaoRepository.findByEventoIdAndCheckInTrue(eventoId);

        for (Inscricao inscricao : inscricoesComCheckin) {
            if (!certificadoRepository.existsByInscricaoId(inscricao.getId())) {
                Certificado certificado = new Certificado();
                certificado.setInscricao(inscricao);
                certificado.setDataEmissao(LocalDateTime.now());
                certificado.setCodigoVerificacao(gerarCodigoVerificacao());
                certificado.setEmitidoPorNumero(emitidoPorNumero);
                certificado.setTipo(tipo != null ? tipo : TipoCertificado.PRESENCA);
                certificadoRepository.save(certificado);
            }
        }
    }

    private String gerarCodigoVerificacao() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private CertificadoDTO toDTO(Certificado cert) {
        CertificadoDTO dto = new CertificadoDTO();
        dto.setId(cert.getId());
        dto.setInscricaoId(cert.getInscricao().getId());
        dto.setEventoId(cert.getInscricao().getEvento().getId());
        dto.setEventoTitulo(cert.getInscricao().getEvento().getTitulo());
        dto.setUtilizadorNumero(cert.getInscricao().getUtilizador().getNumero());
        dto.setUtilizadorNome(cert.getInscricao().getUtilizador().getNome());
        dto.setDataEmissao(cert.getDataEmissao());
        dto.setCodigoVerificacao(cert.getCodigoVerificacao());
        dto.setEmitidoPorNumero(cert.getEmitidoPorNumero());
        dto.setTipo(cert.getTipo());
        return dto;
    }
}
