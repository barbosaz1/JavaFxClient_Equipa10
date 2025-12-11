package gestaoeventos.service;

import gestaoeventos.dto.UtilizadorCreateDTO;
import gestaoeventos.dto.UtilizadorDTO;
import gestaoeventos.entity.Utilizador;
import gestaoeventos.exception.BusinessException;
import gestaoeventos.exception.NotFoundException;
import gestaoeventos.repository.UtilizadorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilizadorService {

    private final UtilizadorRepository utilizadorRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilizadorService(UtilizadorRepository utilizadorRepository,
                             PasswordEncoder passwordEncoder) {
        this.utilizadorRepository = utilizadorRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public UtilizadorDTO alterarEstadoAtivo(Integer numero, boolean ativo) {
        Utilizador u = utilizadorRepository.findById(numero)
                .orElseThrow(() -> new NotFoundException("Utilizador não encontrado"));
        u.setAtivo(ativo);
        Utilizador salvo = utilizadorRepository.save(u);
        return toDTO(salvo);
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

    public UtilizadorDTO criar(UtilizadorCreateDTO dto) {
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

        // hash da password
        String passwordHash = passwordEncoder.encode(dto.getPassword());
        u.setPasswordHash(passwordHash);

        Utilizador salvo = utilizadorRepository.save(u);
        return toDTO(salvo);
    }

    public UtilizadorDTO atualizar(Integer numero, UtilizadorCreateDTO dto) {
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
        return toDTO(salvo);
    }

    public void apagar(Integer numero) {
        if (!utilizadorRepository.existsById(numero)) {
            throw new NotFoundException("Utilizador não encontrado");
        }
        utilizadorRepository.deleteById(numero);
    }

    private UtilizadorDTO toDTO(Utilizador u) {
        return new UtilizadorDTO(
                u.getNumero(),
                u.getNome(),
                u.getEmail(),
                u.getPerfil(),
                u.isAtivo()
        );
    }
}
