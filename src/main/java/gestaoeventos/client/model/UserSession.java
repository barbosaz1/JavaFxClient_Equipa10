package gestaoeventos.client.model;

import gestaoeventos.dto.LoginResponseDTO; // Reutiliza ou cria igual ao backend

public class UserSession {
    private static UserSession instance;
    private LoginResponseDTO user;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(LoginResponseDTO user) {
        this.user = user;
    }

    public void logout() {
        this.user = null;
    }

    public LoginResponseDTO getUser() {
        return user;
    }

    public boolean isLoggedIn() {
        return user != null;
    }
}