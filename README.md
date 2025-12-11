# 🎓 Portal de Gestão de Eventos UPT

Sistema completo de gestão de eventos académicos para a Universidade Portucalense, desenvolvido em JavaFX com Spring Boot backend.

## 📋 Índice
- [Tecnologias](#tecnologias)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Perfis de Utilizador](#perfis-de-utilizador)
- [Execução](#execução)
- [Arquitetura](#arquitetura)

---
### 🧑‍💼 Administrador
- CRUD completo de utilizadores
- **Ativar/Desativar** utilizadores
- Gestão de locais
- Visualização de logs de auditoria
- Gestão de inscrições

### 📋 Gestor de Eventos
- **Criar eventos** diretamente no painel
- Gerir locais (visualização)
- Enviar anúncios broadcast
- Estatísticas de eventos

### 👨‍🏫 Docente
- **Criar eventos** próprios
- Registar presenças (check-in)
- **Emitir certificados** com nível superior
- Estatísticas dos seus eventos

### 🎓 Estudante
- Visualizar eventos disponíveis
- Inscrever-se em eventos
- Fazer check-in via QR Code
- Visualizar certificados recebidos

---

## 🛠️ Tecnologias

### Cliente (JavaFX)
- **JavaFX 21** - Interface gráfica
- **Jackson** - Serialização JSON
- **CSS** - UI inspirada em shadcn

### Backend (Spring Boot)
- **Spring Boot 3.x**
- **Spring Data JPA**
- **H2/PostgreSQL**
- **API REST**

---

## 📁 Estrutura do Projeto

```
gestaoeventos4/
├── src/main/java/gestaoeventos/
│   ├── client/                    # Cliente JavaFX
│   │   ├── controller/           # Controladores FXML
│   │   │   ├── AdminController.java
│   │   │   ├── DocenteController.java
│   │   │   ├── GestorController.java
│   │   │   └── ...
│   │   ├── service/              # Serviços de API
│   │   │   ├── ApiClient.java    # Base para todos os serviços
│   │   │   ├── EventoService.java
│   │   │   └── ...
│   │   ├── model/                # Modelos do cliente
│   │   │   └── UserSession.java  # Sessão do utilizador
│   │   └── util/                 # Utilitários
│   │       ├── ToastNotification.java  # Notificações
│   │       ├── EventoDialogHelper.java # Diálogos
│   │       └── PageNavigator.java      # Navegação
│   │
│   ├── controller/               # Controllers REST
│   ├── service/                  # Serviços do backend
│   ├── repository/               # Repositórios JPA
│   ├── entity/                   # Entidades JPA
│   ├── dto/                      # Data Transfer Objects
│   └── exception/                # Exceções customizadas
│
└── src/main/resources/
    ├── view/                     # Ficheiros FXML
    ├── css/                      # Estilos CSS
    │   └── app-theme.css         # Tema principal
    └── application.properties    # Configuração
```

---

## 👥 Perfis de Utilizador

| Perfil | Permissões |
|--------|------------|
| `ADMIN` | Acesso total ao sistema |
| `GESTOR_EVENTOS` | Gestão de eventos e locais |
| `DOCENTE` | Criar eventos, emitir certificados |
| `ESTUDANTE` | Participar em eventos |

---

## 📋 Pré-requisitos

- **Java 17+** - JDK instalado
- **Maven 3.8+** - Ou usar o Maven Wrapper incluído (`./mvnw`)
- **MySQL 8.0+** - Base de dados

---

## ⚙️ Configuração

1. **Clonar o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/gestaoeventos4.git
   cd gestaoeventos4
   ```

2. **Configurar a base de dados:**
   ```bash
   cp src/main/resources/application-example.properties src/main/resources/application.properties
   ```

3. **Editar `application.properties`** com as suas credenciais MySQL:
   ```properties
   spring.datasource.username=utilizador
   spring.datasource.password=password
   ```

4. **Criar a base de dados MySQL** (opcional - será criada automaticamente):
   ```sql
   CREATE DATABASE gestaoeventos;
   ```

---

## 🚀 Execução

### Backend (Spring Boot)

```bash
mvnw.cmd spring-boot:run
```

O servidor estará disponível em `http://localhost:8080`

### Cliente (JavaFX)

```bash
./mvnw compile
java -cp target/classes gestaoeventos.client.Launcher
```

---

## 🏗️ Arquitetura

### Sistema de Notificações
O sistema usa **ToastNotification** para exibir feedback visual:
- ✅ **Sucesso** - Verde
- ❌ **Erro** - Vermelho com código
- ⚠️ **Aviso** - Amarelo
- ℹ️ **Info** - Azul

### Certificados
Dois tipos de certificados com diferentes níveis de autoridade:
1. **PRESENCA** - Certificado básico automático
2. **DOCENTE** - Certificado emitido manualmente (maior valor)

### API REST
Base URL: `http://localhost:8080/api`

| Endpoint | Descrição |
|----------|-----------|
| `/auth/login` | Autenticação |
| `/utilizadores` | CRUD utilizadores |
| `/eventos` | CRUD eventos |
| `/inscricoes` | Gestão de inscrições |
| `/certificados` | Emissão de certificados |
| `/locais` | Gestão de locais |
| `/logs` | Logs de auditoria |

---

## 🎨 Interface

A interface foi desenhada com um tema **dark premium** moderno:
- Paleta de cores escuras 
- Gradientes roxos 
- Animações suaves em botões e transições
- Cards com sombras e bordas subtis

---

### Tratamento de Erros
Todos os erros são capturados e exibidos via ToastNotification.
O código de erro HTTP é incluído quando aplicável.

---

## 👨‍💻 Autor
Rodrigo Barbosa - 51770
Projeto desenvolvido para a disciplina de Laboratorio de Programação
**Universidade Portucalense - 2025**
