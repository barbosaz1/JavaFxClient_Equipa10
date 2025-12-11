package gestaoeventos.controller;

import gestaoeventos.dto.LoginRequestDTO;
import gestaoeventos.dto.LoginResponseDTO;
import gestaoeventos.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO res = authService.login(dto);
        return ResponseEntity.ok(res);
    }
}

