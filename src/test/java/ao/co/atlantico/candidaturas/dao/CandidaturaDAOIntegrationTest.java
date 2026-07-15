package ao.co.atlantico.candidaturas.dao;

import ao.co.atlantico.candidaturas.model.Candidatura;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "TEST_DATABASE_URL", matches = ".+")
class CandidaturaDAOIntegrationTest {

    @Test
    void gravaEConsultaCandidaturaComRelacionamentos() throws Exception {
        System.setProperty("db.url", System.getenv("TEST_DATABASE_URL"));
        System.setProperty("db.user", System.getenv().getOrDefault("TEST_DATABASE_USER", "postgres"));
        System.setProperty("db.password", System.getenv().getOrDefault("TEST_DATABASE_PASSWORD", ""));

        ConnectionFactory connectionFactory = new ConnectionFactory();
        new DatabaseInitializer(connectionFactory).initialize();
        CandidaturaDAO dao = new CandidaturaDAO(connectionFactory);
        Candidatura candidatura = candidate();
        long id = 0;
        long nonLicensedId = 0;

        try {
            long before = dao.countCandidatesByStudyArea()
                .getOrDefault("Tecnologia e Engenharias", 0L);
            long savedId = dao.save(candidatura);
            id = savedId;

            Candidatura nonLicensed = candidate();
            nonLicensed.setNivelEscolaridade("Curso Tecnico");
            nonLicensedId = dao.save(nonLicensed);

            Candidatura saved = dao.findById(savedId).orElseThrow();
            assertEquals(candidatura.getNomeCompleto(), saved.getNomeCompleto());
            assertEquals(2, saved.getAreasInteresse().size());
            assertTrue(dao.findAll("Integracao JDBC").stream().anyMatch(item -> item.getId() == savedId));

            Map<String, Long> counts = dao.countCandidatesByStudyArea();
            assertTrue(counts.containsKey("Tecnologia e Engenharias"));
            assertEquals(before + 1L, counts.get("Tecnologia e Engenharias"));

        } finally {
            if (id > 0) {
                try (var connection = connectionFactory.getConnection();
                     PreparedStatement statement = connection.prepareStatement("DELETE FROM candidatura WHERE id = ?")) {
                    statement.setLong(1, id);
                    statement.executeUpdate();
                }
            }
            if (nonLicensedId > 0) {
                try (var connection = connectionFactory.getConnection();
                     PreparedStatement statement = connection.prepareStatement("DELETE FROM candidatura WHERE id = ?")) {
                    statement.setLong(1, nonLicensedId);
                    statement.executeUpdate();
                }
            }
        }
    }

    private Candidatura candidate() {
        String unique = String.valueOf(System.nanoTime());
        Candidatura c = new Candidatura();
        c.setNomeCompleto("Teste Integracao JDBC");
        c.setDataNascimento(LocalDate.of(1995, 2, 10));
        c.setSexo("Masculino");
        c.setNacionalidade("Angolana");
        c.setBiPassaporte("T" + unique.substring(Math.max(0, unique.length() - 12)));
        c.setResidenciaPais("Angola");
        c.setProvincia("Luanda");
        c.setEmail("jdbc-" + unique + "@example.com");
        c.setContactoTelefonico("923456789");
        c.setNivelEscolaridade("Licenciatura");
        c.setAreasEstudo(List.of("Tecnologia e Engenharias"));
        c.setCursoTecnico("Engenharia Informatica");
        c.setInstituicao("Universidade de Teste");
        c.setPaisFormacao("Angola");
        c.setDataFimCurso(LocalDate.of(2020, 12, 1));
        c.setAreasInteresse(List.of("Tecnologias", "Auditoria"));
        c.setObjectivosProfissionais("Validar a camada DAO.");
        c.setResumoProfissional("Candidato criado por teste de integracao.");
        c.setConsentimentoAceite(true);
        c.setAssinaturaCandidato("Teste Integracao JDBC");
        c.setDataAssinatura(LocalDate.now());
        return c;
    }
}
