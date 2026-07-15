package ao.co.atlantico.candidaturas.web;

import ao.co.atlantico.candidaturas.model.Candidatura;
import ao.co.atlantico.candidaturas.service.CandidaturaService;
import ao.co.atlantico.candidaturas.service.ValidationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {

    public static final String ADMIN_SESSION_KEY = "adminAuthenticated";

    private static final String LOGIN_VIEW = "/WEB-INF/views/admin-login.jsp";
    private static final String LIST_VIEW = "/WEB-INF/views/candidatura-list.jsp";
    private static final String DETAIL_VIEW = "/WEB-INF/views/candidatura-detail.jsp";
    private static final String ERROR_VIEW = "/WEB-INF/views/error.jsp";
    private static final String ADMIN_CSRF_SESSION_KEY = "adminCsrfToken";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Logger LOGGER = Logger.getLogger(AdminServlet.class.getName());

    private CandidaturaService service;

    @Override
    public void init() throws ServletException {
        service = (CandidaturaService) getServletContext().getAttribute(AppContextListener.SERVICE_ATTRIBUTE);
        if (service == null) {
            throw new ServletException("O servico de candidaturas nao foi inicializado.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = Optional.ofNullable(request.getPathInfo()).orElse("/");
        switch (path) {
            case "/", "" -> response.sendRedirect(response.encodeRedirectURL(
                request.getContextPath() + "/admin/candidaturas"));
            case "/login" -> {
                if (isAuthenticated(request)) {
                    response.sendRedirect(response.encodeRedirectURL(
                        request.getContextPath() + "/admin/candidaturas"));
                } else {
                    showLogin(request, response, "", Map.of());
                }
            }
            case "/logout" -> logout(request, response);
            case "/candidaturas" -> {
                if (requireAdmin(request, response) && databaseAvailable(request, response)) {
                    showList(request, response);
                }
            }
            case "/candidaturas/detalhe" -> {
                if (requireAdmin(request, response) && databaseAvailable(request, response)) {
                    showDetail(request, response);
                }
            }
            default -> showError(request, response, HttpServletResponse.SC_NOT_FOUND,
                "Pagina nao encontrada", "O endereco administrativo solicitado nao existe.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = Optional.ofNullable(request.getPathInfo()).orElse("/");
        if ("/login".equals(path)) {
            login(request, response);
            return;
        }
        showError(request, response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
            "Operacao nao permitida", "Este endereco administrativo nao aceita o metodo utilizado.");
    }

    private void showLogin(HttpServletRequest request, HttpServletResponse response, String username,
            Map<String, String> errors) throws ServletException, IOException {
        request.setAttribute("username", username);
        request.setAttribute("errors", errors);
        request.setAttribute("loggedOut", "1".equals(request.getParameter("saiu")));
        request.setAttribute("adminCsrfToken", csrfToken(request.getSession()));
        request.getRequestDispatcher(LOGIN_VIEW).forward(request, response);
    }

    private void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = parameter(request, "username");
        String password = parameter(request, "password");
        Map<String, String> errors = new LinkedHashMap<>();

        if (!validCsrf(request)) {
            errors.put("login", "Sessao expirada. Actualize a pagina e tente novamente.");
        } else if (!matches(username, ADMIN_USERNAME) || !matches(password, ADMIN_PASSWORD)) {
            errors.put("login", "Credenciais invalidas. Use o utilizador admin e a senha admin.");
        }

        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            showLogin(request, response, username, errors);
            return;
        }

        request.changeSessionId();
        HttpSession session = request.getSession();
        session.setAttribute(ADMIN_SESSION_KEY, Boolean.TRUE);
        rotateCsrfToken(session);
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/candidaturas"));
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/login?saiu=1"));
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = parameter(request, "q");
        try {
            request.setAttribute("contagensAreasEstudo", service.contarCandidatosPorAreaEstudo());
            List<Candidatura> candidaturas = service.listar(search);
            request.setAttribute("candidaturas", candidaturas);
            request.setAttribute("q", search);
            request.getRequestDispatcher(LIST_VIEW).forward(request, response);
        } catch (ValidationException exception) {
            request.setAttribute("candidaturas", List.of());
            request.setAttribute("contagensAreasEstudo", Map.of());
            request.setAttribute("q", search);
            request.setAttribute("errors", exception.getErrors());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.getRequestDispatcher(LIST_VIEW).forward(request, response);
        } catch (SQLException exception) {
            databaseError(request, response, exception);
        }
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = positiveLong(request.getParameter("id"));
        if (id == null) {
            showError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                "Numero de registo invalido", "Informe um numero de candidatura maior do que zero.");
            return;
        }
        try {
            Optional<Candidatura> candidatura = service.consultar(id);
            if (candidatura.isEmpty()) {
                showError(request, response, HttpServletResponse.SC_NOT_FOUND,
                    "Candidatura nao encontrada", "Nao existe uma candidatura com o registo informado.");
                return;
            }
            request.setAttribute("candidatura", candidatura.get());
            request.getRequestDispatcher(DETAIL_VIEW).forward(request, response);
        } catch (SQLException exception) {
            databaseError(request, response, exception);
        }
    }

    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (isAuthenticated(request)) {
            return true;
        }
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/login"));
        return false;
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY));
    }

    private boolean matches(String received, String expected) {
        return MessageDigest.isEqual(
            received.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            expected.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    private String csrfToken(HttpSession session) {
        String token = (String) session.getAttribute(ADMIN_CSRF_SESSION_KEY);
        if (token == null) {
            token = rotateCsrfToken(session);
        }
        return token;
    }

    private String rotateCsrfToken(HttpSession session) {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(ADMIN_CSRF_SESSION_KEY, token);
        return token;
    }

    private boolean validCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String expected = (String) session.getAttribute(ADMIN_CSRF_SESSION_KEY);
        String received = request.getParameter("csrfToken");
        return expected != null && received != null
            && MessageDigest.isEqual(expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                received.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private Long positiveLong(String value) {
        try {
            long parsed = Long.parseLong(value == null ? "" : value.trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String parameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }

    private boolean databaseAvailable(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object error = getServletContext().getAttribute(AppContextListener.STARTUP_ERROR_ATTRIBUTE);
        if (error == null) {
            return true;
        }
        showError(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
            "Base de dados indisponivel", String.valueOf(error));
        return false;
    }

    private void databaseError(HttpServletRequest request, HttpServletResponse response, SQLException exception)
            throws ServletException, IOException {
        LOGGER.log(Level.SEVERE, "Erro ao aceder as candidaturas no painel admin.", exception);
        showError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Nao foi possivel concluir a operacao",
            "Ocorreu uma falha ao comunicar com a base de dados. Tente novamente.");
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, int status,
            String title, String message) throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("status", status);
        request.setAttribute("errorTitle", title);
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher(ERROR_VIEW).forward(request, response);
    }
}
