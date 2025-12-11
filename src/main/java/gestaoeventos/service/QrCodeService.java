package gestaoeventos.service;

import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class QrCodeService {

    public String gerarUrlQrCode(String conteudo) {
        String data = URLEncoder.encode(conteudo, StandardCharsets.UTF_8);
        return "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + data;
    }
}

