package gestaoeventos.service;

import gestaoeventos.dto.EventoCreateDTO;
import gestaoeventos.dto.EventoDTO;
import gestaoeventos.dto.EstatisticasEventoDTO;
import gestaoeventos.dto.InscricaoResultadoDTO;
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

/**
 * Serviço para gestão de eventos.
 * Trata da criação, edição, publicação, cancelamento e inscrições em eventos.
 */
@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final LocalRepository localRepository;
    private final InscricaoRepository inscricaoRepository;
    private final ListaEsperaRepository listaEsperaRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;
    private final QrCodeService qrCodeService;

    public EventoService(EventoRepository eventoRepository,
            UtilizadorRepository utilizadorRepository,
            LocalRepository localRepository,
            InscricaoRepository inscricaoRepository,
            ListaEsperaRepository listaEsperaRepository,
            LogAuditoriaRepository logAuditoriaRepository,
            QrCodeService qrCodeService) {
        this.eventoRepository = eventoRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.localRepository = localRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.listaEsperaRepository = listaEsperaRepository;
        this.logAuditoriaRepository = logAuditoriaRepository;
        this.qrCodeService = qrCodeService;
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

    /**
     * Cria um novo evento no sistema.
     */
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
        e.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoEvento.RASCUNHO);

        Evento salvo = eventoRepository.save(e);
        registarLog("CRIAR_EVENTO", "Evento", salvo.getId(), criador, null);
        return toDTO(salvo);
    }

    /**
     * Atualiza um evento existente.
     */
    public EventoDTO atualizar(Integer id, EventoCreateDTO dto) {
        Evento e = eventoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            throw new BusinessException("Título é obrigatório");
        }
        if (dto.getDataInicio() == null || dto.getDataFim() == null) {
            throw new BusinessException("Datas de início e fim são obrigatórias");
        }
        if (dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new BusinessException("Data de fim não pode ser anterior à data de início");
        }
        if (dto.getCriadorNumero() == null) {
            throw new BusinessException("Identificação do utilizador é obrigatória");
        }

        Utilizador autor = utilizadorRepository.findById(dto.getCriadorNumero())
                .orElseThrow(() -> new BusinessException("Utilizador não encontrado"));

        Local local = localRepository.findById(dto.getLocalId())
                .orElseThrow(() -> new BusinessException("Local não encontrado"));

        e.setTitulo(dto.getTitulo());
        e.setDescricao(dto.getDescricao());
        e.setDataInicio(dto.getDataInicio());
        e.setDataFim(dto.getDataFim());
        e.setMaxParticipantes(dto.getMaxParticipantes());
        e.setTipo(dto.getTipo());
        e.setAreaTematica(dto.getAreaTematica());
        e.setLocal(local);

        if (dto.getEstado() != null) {
            e.setEstado(dto.getEstado());
        }

        Evento salvo = eventoRepository.save(e);
        registarLog("ATUALIZAR_EVENTO", "Evento", salvo.getId(), autor, null);
        return toDTO(salvo);
    }

    /**
     * Publica um evento (torna-o visível para inscrições).
     */
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

    /**
     * Cancela um evento com um motivo.
     */
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
     * Inscreve um utilizador num evento.
     * Se o evento estiver cheio, adiciona à lista de espera.
     * Devolve o QR code para fazer check-in.
     */
    public InscricaoResultadoDTO inscreverEmEvento(Integer eventoId, Integer utilizadorNumero) {
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

        int inscritosAtivos = inscricaoRepository.countByEventoIdAndEstado(eventoId, EstadoInscricao.ATIVA);

        // Se não há vagas, vai para a lista de espera
        if (evento.getMaxParticipantes() != null && inscritosAtivos >= evento.getMaxParticipantes()) {
            ListaEspera le = new ListaEspera();
            le.setEvento(evento);
            le.setUtilizador(utilizador);
            int posicao = listaEsperaRepository.findByEventoIdOrderByDataEntradaAsc(eventoId).size() + 1;
            le.setPosicao(posicao);

            listaEsperaRepository.save(le);
            registarLog("ENTRADA_LISTA_ESPERA", "Evento", eventoId, utilizador, null);
            return new InscricaoResultadoDTO("EVENTO_LOTADO_LISTA_ESPERA", null, null, null);
        }

        // Criar inscrição
        Inscricao insc = new Inscricao();
        insc.setEvento(evento);
        insc.setUtilizador(utilizador);
        insc.setEstado(EstadoInscricao.ATIVA);
        insc.setDataInscricao(LocalDateTime.now());

        Inscricao salvo = inscricaoRepository.save(insc);

        // Gerar token do QR code
        String token = "CHK-" + salvo.getId() + "-" + UUID.randomUUID();
        salvo.setQrCodeCheckin(token);

        // QR code válido até 2 horas após o início
        if (evento.getDataInicio() != null) {
            salvo.setValidadeQrcode(evento.getDataInicio().plusHours(2));
        }

        inscricaoRepository.save(salvo);
        registarLog("INSCRICAO_EVENTO", "Evento", eventoId, utilizador, null);

        String qrCodeUrl = qrCodeService.gerarUrlQrCode(token);
        return new InscricaoResultadoDTO("INSCRICAO_OK", salvo.getId(), token, qrCodeUrl);
    }

    /**
     * Promove o primeiro da lista de espera para inscrição ativa.
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
     * Calcula as estatísticas de um evento.
     */
    public EstatisticasEventoDTO obterEstatisticas(Integer eventoId) {
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

        EstatisticasEventoDTO dto = new EstatisticasEventoDTO();
        dto.setEventoId(evento.getId());
        dto.setEventoTitulo(evento.getTitulo());
        dto.setTotalInscricoes(totalInscricoes);
        dto.setInscricoesAtivas(inscricoesAtivas);
        dto.setInscricoesCanceladas(inscricoesCanceladas);
        dto.setCheckInsRealizados(checkInsRealizados);
        dto.setMaxParticipantes(maxParticipantes);
        dto.setVagasDisponiveis(vagasDisponiveis);
        dto.setPercentualOcupacao(percentualOcupacao);
        dto.setCertificadosEmitidos(0);

        return dto;
    }

    /**
     * Pesquisa eventos com filtros opcionais.
     */
    public List<EventoDTO> pesquisar(String inicioStr, String fimStr, String tipoStr,
            Integer localId, Integer organizadorNumero) {

        List<Evento> todos = eventoRepository.findAll();
        Stream<Evento> stream = todos.stream();

        // Filtrar por datas
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
            throw new BusinessException("Formato de data inválido. Use ISO-8601, ex: 2025-12-01T10:00:00");
        }

        if (inicio != null) {
            LocalDateTime finalInicio = inicio;
            stream = stream.filter(e -> e.getDataInicio() != null && !e.getDataInicio().isBefore(finalInicio));
        }
        if (fim != null) {
            LocalDateTime finalFim = fim;
            stream = stream.filter(e -> e.getDataInicio() != null && !e.getDataInicio().isAfter(finalFim));
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
            stream = stream.filter(e -> e.getLocal() != null && e.getLocal().getId().equals(localId));
        }

        // Filtrar por organizador
        if (organizadorNumero != null) {
            stream = stream.filter(e -> e.getCriador() != null && e.getCriador().getNumero().equals(organizadorNumero));
        }

        return stream.map(this::toDTO).collect(Collectors.toList());
    }

    private void registarLog(String acao, String entidade, Integer entidadeId,
            Utilizador autor, String motivo) {
        LogAuditoria log = new LogAuditoria();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setAutor(autor);
        log.setMotivo(motivo);
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
}
