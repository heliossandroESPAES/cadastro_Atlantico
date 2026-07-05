package ao.co.atlantico.candidaturas.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** JavaBean que representa uma candidatura profissional. */
public class Candidatura implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy 'as' HH:mm");

    private Long id;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private String sexo;
    private String nacionalidade;
    private String biPassaporte;
    private String residenciaPais;
    private String provincia;
    private String email;
    private String contactoTelefonico;
    private String nivelEscolaridade;
    private List<String> areasEstudo = new ArrayList<>();
    private String outraAreaEstudo;
    private String cursoTecnico;
    private String instituicao;
    private String paisFormacao;
    private LocalDate dataFimCurso;
    private List<String> areasInteresse = new ArrayList<>();
    private String outraAreaInteresse;
    private String objectivosProfissionais;
    private String resumoProfissional;
    private String linkedinUrl;
    private String portfolioUrl;
    private boolean consentimentoAceite;
    private String assinaturaCandidato;
    private LocalDate dataAssinatura;
    private OffsetDateTime criadoEm;

    public Candidatura() {
        // Construtor sem argumentos exigido pela convencao JavaBeans/JSP.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getBiPassaporte() {
        return biPassaporte;
    }

    public void setBiPassaporte(String biPassaporte) {
        this.biPassaporte = biPassaporte;
    }

    public String getResidenciaPais() {
        return residenciaPais;
    }

    public void setResidenciaPais(String residenciaPais) {
        this.residenciaPais = residenciaPais;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactoTelefonico() {
        return contactoTelefonico;
    }

    public void setContactoTelefonico(String contactoTelefonico) {
        this.contactoTelefonico = contactoTelefonico;
    }

    public String getNivelEscolaridade() {
        return nivelEscolaridade;
    }

    public void setNivelEscolaridade(String nivelEscolaridade) {
        this.nivelEscolaridade = nivelEscolaridade;
    }

    public List<String> getAreasEstudo() {
        return areasEstudo;
    }

    public void setAreasEstudo(List<String> areasEstudo) {
        this.areasEstudo = areasEstudo == null ? new ArrayList<>() : new ArrayList<>(areasEstudo);
    }

    public String getOutraAreaEstudo() {
        return outraAreaEstudo;
    }

    public void setOutraAreaEstudo(String outraAreaEstudo) {
        this.outraAreaEstudo = outraAreaEstudo;
    }

    public String getCursoTecnico() {
        return cursoTecnico;
    }

    public void setCursoTecnico(String cursoTecnico) {
        this.cursoTecnico = cursoTecnico;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getPaisFormacao() {
        return paisFormacao;
    }

    public void setPaisFormacao(String paisFormacao) {
        this.paisFormacao = paisFormacao;
    }

    public LocalDate getDataFimCurso() {
        return dataFimCurso;
    }

    public void setDataFimCurso(LocalDate dataFimCurso) {
        this.dataFimCurso = dataFimCurso;
    }

    public List<String> getAreasInteresse() {
        return areasInteresse;
    }

    public void setAreasInteresse(List<String> areasInteresse) {
        this.areasInteresse = areasInteresse == null ? new ArrayList<>() : new ArrayList<>(areasInteresse);
    }

    public String getOutraAreaInteresse() {
        return outraAreaInteresse;
    }

    public void setOutraAreaInteresse(String outraAreaInteresse) {
        this.outraAreaInteresse = outraAreaInteresse;
    }

    public String getObjectivosProfissionais() {
        return objectivosProfissionais;
    }

    public void setObjectivosProfissionais(String objectivosProfissionais) {
        this.objectivosProfissionais = objectivosProfissionais;
    }

    public String getResumoProfissional() {
        return resumoProfissional;
    }

    public void setResumoProfissional(String resumoProfissional) {
        this.resumoProfissional = resumoProfissional;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public boolean isConsentimentoAceite() {
        return consentimentoAceite;
    }

    public void setConsentimentoAceite(boolean consentimentoAceite) {
        this.consentimentoAceite = consentimentoAceite;
    }

    public String getAssinaturaCandidato() {
        return assinaturaCandidato;
    }

    public void setAssinaturaCandidato(String assinaturaCandidato) {
        this.assinaturaCandidato = assinaturaCandidato;
    }

    public LocalDate getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(LocalDate dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getDataNascimentoFormatada() {
        return format(dataNascimento);
    }

    public String getDataFimCursoFormatada() {
        return format(dataFimCurso);
    }

    public String getDataAssinaturaFormatada() {
        return format(dataAssinatura);
    }

    public String getCriadoEmFormatado() {
        return criadoEm == null ? "" : DATE_TIME_DISPLAY.format(criadoEm);
    }

    private String format(LocalDate date) {
        return date == null ? "" : DATE_DISPLAY.format(date);
    }
}
