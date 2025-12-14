package gestaoeventos.service;

import gestaoeventos.dto.UtilizadorCreateDTO;
import gestaoeventos.dto.UtilizadorDTO;
import gestaoeventos.entity.LogAuditoria;
import gestaoeventos.entity.Utilizador;
import gestaoeventos.exception.BusinessException;
import gestaoeventos.exception.NotFoundException;
import gestaoeventos.repository.LogAuditoriaRepository;
import gestaoeventos.repository.UtilizadorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gestão de utilizadores.
 * Inclui operações de criação, edição, ativação e desativação com registo de
 * auditoria.
 */
@Service
public class UtilizadorService {

    private final UtilizadorRepository utilizadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogAuditoriaRepository logAuditoriaRepository;

    public UtilizadorService(UtilizadorRepository utilizadorRepository,
            PasswordEncoder passwordEncoder,
            LogAuditoriaRepository logAuditoriaRepository) {
        this.utilizadorRepository = utilizadorRepository;
        this.passwordEncoder = passwordEncoder;
        this.logAuditoriaRepository = logAuditoriaRepository;
    }

    /**
     * Ativa ou desativa um utilizador.
     */
    public UtilizadorDTO alterarEstadoAtivo(Integer numero, boolean ativo, Integer autorNumero) {
        Utilizador u = utilizadorRepository.findById(numero)
                .orElseThrow(() -> new NotFoundException("Utilizador não encontrado"));

        Utilizador autor = null;
        if (autorNumero != null) {
            autor = utilizadorRepository.findById(autorNumero).orElse(null);
        }

        u.setAtivo(ativo);
        Utilizador salvo = utilizadorRepository.save(u);

        String acao = ativo ? "ATIVAR_UTILIZADOR" : "DESATIVAR_UTILIZADOR";
        registarLog(acao, "Utilizador", numero, autor, null);

        return toDTO(salvo);
    }

    /**
     * Versão sem identificação do autor (retrocompatibilidade).
     */
    public UtilizadorDTO alterarEstadoAtivo(Integer numero, boolean ativo) {
        return alterarEstadoAtivo(numero, ativo, null);
    }

    public List<UtilizadorDTO> listarTodos() {
        return utilizadorRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UtilizadorDTO obterPorNumero(Integer numero) {
        Utilizador u = utilizadorRepository.findById(numero)
                .orElseThrow(() -> new NotFoundException("Utilizador não encontrado"));
        return toDTO(u);
    }

    /**
     * Cria um novo utilizador no sistema.
     */
    public UtilizadorDTO criar(UtilizadorCreateDTO dto, Integer autorNumero) {
        if (dto.getNumero() == null) {
            throw new BusinessException("Número de utilizador é obrigatório");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessException("Password é obrigatória");
        }

        utilizadorRepository.findById(dto.getNumero())
                .ifPresent(u -> {
                    throw new BusinessException("Já existe um utilizador com esse número");
                });

        utilizadorRepository.findByEmail(dto.getEmail())
                .ifPresent(u -> {
                    throw new BusinessException("Já existe um utilizador com esse email");
                });

        Utilizador u = new Utilizador();
        u.setNumero(dto.getNumero());
        u.setNome(dto.getNome());
        u.setEmail(dto.getEmail());
        u.setPerfil(dto.getPerfil());
        u.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());

        String passwordHash = passwordEncoder.encode(dto.getPassword());
        u.setPasswordHash(passwordHash);

        Utilizador salvo = utilizadorRepository.save(u);

        // Registar no log de auditoria
        Utilizador autor = null;
        if (autorNumero != null) {
            autor = utilizadorRepository.findById(autorNumero).orElse(null);
        }
        registarLog("CRIAR_UTILIZADOR", "Utilizador", salvo.getNumero(), autor,
                "Criado utilizador: " + salvo.getNome() + " (" + salvo.getPerfil() + ")");

        return toDTO(salvo);
    }

    /**
     * Versão sem identificação do autor (retrocompatibilidade).
     */
    public UtilizadorDTO criar(UtilizadorCreateDTO dto) {
        return criar(dto, null);
    }

    /**
     * Atualiza os dados de um utilizador existente.
     */
    public UtilizadorDTO atualizar(Integer numero, UtilizadorCreateDTO dto, Integer autorNumero) {
        Utilizador existente = utilizadorRepository.findById(numero)
                .orElseThrow(() -> new NotFoundException("Utilizador não encontrado"));

        if (dto.getNome() != null) {
            existente.setNome(dto.getNome());
        }
        if (dto.getEmail() != null) {
            existente.setEmail(dto.getEmail());
        }
        if (dto.getPerfil() != null) {
            existente.setPerfil(dto.getPerfil());
        }
        if (dto.getAtivo() != null) {
            existente.setAtivo(dto.getAtivo());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            String passwordHash = passwordEncoder.encode(dto.getPassword());
            existente.setPasswordHash(passwordHash);
        }

        Utilizador salvo = utilizadorRepository.save(existente);

        // Registar no log de auditoria
        Utilizador autor = null;
        if (autorNumero != null) {
            autor = utilizadorRepository.findById(autorNumero).orElse(null);
        }
        registarLog("ATUALIZAR_UTILIZADOR", "Utilizador", numero, autor, null);

        return toDTO(salvo);
    }

    /**
     * Versão sem identificação do autor (retrocompatibilidade).
     */
    public UtilizadorDTO atualizar(Integer numero, UtilizadorCreateDTO dto) {
        return atualizar(numero, dto, null);
    }

    /**
     * Remove um utilizador do sistema.
     */
    public void apagar(Integer numero, Integer autorNumero) {
        if (!utilizadorRepository.existsById(numero)) {
            throw new NotFoundException("Utilizador não encontrado");
        }

        Utilizador autor = null;
        if (autorNumero != null) {
            autor = utilizadorRepository.findById(autorNumero).orElse(null);
        }
        registarLog("APAGAR_UTILIZADOR", "Utilizador", numero, autor, null);

        utilizadorRepository.deleteById(numero);
    }

    /**
     * Versão sem identificação do autor (retrocompatibilidade).
     */
    public void apagar(Integer numero) {
        apagar(numero, null);
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

    private UtilizadorDTO toDTO(Utilizador u) {
        return new UtilizadorDTO(
                u.getNumero(),
                u.getNome(),
                u.getEmail(),
                u.getPerfil(),
                u.isAtivo());
    }
}
