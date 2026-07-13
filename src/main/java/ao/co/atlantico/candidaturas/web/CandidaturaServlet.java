package ao.co.atlantico.candidaturas.web;

import ao.co.atlantico.candidaturas.model.Candidatura;
import ao.co.atlantico.candidaturas.service.CandidaturaOptions;
import ao.co.atlantico.candidaturas.service.CandidaturaService;
import ao.co.atlantico.candidaturas.service.ValidationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/candidaturas/*")
public class CandidaturaServlet extends HttpServlet {

    private static final String FORM_VIEW = "/WEB-INF/views/candidatura-form.jsp";
    private static final String SUCCESS_VIEW = "/WEB-INF/views/candidatura-success.jsp";
    private static final String ERROR_VIEW = "/WEB-INF/views/error.jsp";
    private static final String CSRF_SESSION_KEY = "csrfToken";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Logger LOGGER = Logger.getLogger(CandidaturaServlet.class.getName());

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
                request.getContextPath() + "/candidaturas/nova"));
            case "/nova" -> {
                if (databaseAvailable(request, response)) {
                    showForm(request, response, new Candidatura(), Map.of());
                }
            }
            case "/sucesso" -> showSuccess(request, response);
            default -> showError(request, response, HttpServletResponse.SC_NOT_FOUND,
                "Pagina nao encontrada", "O endereco solicitado nao existe nesta aplicacao.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!databaseAvailable(request, response)) {
            return;
        }

        String path = Optional.ofNullable(request.getPathInfo()).orElse("");
        if (!"/nova".equals(path)) {
            showError(request, response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Operacao nao permitida", "Este endereco nao aceita o metodo utilizado.");
            return;
        }
        submit(request, response);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Candidatura candidatura,
            Map<String, String> errors) throws ServletException, IOException {
        request.setAttribute("candidatura", candidatura);
        request.setAttribute("errors", errors);
        request.setAttribute("csrfToken", csrfToken(request.getSession()));
        request.setAttribute("nacionalidades", CandidaturaOptions.NACIONALIDADES);
        request.setAttribute("paises", CandidaturaOptions.PAISES);
        request.setAttribute("niveisEscolaridade", CandidaturaOptions.NIVEIS_ESCOLARIDADE);
        request.setAttribute("areasEstudo", CandidaturaOptions.AREAS_ESTUDO);
        request.setAttribute("areasInteresse", CandidaturaOptions.AREAS_INTERESSE);
        request.setAttribute("hoje", LocalDate.now());
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }

    private void showSuccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = positiveLong(request.getParameter("id"));
        if (id == null) {
            showError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                "Numero de registo invalido", "A confirmacao precisa de um numero de candidatura valido.");
            return;
        }
        request.setAttribute("candidateId", id);
        request.getRequestDispatcher(SUCCESS_VIEW).forward(request, response);
    }

    private void submit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Candidatura candidatura = bind(request);
        Map<String, String> bindingErrors = dateErrors(request);

        if (!validCsrf(request)) {
            showError(request, response, HttpServletResponse.SC_FORBIDDEN,
                "Sessao expirada", "Actualize a pagina e volte a submeter o formulario.");
            return;
        }

        if (!bindingErrors.isEmpty()) {
            try {
                service.validar(candidatura);
            } catch (ValidationException exception) {
                exception.getErrors().forEach(bindingErrors::putIfAbsent);
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            showForm(request, response, candidatura, bindingErrors);
            return;
        }

        try {
            long id = service.submeter(candidatura);
            rotateCsrfToken(request.getSession());
            String location = request.getContextPath() + "/candidaturas/sucesso?id=" + id;
            response.sendRedirect(response.encodeRedirectURL(location));
        } catch (ValidationException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            showForm(request, response, candidatura, exception.getErrors());
        } catch (SQLException exception) {
            databaseError(request, response, exception);
        }
    }

    private Candidatura bind(HttpServletRequest request) {
        Candidatura c = new Candidatura();
        c.setNomeCompleto(parameter(request, "nomeCompleto"));
        c.setDataNascimento(parseDate(request.getParameter("dataNascimento")));
        c.setSexo(parameter(request, "sexo"));
        c.setNacionalidade(parameter(request, "nacionalidade"));
        c.setBiPassaporte(parameter(request, "biPassaporte"));
        c.setResidenciaPais(parameter(request, "residenciaPais"));
        c.setProvincia(parameter(request, "provincia"));
        c.setEmail(parameter(request, "email"));
        c.setContactoTelefonico(parameter(request, "contactoTelefonico"));
        c.setNivelEscolaridade(parameter(request, "nivelEscolaridade"));
        c.setAreasEstudo(parameters(request, "areasEstudo"));
        c.setOutraAreaEstudo(parameter(request, "outraAreaEstudo"));
        c.setCursoTecnico(parameter(request, "cursoTecnico"));
        c.setInstituicao(parameter(request, "instituicao"));
        c.setPaisFormacao(parameter(request, "paisFormacao"));
        c.setDataFimCurso(parseDate(request.getParameter("dataFimCurso")));
        c.setAreasInteresse(parameters(request, "areasInteresse"));
        c.setOutraAreaInteresse(parameter(request, "outraAreaInteresse"));
        c.setObjectivosProfissionais(parameter(request, "objectivosProfissionais"));
        c.setResumoProfissional(parameter(request, "resumoProfissional"));
        c.setLinkedinUrl(parameter(request, "linkedinUrl"));
        c.setPortfolioUrl(parameter(request, "portfolioUrl"));
        c.setConsentimentoAceite("on".equals(request.getParameter("consentimentoAceite")));
        c.setAssinaturaCandidato(parameter(request, "assinaturaCandidato"));
        c.setDataAssinatura(parseDate(request.getParameter("dataAssinatura")));
        return c;
    }

    private Map<String, String> dateErrors(HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        invalidDate(request, errors, "dataNascimento");
        invalidDate(request, errors, "dataFimCurso");
        invalidDate(request, errors, "dataAssinatura");
        return errors;
    }

    private void invalidDate(HttpServletRequest request, Map<String, String> errors, String name) {
        String value = parameter(request, name);
        if (!value.isEmpty() && parseDate(value) == null) {
            errors.put(name, "Introduza uma data valida.");
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private List<String> parameters(HttpServletRequest request, String name) {
        String[] values = request.getParameterValues(name);
        return values == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(values));
    }

    private String parameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }

    private Long positiveLong(String value) {
        try {
            long parsed = Long.parseLong(value == null ? "" : value.trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private boolean validCsrf(HttpServletRequest request) {
        String expected = (String) request.getSession().getAttribute(CSRF_SESSION_KEY);
        String received = request.getParameter("csrfToken");
        return expected != null && received != null
            && java.security.MessageDigest.isEqual(expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                received.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private String csrfToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (token == null) {
            token = rotateCsrfToken(session);
        }
        return token;
    }

    private String rotateCsrfToken(HttpSession session) {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(CSRF_SESSION_KEY, token);
        return token;
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
        LOGGER.log(Level.SEVERE, "Erro ao aceder as candidaturas.", exception);
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
