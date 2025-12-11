package gestaoeventos.service;

import gestaoeventos.dto.CertificadoDTO;
import gestaoeventos.entity.Certificado;
import gestaoeventos.entity.Inscricao;
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
                .orElseThrow(() -> new NotFoundException("Certificado não encontrado"));
        return toDTO(cert);
    }

    @Transactional
    public CertificadoDTO emitirCertificado(Integer inscricaoId, Integer emitidoPorNumero) {
        // Verifica se já existe certificado para esta inscrição
        if (certificadoRepository.existsByInscricaoId(inscricaoId)) {
            throw new BusinessException("Já existe um certificado emitido para esta inscrição");
        }

        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada"));

        // Verifica se o participante fez check-in
        if (!inscricao.isCheckIn()) {
            throw new BusinessException("O participante não fez check-in neste evento");
        }

        Certificado certificado = new Certificado();
        certificado.setInscricao(inscricao);
        certificado.setDataEmissao(LocalDateTime.now());
        certificado.setCodigoVerificacao(gerarCodigoVerificacao());
        certificado.setEmitidoPorNumero(emitidoPorNumero);

        certificado = certificadoRepository.save(certificado);
        return toDTO(certificado);
    }

    @Transactional
    public void emitirCertificadosEmMassa(Integer eventoId, Integer emitidoPorNumero) {
        List<Inscricao> inscricoesComCheckin = inscricaoRepository.findByEventoIdAndCheckInTrue(eventoId);

        for (Inscricao inscricao : inscricoesComCheckin) {
            if (!certificadoRepository.existsByInscricaoId(inscricao.getId())) {
                Certificado certificado = new Certificado();
                certificado.setInscricao(inscricao);
                certificado.setDataEmissao(LocalDateTime.now());
                certificado.setCodigoVerificacao(gerarCodigoVerificacao());
                certificado.setEmitidoPorNumero(emitidoPorNumero);
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
        return dto;
    }
}
