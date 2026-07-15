-- Esquema relacional PostgreSQL. Executado automaticamente no arranque da aplicacao.
CREATE TABLE IF NOT EXISTS candidatura (
    id BIGSERIAL PRIMARY KEY,
    nome_completo VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL,
    sexo VARCHAR(10) NOT NULL CHECK (sexo IN ('Masculino', 'Feminino')),
    nacionalidade VARCHAR(60) NOT NULL,
    bi_passaporte VARCHAR(20) NOT NULL,
    residencia_pais VARCHAR(60) NOT NULL,
    provincia VARCHAR(80) NOT NULL,
    email VARCHAR(254) NOT NULL,
    contacto_telefonico VARCHAR(9) NOT NULL CHECK (contacto_telefonico ~ '^9[0-9]{8}$'),
    nivel_escolaridade VARCHAR(40) NOT NULL,
    outra_area_estudo VARCHAR(120),
    curso_tecnico VARCHAR(120) NOT NULL,
    instituicao VARCHAR(120) NOT NULL,
    pais_formacao VARCHAR(60) NOT NULL,
    data_fim_curso DATE,
    outra_area_interesse VARCHAR(120),
    objectivos_profissionais TEXT NOT NULL,
    resumo_profissional TEXT NOT NULL,
    linkedin_url VARCHAR(500),
    portfolio_url VARCHAR(500),
    consentimento_aceite BOOLEAN NOT NULL CHECK (consentimento_aceite),
    assinatura_candidato VARCHAR(100) NOT NULL,
    data_assinatura DATE NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_candidatura_documento UNIQUE (bi_passaporte),
    CONSTRAINT ck_datas_candidatura CHECK (
        data_nascimento <= CURRENT_DATE
        AND (data_fim_curso IS NULL OR data_fim_curso >= data_nascimento)
        AND data_assinatura >= data_nascimento
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_candidatura_email_lower ON candidatura (LOWER(email));
CREATE INDEX IF NOT EXISTS ix_candidatura_nome_lower ON candidatura (LOWER(nome_completo));
CREATE INDEX IF NOT EXISTS ix_candidatura_criado_em ON candidatura (criado_em DESC);

-- Remove a restrição antiga caso tenha sido criada numa versão anterior.
ALTER TABLE candidatura DROP CONSTRAINT IF EXISTS ck_nivel_licenciatura;

CREATE TABLE IF NOT EXISTS area (
    id SMALLSERIAL PRIMARY KEY,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('ESTUDO', 'INTERESSE')),
    nome VARCHAR(100) NOT NULL,
    CONSTRAINT uq_area_tipo_nome UNIQUE (tipo, nome)
);

CREATE TABLE IF NOT EXISTS candidatura_area (
    candidatura_id BIGINT NOT NULL REFERENCES candidatura(id) ON DELETE CASCADE,
    area_id SMALLINT NOT NULL REFERENCES area(id),
    PRIMARY KEY (candidatura_id, area_id)
);

INSERT INTO area (tipo, nome) VALUES
    ('ESTUDO', 'Tecnologia e Engenharias'),
    ('ESTUDO', 'Economia, Gestao, Contabilidade e Financas'),
    ('ESTUDO', 'Ciencias e Saude'),
    ('ESTUDO', 'Arquitectura e Artes'),
    ('ESTUDO', 'Marketing, Comunicacao e Design'),
    ('ESTUDO', 'Ciencias Juridicas'),
    ('ESTUDO', 'Turismo'),
    ('ESTUDO', 'Agricultura e Recursos Naturais'),
    ('ESTUDO', 'Secretariado e Traducao'),
    ('ESTUDO', 'Outra'),
    ('INTERESSE', 'Banca Comercial'),
    ('INTERESSE', 'Logistica'),
    ('INTERESSE', 'Administrativa'),
    ('INTERESSE', 'Direito'),
    ('INTERESSE', 'Contabilidade'),
    ('INTERESSE', 'Tecnologias'),
    ('INTERESSE', 'Marketing'),
    ('INTERESSE', 'Auditoria'),
    ('INTERESSE', 'Gestao de Projectos'),
    ('INTERESSE', 'Recursos Humanos'),
    ('INTERESSE', 'Compliance'),
    ('INTERESSE', 'Outro')
ON CONFLICT (tipo, nome) DO NOTHING;
