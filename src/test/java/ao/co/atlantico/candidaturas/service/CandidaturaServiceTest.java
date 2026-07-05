package ao.co.atlantico.candidaturas.service;

import ao.co.atlantico.candidaturas.dao.CandidaturaDAO;
import ao.co.atlantico.candidaturas.dao.ConnectionFactory;
import ao.co.atlantico.candidaturas.model.Candidatura;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CandidaturaServiceTest {

    private final CandidaturaService service = new CandidaturaService(
        new CandidaturaDAO(new ConnectionFactory())
    );

    @Test
    void aceitaCandidaturaValidaSemAcederAoBanco() {
        assertDoesNotThrow(() -> service.validar(validCandidate()));
    }

    @Test
    void rejeitaUrlComProtocoloPerigoso() {
        Candidatura candidatura = validCandidate();
        candidatura.setPortfolioUrl("javascript:alert(1)");

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> service.validar(candidatura)
        );

        assertTrue(exception.getErrors().containsKey("portfolioUrl"));
    }

    @Test
    void rejeitaDataFutura() {
        Candidatura candidatura = validCandidate();
        candidatura.setDataAssinatura(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> service.validar(candidatura)
        );

        assertEquals("A data nao pode ser futura.", exception.getErrors().get("dataAssinatura"));
    }

    @Test
    void rejeitaOpcaoForjadaForaDoCatalogo() {
        Candidatura candidatura = validCandidate();
        candidatura.setAreasInteresse(List.of("Administrador do sistema"));

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> service.validar(candidatura)
        );

        assertTrue(exception.getErrors().containsKey("areasInteresse"));
    }

    private Candidatura validCandidate() {
        Candidatura candidatura = new Candidatura();
        candidatura.setNomeCompleto("Ana Manuel");
        candidatura.setDataNascimento(LocalDate.of(1998, 5, 12));
        candidatura.setSexo("Feminino");
        candidatura.setNacionalidade("Angolana");
        candidatura.setBiPassaporte("123456789LA042");
        candidatura.setResidenciaPais("Angola");
        candidatura.setProvincia("Luanda");
        candidatura.setEmail("ana.manuel@example.com");
        candidatura.setContactoTelefonico("923456789");
        candidatura.setNivelEscolaridade("Licenciatura");
        candidatura.setAreasEstudo(List.of("Tecnologia e Engenharias"));
        candidatura.setCursoTecnico("Engenharia Informatica");
        candidatura.setInstituicao("Universidade Atlantica");
        candidatura.setPaisFormacao("Angola");
        candidatura.setDataFimCurso(LocalDate.of(2022, 11, 30));
        candidatura.setAreasInteresse(List.of("Tecnologias", "Auditoria"));
        candidatura.setObjectivosProfissionais("Desenvolver solucoes digitais seguras para a banca.");
        candidatura.setResumoProfissional("Engenheira informatica com experiencia em desenvolvimento web.");
        candidatura.setLinkedinUrl("https://www.linkedin.com/in/ana-manuel");
        candidatura.setPortfolioUrl("https://portfolio.example.com");
        candidatura.setConsentimentoAceite(true);
        candidatura.setAssinaturaCandidato("Ana Manuel");
        candidatura.setDataAssinatura(LocalDate.now());
        return candidatura;
    }
}
