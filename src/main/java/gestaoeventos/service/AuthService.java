package gestaoeventos.service;

import gestaoeventos.dto.LoginRequestDTO;
import gestaoeventos.dto.LoginResponseDTO;
import gestaoeventos.entity.Utilizador;
import gestaoeventos.exception.BusinessException;
import gestaoeventos.repository.UtilizadorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UtilizadorRepository utilizadorRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UtilizadorRepository repo, PasswordEncoder encoder) {
        this.utilizadorRepository = repo;
        this.passwordEncoder = encoder;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        Utilizador u = utilizadorRepository.findById(dto.getNumero())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas"));

        if (!u.isAtivo()) {
            throw new BusinessException("Utilizador inativo");
        }

        if (!passwordEncoder.matches(dto.getPassword(), u.getPasswordHash())) {
            throw new BusinessException("Credenciais inválidas");
        } 

        LoginResponseDTO res = new LoginResponseDTO();
        res.setNumero(u.getNumero());
        res.setNome(u.getNome());
        res.setEmail(u.getEmail());
        res.setPerfil(u.getPerfil());
        return res;
    }
}
