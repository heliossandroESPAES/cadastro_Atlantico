package ao.co.atlantico.candidaturas.service;

import ao.co.atlantico.candidaturas.dao.CandidaturaDAO;
import ao.co.atlantico.candidaturas.model.Candidatura;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public final class CandidaturaService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?"
            + "(?:\\.[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?)+$",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^9\\d{8}$");
    private static final Pattern PERSON_NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L} .'-]{2,99}$");
    private static final Pattern PLACE_NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,79}$");
    private static final Pattern DOCUMENT_PATTERN = Pattern.compile("^(?=.*[A-Za-z0-9])[A-Za-z0-9/-]{6,20}$");

    private final CandidaturaDAO dao;

    public CandidaturaService(CandidaturaDAO dao) {
        this.dao = dao;
    }

    public long submeter(Candidatura candidatura) throws ValidationException, SQLException {
        normalize(candidatura);
        validar(candidatura);
        try {
            return dao.save(candidatura);
        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                Map<String, String> errors = new LinkedHashMap<>();
                errors.put("biPassaporte", "Ja existe uma candidatura com este B.I. / Passaporte ou e-mail.");
                errors.put("email", "Use dados que ainda nao estejam associados a outra candidatura.");
                throw new ValidationException(errors);
            }
            throw exception;
        }
    }

    public Optional<Candidatura> consultar(long id) throws SQLException {
        return id > 0 ? dao.findById(id) : Optional.empty();
    }

    public List<Candidatura> listar(String termo) throws ValidationException, SQLException {
        String normalized = clean(termo);
        if (normalized.length() > 100 || containsControlCharacter(normalized)) {
            throw singleError("pesquisa", "A pesquisa deve ter no maximo 100 caracteres validos.");
        }
        return dao.findAll(normalized);
    }

    public void validar(Candidatura c) throws ValidationException {
        Map<String, String> errors = new LinkedHashMap<>();

        required(errors, "nomeCompleto", c.getNomeCompleto(), "Informe o nome completo.");
        requiredDate(errors, "dataNascimento", c.getDataNascimento(), "Informe a data de nascimento.");
        option(errors, "sexo", c.getSexo(), CandidaturaOptions.SEXOS, "Seleccione o sexo.");
        option(errors, "nacionalidade", c.getNacionalidade(), Set.copyOf(CandidaturaOptions.NACIONALIDADES),
            "Seleccione uma nacionalidade valida.");
        required(errors, "biPassaporte", c.getBiPassaporte(), "Informe o B.I. ou Passaporte.");
        option(errors, "residenciaPais", c.getResidenciaPais(), Set.copyOf(CandidaturaOptions.PAISES),
            "Seleccione um pais de residencia valido.");
        if (!CandidaturaOptions.provinciaValida(c.getResidenciaPais(), c.getProvincia())) {
            errors.put("provincia", "Seleccione uma provincia valida para o pais de residencia.");
        }
        required(errors, "email", c.getEmail(), "Informe o e-mail.");
        required(errors, "contactoTelefonico", c.getContactoTelefonico(), "Informe o contacto telefonico.");
        option(errors, "nivelEscolaridade", c.getNivelEscolaridade(),
            Set.copyOf(CandidaturaOptions.NIVEIS_ESCOLARIDADE), "Seleccione o nivel de escolaridade.");
        validateAreas(errors, "areasEstudo", c.getAreasEstudo(), CandidaturaOptions.AREAS_ESTUDO,
            "Seleccione pelo menos uma area de estudo.");
        required(errors, "cursoTecnico", c.getCursoTecnico(), "Informe o curso.");
        required(errors, "instituicao", c.getInstituicao(), "Informe a instituicao.");
        option(errors, "paisFormacao", c.getPaisFormacao(), Set.copyOf(CandidaturaOptions.PAISES),
            "Seleccione o pais da formacao.");
        validateAreas(errors, "areasInteresse", c.getAreasInteresse(), CandidaturaOptions.AREAS_INTERESSE,
            "Seleccione pelo menos uma area de interesse.");
        required(errors, "objectivosProfissionais", c.getObjectivosProfissionais(),
            "Descreva os seus objectivos profissionais.");
        required(errors, "resumoProfissional", c.getResumoProfissional(), "Informe o resumo profissional.");
        required(errors, "assinaturaCandidato", c.getAssinaturaCandidato(), "Informe a assinatura do candidato.");
        requiredDate(errors, "dataAssinatura", c.getDataAssinatura(), "Informe a data de assinatura.");

        if (hasText(c.getNomeCompleto()) && !PERSON_NAME_PATTERN.matcher(c.getNomeCompleto()).matches()) {
            errors.put("nomeCompleto", "O nome deve conter apenas letras e ter entre 3 e 100 caracteres.");
        }
        if (hasText(c.getAssinaturaCandidato())
                && !PERSON_NAME_PATTERN.matcher(c.getAssinaturaCandidato()).matches()) {
            errors.put("assinaturaCandidato", "A assinatura deve conter apenas letras.");
        }
        place(errors, "nacionalidade", c.getNacionalidade());
        place(errors, "residenciaPais", c.getResidenciaPais());
        place(errors, "provincia", c.getProvincia());
        place(errors, "paisFormacao", c.getPaisFormacao());

        if (hasText(c.getBiPassaporte()) && !DOCUMENT_PATTERN.matcher(c.getBiPassaporte()).matches()) {
            errors.put("biPassaporte", "Use 6 a 20 letras/numeros; '/' e '-' tambem sao permitidos.");
        }
        if (hasText(c.getEmail())
                && (c.getEmail().length() > 254 || !EMAIL_PATTERN.matcher(c.getEmail()).matches())) {
            errors.put("email", "Introduza um endereco de e-mail valido.");
        }
        if (hasText(c.getContactoTelefonico()) && !PHONE_PATTERN.matcher(c.getContactoTelefonico()).matches()) {
            errors.put("contactoTelefonico", "Use 9 digitos e comece o numero por 9.");
        }

        maxLength(errors, "cursoTecnico", c.getCursoTecnico(), 120);
        maxLength(errors, "instituicao", c.getInstituicao(), 120);
        maxLength(errors, "outraAreaEstudo", c.getOutraAreaEstudo(), 120);
        maxLength(errors, "outraAreaInteresse", c.getOutraAreaInteresse(), 120);
        maxLength(errors, "objectivosProfissionais", c.getObjectivosProfissionais(), 2_000);
        maxLength(errors, "resumoProfissional", c.getResumoProfissional(), 5_000);

        if (c.getAreasEstudo().contains("Outra") && !hasText(c.getOutraAreaEstudo())) {
            errors.put("outraAreaEstudo", "Especifique a outra area de estudo.");
        }
        if (c.getAreasInteresse().contains("Outro") && !hasText(c.getOutraAreaInteresse())) {
            errors.put("outraAreaInteresse", "Especifique a outra area de interesse.");
        }

        dateNotFuture(errors, "dataNascimento", c.getDataNascimento());
        dateNotFuture(errors, "dataFimCurso", c.getDataFimCurso());
        dateNotFuture(errors, "dataAssinatura", c.getDataAssinatura());
        if (c.getDataNascimento() != null && c.getDataFimCurso() != null
                && c.getDataFimCurso().isBefore(c.getDataNascimento())) {
            errors.put("dataFimCurso", "A conclusao do curso nao pode ser anterior ao nascimento.");
        }
        if (c.getDataNascimento() != null && c.getDataAssinatura() != null
                && c.getDataAssinatura().isBefore(c.getDataNascimento())) {
            errors.put("dataAssinatura", "A assinatura nao pode ser anterior ao nascimento.");
        }

        validUrl(errors, "linkedinUrl", c.getLinkedinUrl());
        validUrl(errors, "portfolioUrl", c.getPortfolioUrl());

        if (!c.isConsentimentoAceite()) {
            errors.put("consentimentoAceite", "Aceite os termos para submeter a candidatura.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void normalize(Candidatura c) {
        c.setNomeCompleto(clean(c.getNomeCompleto()));
        c.setSexo(clean(c.getSexo()));
        c.setNacionalidade(clean(c.getNacionalidade()));
        c.setBiPassaporte(clean(c.getBiPassaporte()).toUpperCase(Locale.ROOT));
        c.setResidenciaPais(clean(c.getResidenciaPais()));
        c.setProvincia(clean(c.getProvincia()));
        c.setEmail(clean(c.getEmail()).toLowerCase(Locale.ROOT));
        c.setContactoTelefonico(clean(c.getContactoTelefonico()));
        c.setNivelEscolaridade(clean(c.getNivelEscolaridade()));
        c.setAreasEstudo(distinct(c.getAreasEstudo()));
        c.setOutraAreaEstudo(clean(c.getOutraAreaEstudo()));
        c.setCursoTecnico(clean(c.getCursoTecnico()));
        c.setInstituicao(clean(c.getInstituicao()));
        c.setPaisFormacao(clean(c.getPaisFormacao()));
        c.setAreasInteresse(distinct(c.getAreasInteresse()));
        c.setOutraAreaInteresse(clean(c.getOutraAreaInteresse()));
        c.setObjectivosProfissionais(clean(c.getObjectivosProfissionais()));
        c.setResumoProfissional(clean(c.getResumoProfissional()));
        c.setLinkedinUrl(clean(c.getLinkedinUrl()));
        c.setPortfolioUrl(clean(c.getPortfolioUrl()));
        c.setAssinaturaCandidato(clean(c.getAssinaturaCandidato()));
    }

    private List<String> distinct(List<String> values) {
        if (values == null) {
            return List.of();
        }
        List<String> cleaned = new ArrayList<>();
        for (String value : values) {
            String normalized = clean(value);
            if (!normalized.isEmpty()) {
                cleaned.add(normalized);
            }
        }
        return new ArrayList<>(new LinkedHashSet<>(cleaned));
    }

    private void validateAreas(Map<String, String> errors, String field, List<String> selected,
            List<String> allowed, String requiredMessage) {
        if (selected == null || selected.isEmpty()) {
            errors.put(field, requiredMessage);
        } else if (!Set.copyOf(allowed).containsAll(selected)) {
            errors.put(field, "Foi recebida uma opcao que nao pertence a lista permitida.");
        }
    }

    private void required(Map<String, String> errors, String field, String value, String message) {
        if (!hasText(value)) {
            errors.put(field, message);
        }
    }

    private void requiredDate(Map<String, String> errors, String field, LocalDate value, String message) {
        if (value == null) {
            errors.put(field, message);
        }
    }

    private void option(Map<String, String> errors, String field, String value, Set<String> options,
            String message) {
        if (!options.contains(value)) {
            errors.put(field, message);
        }
    }

    private void place(Map<String, String> errors, String field, String value) {
        if (hasText(value) && !PLACE_NAME_PATTERN.matcher(value).matches()) {
            errors.put(field, "Use apenas letras, espacos, apostrofos, pontos ou hifens.");
        }
    }

    private void maxLength(Map<String, String> errors, String field, String value, int maximum) {
        if (value != null && value.length() > maximum) {
            errors.put(field, "O campo nao pode exceder " + maximum + " caracteres.");
        }
    }

    private void dateNotFuture(Map<String, String> errors, String field, LocalDate date) {
        if (date != null && date.isAfter(LocalDate.now())) {
            errors.put(field, "A data nao pode ser futura.");
        }
    }

    private void validUrl(Map<String, String> errors, String field, String value) {
        if (!hasText(value)) {
            return;
        }
        if (value.length() > 500 || containsControlCharacter(value)) {
            errors.put(field, "O URL e demasiado longo ou contem caracteres invalidos.");
            return;
        }
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            boolean http = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
            if (!http || uri.getHost() == null || uri.getHost().isBlank() || uri.getUserInfo() != null) {
                errors.put(field, "Use um URL completo e seguro, iniciado por http:// ou https://.");
            }
        } catch (URISyntaxException exception) {
            errors.put(field, "Introduza um URL valido.");
        }
    }

    private ValidationException singleError(String field, String message) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put(field, message);
        return new ValidationException(errors);
    }

    private boolean containsControlCharacter(String value) {
        return value.chars().anyMatch(Character::isISOControl);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
