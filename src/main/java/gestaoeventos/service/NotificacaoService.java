package gestaoeventos.service;

import gestaoeventos.dto.NotificacaoDTO;
import gestaoeventos.entity.Notificacao;
import gestaoeventos.entity.TipoNotificacao;
import gestaoeventos.entity.Utilizador;
import gestaoeventos.exception.NotFoundException;
import gestaoeventos.repository.NotificacaoRepository;
import gestaoeventos.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UtilizadorRepository utilizadorRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
            UtilizadorRepository utilizadorRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.utilizadorRepository = utilizadorRepository;
    }

    public List<NotificacaoDTO> listarPorDestinatario(Integer numero) {
        return notificacaoRepository.findByDestinatarioNumeroOrderByDataCriacaoDesc(numero)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public NotificacaoDTO marcarComoLida(Integer id) {
        Notificacao n = notificacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notificação não encontrada"));
        n.setLida(true);
        Notificacao salvo = notificacaoRepository.save(n);
        return toDTO(salvo);
    }

    /**
     * Envia um anúncio para todos os utilizadores ativos
     */
    @Transactional
    public int enviarAnuncioBroadcast(String conteudo, Integer autorNumero) {
        List<Utilizador> utilizadores = utilizadorRepository.findAll()
                .stream()
                .filter(Utilizador::isAtivo)
                .collect(Collectors.toList());

        int count = 0;
        for (Utilizador dest : utilizadores) {
            Notificacao notif = new Notificacao();
            notif.setDestinatario(dest);
            notif.setTipo(TipoNotificacao.ANUNCIO);
            notif.setConteudo(conteudo);
            notif.setCanal("SISTEMA");
            notif.setLida(false);
            notif.setDataCriacao(LocalDateTime.now());
            notificacaoRepository.save(notif);
            count++;
        }
        return count;
    }

    private NotificacaoDTO toDTO(Notificacao n) {
        NotificacaoDTO dto = new NotificacaoDTO();
        dto.setId(n.getId());
        dto.setDestinatarioNumero(n.getDestinatario().getNumero());
        dto.setEventoId(n.getEvento() != null ? n.getEvento().getId() : null);
        dto.setTipo(n.getTipo());
        dto.setConteudo(n.getConteudo());
        dto.setCanal(n.getCanal());
        dto.setLida(n.isLida());
        dto.setDataCriacao(n.getDataCriacao());
        return dto;
    }
}
