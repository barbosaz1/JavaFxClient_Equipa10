package gestaoeventos.service;

import gestaoeventos.dto.InscricaoDTO;
import gestaoeventos.entity.Inscricao;
import gestaoeventos.entity.EstadoInscricao;
import gestaoeventos.exception.BusinessException;
import gestaoeventos.exception.NotFoundException;
import gestaoeventos.repository.InscricaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final EventoService eventoService;
    private final QrCodeService qrCodeService;

    public InscricaoService(InscricaoRepository inscricaoRepository,
            EventoService eventoService,
            QrCodeService qrCodeService) {
        this.inscricaoRepository = inscricaoRepository;
        this.eventoService = eventoService;
        this.qrCodeService = qrCodeService;
    }

    public List<InscricaoDTO> listarPorEvento(Integer eventoId) {
        return inscricaoRepository.findByEventoId(eventoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<InscricaoDTO> listarPorUtilizador(Integer numero) {
        return inscricaoRepository.findByUtilizadorNumero(numero)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InscricaoDTO obterPorId(Integer id) {
        Inscricao i = inscricaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada"));
        return toDTO(i);
    }

    /**
     * Cancela a inscrição e tenta promover alguém da lista de espera.
     */
    public InscricaoDTO cancelarInscricao(Integer id) {
        Inscricao i = inscricaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada"));

        i.setEstado(EstadoInscricao.CANCELADA);
        Inscricao salvo = inscricaoRepository.save(i);

        // tentar promover alguém da lista de espera
        if (i.getEvento() != null && i.getEvento().getId() != null) {
            eventoService.promoverDaListaEspera(i.getEvento().getId());
        }

        return toDTO(salvo);
    }

    /**
     * Check-in manual por ID da inscrição (sem token).
     */
    public InscricaoDTO fazerCheckin(Integer id) {
        Inscricao i = inscricaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada"));

        if (i.isCheckIn()) {
            throw new BusinessException("Check-in já foi efetuado para esta inscrição");
        }

        i.setCheckIn(true);
        i.setDataCheckin(LocalDateTime.now());
        Inscricao salvo = inscricaoRepository.save(i);
        return toDTO(salvo);
    }

    /**
     * Devolve apenas o URL do QR code para a inscrição.
     */
    public String obterQrCodeUrl(Integer id) {
        Inscricao i = inscricaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada"));

        if (i.getQrCodeCheckin() == null || i.getQrCodeCheckin().isBlank()) {
            throw new BusinessException("Inscrição não tem QR code associado");
        }

        return qrCodeService.gerarUrlQrCode(i.getQrCodeCheckin());
    }

    /**
     * Devolve o token real de QR code guardado na inscrição.
     */
    public String obterQrCodeToken(Integer id) {
        Inscricao i = inscricaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada"));

        if (i.getQrCodeCheckin() == null || i.getQrCodeCheckin().isBlank()) {
            throw new BusinessException("Inscrição não tem QR code associado");
        }

        return i.getQrCodeCheckin();
    }

    /**
     * Faz check-in a partir de um token lido do QR code.
     */
    public InscricaoDTO fazerCheckinPorToken(String token) {
        Inscricao i = inscricaoRepository.findByQrCodeCheckin(token)
                .orElseThrow(() -> new NotFoundException("Token de QR code inválido"));

        // Verificar validade
        LocalDateTime agora = LocalDateTime.now();
        if (i.getValidadeQrcode() != null && agora.isAfter(i.getValidadeQrcode())) {
            throw new BusinessException("QR code expirado");
        }

        // Evitar check-in duplicado
        if (i.isCheckIn()) {
            throw new BusinessException("Check-in já foi efetuado para esta inscrição");
        }

        i.setCheckIn(true);
        i.setDataCheckin(agora);
        Inscricao salvo = inscricaoRepository.save(i);

        return toDTO(salvo);
    }

    // ------------------ MAPEAMENTO DTO -------------------

    private InscricaoDTO toDTO(Inscricao i) {
        InscricaoDTO dto = new InscricaoDTO();
        dto.setId(i.getId());
        dto.setEventoId(i.getEvento() != null ? i.getEvento().getId() : null);
        dto.setEventoTitulo(i.getEvento() != null ? i.getEvento().getTitulo() : null);
        dto.setUtilizadorNumero(i.getUtilizador() != null ? i.getUtilizador().getNumero() : null);
        dto.setDataInscricao(i.getDataInscricao());
        dto.setEstado(i.getEstado());
        dto.setCheckIn(i.isCheckIn());
        dto.setDataCheckin(i.getDataCheckin());

        // Incluir token QR apenas para inscricoes ativas que ainda nao fizeram check-in
        if (i.getEstado() == EstadoInscricao.ATIVA && !i.isCheckIn() && i.getQrCodeCheckin() != null) {
            dto.setQrCodeToken(i.getQrCodeCheckin());
        }

        return dto;
    }
}
