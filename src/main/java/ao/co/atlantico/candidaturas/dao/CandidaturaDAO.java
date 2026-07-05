package ao.co.atlantico.candidaturas.dao;

import ao.co.atlantico.candidaturas.model.Candidatura;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class CandidaturaDAO {

    private static final String SELECT_BASE = """
        SELECT c.*,
               ARRAY(SELECT a.nome FROM candidatura_area ca
                     JOIN area a ON a.id = ca.area_id
                     WHERE ca.candidatura_id = c.id AND a.tipo = 'ESTUDO'
                     ORDER BY a.nome) AS areas_estudo,
               ARRAY(SELECT a.nome FROM candidatura_area ca
                     JOIN area a ON a.id = ca.area_id
                     WHERE ca.candidatura_id = c.id AND a.tipo = 'INTERESSE'
                     ORDER BY a.nome) AS areas_interesse
        FROM candidatura c
        """;

    private static final String INSERT_CANDIDATURA = """
        INSERT INTO candidatura (
            nome_completo, data_nascimento, sexo, nacionalidade, bi_passaporte,
            residencia_pais, provincia, email, contacto_telefonico, nivel_escolaridade,
            outra_area_estudo, curso_tecnico, instituicao, pais_formacao, data_fim_curso,
            outra_area_interesse, objectivos_profissionais, resumo_profissional,
            linkedin_url, portfolio_url, consentimento_aceite, assinatura_candidato,
            data_assinatura
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;

    private final ConnectionFactory connectionFactory;

    public CandidaturaDAO(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public long save(Candidatura candidatura) throws SQLException {
        try (Connection connection = connectionFactory.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long id = insertCandidatura(connection, candidatura);
                insertAreas(connection, id, "ESTUDO", candidatura.getAreasEstudo());
                insertAreas(connection, id, "INTERESSE", candidatura.getAreasInteresse());
                connection.commit();
                return id;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public Optional<Candidatura> findById(long id) throws SQLException {
        String sql = SELECT_BASE + " WHERE c.id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    public List<Candidatura> findAll(String searchTerm) throws SQLException {
        boolean searching = searchTerm != null && !searchTerm.isBlank();
        String sql = SELECT_BASE
            + (searching
                ? " WHERE POSITION(LOWER(?) IN LOWER(c.nome_completo)) > 0"
                    + " OR POSITION(LOWER(?) IN LOWER(c.bi_passaporte)) > 0"
                : "")
            + " ORDER BY c.criado_em DESC LIMIT 100";

        try (Connection connection = connectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            if (searching) {
                statement.setString(1, searchTerm.trim());
                statement.setString(2, searchTerm.trim());
            }
            try (ResultSet result = statement.executeQuery()) {
                List<Candidatura> candidaturas = new ArrayList<>();
                while (result.next()) {
                    candidaturas.add(map(result));
                }
                return candidaturas;
            }
        }
    }

    private long insertCandidatura(Connection connection, Candidatura c) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_CANDIDATURA)) {
            int index = 1;
            statement.setString(index++, c.getNomeCompleto());
            statement.setObject(index++, c.getDataNascimento());
            statement.setString(index++, c.getSexo());
            statement.setString(index++, c.getNacionalidade());
            statement.setString(index++, c.getBiPassaporte());
            statement.setString(index++, c.getResidenciaPais());
            statement.setString(index++, c.getProvincia());
            statement.setString(index++, c.getEmail());
            statement.setString(index++, c.getContactoTelefonico());
            statement.setString(index++, c.getNivelEscolaridade());
            setNullableString(statement, index++, c.getOutraAreaEstudo());
            statement.setString(index++, c.getCursoTecnico());
            statement.setString(index++, c.getInstituicao());
            statement.setString(index++, c.getPaisFormacao());
            statement.setObject(index++, c.getDataFimCurso(), Types.DATE);
            setNullableString(statement, index++, c.getOutraAreaInteresse());
            statement.setString(index++, c.getObjectivosProfissionais());
            statement.setString(index++, c.getResumoProfissional());
            setNullableString(statement, index++, c.getLinkedinUrl());
            setNullableString(statement, index++, c.getPortfolioUrl());
            statement.setBoolean(index++, c.isConsentimentoAceite());
            statement.setString(index++, c.getAssinaturaCandidato());
            statement.setObject(index, c.getDataAssinatura());

            try (ResultSet generated = statement.executeQuery()) {
                if (!generated.next()) {
                    throw new SQLException("A base de dados nao devolveu o identificador da candidatura.");
                }
                return generated.getLong(1);
            }
        }
    }

    private void insertAreas(Connection connection, long candidaturaId, String tipo, List<String> areas)
            throws SQLException {
        String sql = """
            INSERT INTO candidatura_area (candidatura_id, area_id)
            SELECT ?, id FROM area WHERE tipo = ? AND nome = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String area : areas) {
                statement.setLong(1, candidaturaId);
                statement.setString(2, tipo);
                statement.setString(3, area);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private Candidatura map(ResultSet result) throws SQLException {
        Candidatura c = new Candidatura();
        c.setId(result.getLong("id"));
        c.setNomeCompleto(result.getString("nome_completo"));
        c.setDataNascimento(result.getObject("data_nascimento", java.time.LocalDate.class));
        c.setSexo(result.getString("sexo"));
        c.setNacionalidade(result.getString("nacionalidade"));
        c.setBiPassaporte(result.getString("bi_passaporte"));
        c.setResidenciaPais(result.getString("residencia_pais"));
        c.setProvincia(result.getString("provincia"));
        c.setEmail(result.getString("email"));
        c.setContactoTelefonico(result.getString("contacto_telefonico"));
        c.setNivelEscolaridade(result.getString("nivel_escolaridade"));
        c.setOutraAreaEstudo(result.getString("outra_area_estudo"));
        c.setCursoTecnico(result.getString("curso_tecnico"));
        c.setInstituicao(result.getString("instituicao"));
        c.setPaisFormacao(result.getString("pais_formacao"));
        c.setDataFimCurso(result.getObject("data_fim_curso", java.time.LocalDate.class));
        c.setOutraAreaInteresse(result.getString("outra_area_interesse"));
        c.setObjectivosProfissionais(result.getString("objectivos_profissionais"));
        c.setResumoProfissional(result.getString("resumo_profissional"));
        c.setLinkedinUrl(result.getString("linkedin_url"));
        c.setPortfolioUrl(result.getString("portfolio_url"));
        c.setConsentimentoAceite(result.getBoolean("consentimento_aceite"));
        c.setAssinaturaCandidato(result.getString("assinatura_candidato"));
        c.setDataAssinatura(result.getObject("data_assinatura", java.time.LocalDate.class));
        c.setCriadoEm(result.getObject("criado_em", java.time.OffsetDateTime.class));
        c.setAreasEstudo(readArray(result.getArray("areas_estudo")));
        c.setAreasInteresse(readArray(result.getArray("areas_interesse")));
        return c;
    }

    private List<String> readArray(Array sqlArray) throws SQLException {
        if (sqlArray == null) {
            return List.of();
        }
        try {
            Object[] values = (Object[]) sqlArray.getArray();
            return Arrays.stream(values).map(String::valueOf).toList();
        } finally {
            sqlArray.free();
        }
    }

    private void setNullableString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

}
