package gestaoeventos.service;

import gestaoeventos.dto.EventoCreateDTO;
import gestaoeventos.dto.EventoDTO;
import gestaoeventos.entity.EstadoEvento;
import gestaoeventos.entity.EstadoInscricao;
import gestaoeventos.entity.Evento;
import gestaoeventos.entity.Inscricao;
import gestaoeventos.entity.ListaEspera;
import gestaoeventos.entity.Local;
import gestaoeventos.entity.LogAuditoria;
import gestaoeventos.entity.TipoEvento;
import gestaoeventos.entity.Utilizador;
import gestaoeventos.exception.BusinessException;
import gestaoeventos.exception.NotFoundException;
import gestaoeventos.repository.EventoRepository;
import gestaoeventos.repository.InscricaoRepository;
import gestaoeventos.repository.ListaEsperaRepository;
import gestaoeventos.repository.LocalRepository;
import gestaoeventos.repository.LogAuditoriaRepository;
import gestaoeventos.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final LocalRepository localRepository;
    private final InscricaoRepository inscricaoRepository;
    private final ListaEsperaRepository listaEsperaRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;

    public EventoService(EventoRepository eventoRepository,
            UtilizadorRepository utilizadorRepository,
            LocalRepository localRepository,
            InscricaoRepository inscricaoRepository,
            ListaEsperaRepository listaEsperaRepository,
            LogAuditoriaRepository logAuditoriaRepository) {
        this.eventoRepository = eventoRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.localRepository = localRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.listaEsperaRepository = listaEsperaRepository;
        this.logAuditoriaRepository = logAuditoriaRepository;
    }

    public List<EventoDTO> listarTodos() {
        return eventoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EventoDTO obterPorId(Integer id) {
        Evento e = eventoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));
        return toDTO(e);
    }

    public EventoDTO criar(EventoCreateDTO dto) {
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            throw new BusinessException("Título é obrigatório");
        }
        if (dto.getDataInicio() == null || dto.getDataFim() == null) {
            throw new BusinessException("Datas de início e fim são obrigatórias");
        }
        if (dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new BusinessException("Data de fim não pode ser anterior à data de início");
        }

        Utilizador criador = utilizadorRepository.findById(dto.getCriadorNumero())
                .orElseThrow(() -> new BusinessException("Criador não encontrado"));

        Local local = localRepository.findById(dto.getLocalId())
                .orElseThrow(() -> new BusinessException("Local não encontrado"));

        Evento e = new Evento();
        e.setTitulo(dto.getTitulo());
        e.setDescricao(dto.getDescricao());
        e.setDataInicio(dto.getDataInicio());
        e.setDataFim(dto.getDataFim());
        e.setMaxParticipantes(dto.getMaxParticipantes());
        e.setTipo(dto.getTipo());
        e.setAreaTematica(dto.getAreaTematica());
        e.setCriador(criador);
        e.setLocal(local);
        e.setEstado(EstadoEvento.RASCUNHO);

        Evento salvo = eventoRepository.save(e);
        registarLog("CRIAR_EVENTO", "Evento", salvo.getId(), criador, null);
        return toDTO(salvo);
    }

    public EventoDTO publicar(Integer id, Integer autorNumero) {
        Evento e = eventoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        Utilizador autor = utilizadorRepository.findById(autorNumero)
                .orElseThrow(() -> new BusinessException("Autor não encontrado"));

        e.setEstado(EstadoEvento.PUBLICADO);
        Evento salvo = eventoRepository.save(e);

        registarLog("PUBLICAR_EVENTO", "Evento", id, autor, null);
        return toDTO(salvo);
    }

    public EventoDTO cancelar(Integer id, Integer autorNumero, String motivo) {
        Evento e = eventoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        Utilizador autor = utilizadorRepository.findById(autorNumero)
                .orElseThrow(() -> new BusinessException("Autor não encontrado"));

        e.setEstado(EstadoEvento.CANCELADO);
        e.setMotivoRemocao(motivo);
        Evento salvo = eventoRepository.save(e);

        registarLog("CANCELAR_EVENTO", "Evento", id, autor, motivo);
        return toDTO(salvo);
    }

    /**
     * Inscrição com:
     * - validação de estado PUBLICADO
     * - bloqueio após início do evento
     * - gestão de capacidade + lista de espera
     * - geração de token de QR code e validade
     */
    public String inscreverEmEvento(Integer eventoId, Integer utilizadorNumero) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (evento.getEstado() != EstadoEvento.PUBLICADO) {
            throw new BusinessException("Só é possível inscrever em eventos publicados");
        }

        if (evento.getDataInicio() != null &&
                evento.getDataInicio().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Não é possível inscrever após o início do evento");
        }

        Utilizador utilizador = utilizadorRepository.findById(utilizadorNumero)
                .orElseThrow(() -> new BusinessException("Utilizador não encontrado"));

        boolean jaInscrito = inscricaoRepository.findByEventoId(eventoId)
                .stream()
                .anyMatch(i -> i.getUtilizador().getNumero().equals(utilizadorNumero));

        if (jaInscrito) {
            throw new BusinessException("Utilizador já está inscrito neste evento");
        }

        int inscritosAtivos = inscricaoRepository
                .countByEventoIdAndEstado(eventoId, EstadoInscricao.ATIVA);

        if (evento.getMaxParticipantes() != null &&
                inscritosAtivos >= evento.getMaxParticipantes()) {

            ListaEspera le = new ListaEspera();
            le.setEvento(evento);
            le.setUtilizador(utilizador);
            int posicao = listaEsperaRepository
                    .findByEventoIdOrderByDataEntradaAsc(eventoId)
                    .size() + 1;
            le.setPosicao(posicao);

            listaEsperaRepository.save(le);
            registarLog("ENTRADA_LISTA_ESPERA", "Evento", eventoId, utilizador, null);
            return "EVENTO_LOTADO_LISTA_ESPERA";
        }

        // ---------- INSCRIÇÃO NORMAL + QR CODE ----------
        Inscricao insc = new Inscricao();
        insc.setEvento(evento);
        insc.setUtilizador(utilizador);
        insc.setEstado(EstadoInscricao.ATIVA);
        insc.setDataInscricao(LocalDateTime.now());

        // 1º save para obter ID
        Inscricao salvo = inscricaoRepository.save(insc);

        // Gerar token de QR code único
        String token = "CHK-" + salvo.getId() + "-" + UUID.randomUUID();
        salvo.setQrCodeCheckin(token);

        // Validade do QR code: até 2 horas após o início do evento
        if (evento.getDataInicio() != null) {
            salvo.setValidadeQrcode(evento.getDataInicio().plusHours(2));
        }

        // 2º save com token + validade
        inscricaoRepository.save(salvo);

        registarLog("INSCRICAO_EVENTO", "Evento", eventoId, utilizador, null);
        return "INSCRICAO_OK";
    }

    /**
     * Promove o primeiro da lista de espera a inscrição ativa.
     */
    public void promoverDaListaEspera(Integer eventoId) {
        ListaEspera proximo = listaEsperaRepository
                .findFirstByEventoIdOrderByPosicaoAsc(eventoId)
                .orElse(null);

        if (proximo == null) {
            return;
        }

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        Inscricao nova = new Inscricao();
        nova.setEvento(evento);
        nova.setUtilizador(proximo.getUtilizador());
        nova.setEstado(EstadoInscricao.ATIVA);
        nova.setDataInscricao(LocalDateTime.now());

        // gerar novo token para a nova inscrição promovida
        Inscricao salvo = inscricaoRepository.save(nova);
        String token = "CHK-" + salvo.getId() + "-" + UUID.randomUUID();
        salvo.setQrCodeCheckin(token);
        if (evento.getDataInicio() != null) {
            salvo.setValidadeQrcode(evento.getDataInicio().plusHours(2));
        }
        inscricaoRepository.save(salvo);

        listaEsperaRepository.delete(proximo);

        registarLog("PROMOVER_LISTA_ESPERA", "Evento", eventoId, proximo.getUtilizador(), null);
    }

    /**
     * Obtém estatísticas detalhadas de um evento
     */
    public gestaoeventos.dto.EstatisticasEventoDTO obterEstatisticas(Integer eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        List<Inscricao> inscricoes = inscricaoRepository.findByEventoId(eventoId);

        int totalInscricoes = inscricoes.size();
        int inscricoesAtivas = (int) inscricoes.stream()
                .filter(i -> i.getEstado() == EstadoInscricao.ATIVA).count();
        int inscricoesCanceladas = (int) inscricoes.stream()
                .filter(i -> i.getEstado() == EstadoInscricao.CANCELADA).count();
        int checkInsRealizados = (int) inscricoes.stream()
                .filter(Inscricao::isCheckIn).count();

        Integer maxParticipantes = evento.getMaxParticipantes();
        int vagasDisponiveis = maxParticipantes != null ? Math.max(0, maxParticipantes - inscricoesAtivas) : -1;
        double percentualOcupacao = maxParticipantes != null && maxParticipantes > 0
                ? (inscricoesAtivas * 100.0 / maxParticipantes)
                : 0.0;

        gestaoeventos.dto.EstatisticasEventoDTO dto = new gestaoeventos.dto.EstatisticasEventoDTO();
        dto.setEventoId(evento.getId());
        dto.setEventoTitulo(evento.getTitulo());
        dto.setTotalInscricoes(totalInscricoes);
        dto.setInscricoesAtivas(inscricoesAtivas);
        dto.setInscricoesCanceladas(inscricoesCanceladas);
        dto.setCheckInsRealizados(checkInsRealizados);
        dto.setMaxParticipantes(maxParticipantes);
        dto.setVagasDisponiveis(vagasDisponiveis);
        dto.setPercentualOcupacao(percentualOcupacao);
        dto.setCertificadosEmitidos(0); // Será preenchido quando tiver acesso ao CertificadoRepository

        return dto;
    }

    private void registarLog(String acao, String entidade, Integer entidadeId,
            Utilizador autor, String motivo) {
        LogAuditoria log = new LogAuditoria();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setAutor(autor);
        log.setMotivo(motivo);
        // dataHora e ipOrigem podem ser preenchidos automaticamente na entity, se
        // quiseres
        logAuditoriaRepository.save(log);
    }

    private EventoDTO toDTO(Evento e) {
        EventoDTO dto = new EventoDTO();
        dto.setId(e.getId());
        dto.setTitulo(e.getTitulo());
        dto.setDescricao(e.getDescricao());
        dto.setDataInicio(e.getDataInicio());
        dto.setDataFim(e.getDataFim());
        dto.setMaxParticipantes(e.getMaxParticipantes());
        dto.setEstado(e.getEstado());
        dto.setTipo(e.getTipo());
        dto.setAreaTematica(e.getAreaTematica());
        dto.setCriadorNumero(e.getCriador().getNumero());
        dto.setLocalId(e.getLocal().getId());
        dto.setMotivoRemocao(e.getMotivoRemocao());
        return dto;
    }

    /**
     * Pesquisa de eventos com filtros opcionais:
     * - intervalo de datas (dataInicio)
     * - tipo (enum TipoEvento)
     * - localId
     * - organizadorNumero (criador)
     */
    public List<EventoDTO> pesquisar(String inicioStr,
            String fimStr,
            String tipoStr,
            Integer localId,
            Integer organizadorNumero) {

        List<Evento> todos = eventoRepository.findAll();
        Stream<Evento> stream = todos.stream();

        // Filtrar por intervalo de datas
        LocalDateTime inicio = null;
        LocalDateTime fim = null;
        try {
            if (inicioStr != null && !inicioStr.isBlank()) {
                inicio = LocalDateTime.parse(inicioStr);
            }
            if (fimStr != null && !fimStr.isBlank()) {
                fim = LocalDateTime.parse(fimStr);
            }
        } catch (DateTimeParseException e) {
            throw new BusinessException(
                    "Formato de data inválido. Use ISO-8601, ex: 2025-12-01T10:00:00");
        }

        if (inicio != null) {
            LocalDateTime finalInicio = inicio;
            stream = stream.filter(e -> e.getDataInicio() != null &&
                    !e.getDataInicio().isBefore(finalInicio));
        }
        if (fim != null) {
            LocalDateTime finalFim = fim;
            stream = stream.filter(e -> e.getDataInicio() != null &&
                    !e.getDataInicio().isAfter(finalFim));
        }

        // Filtrar por tipo
        if (tipoStr != null && !tipoStr.isBlank()) {
            try {
                TipoEvento tipoEnum = TipoEvento.valueOf(tipoStr);
                stream = stream.filter(e -> e.getTipo() == tipoEnum);
            } catch (IllegalArgumentException ex) {
                throw new BusinessException("Tipo de evento inválido");
            }
        }

        // Filtrar por local
        if (localId != null) {
            stream = stream.filter(e -> e.getLocal() != null &&
                    e.getLocal().getId().equals(localId));
        }

        // Filtrar por organizador
        if (organizadorNumero != null) {
            stream = stream.filter(e -> e.getCriador() != null &&
                    e.getCriador().getNumero().equals(organizadorNumero));
        }

        return stream
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
