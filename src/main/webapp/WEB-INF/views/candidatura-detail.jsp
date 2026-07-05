<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Detalhe da candidatura"/>
<c:set var="pageKey" value="detail"/>
<%@ include file="fragments/header.jspf" %>

<c:if test="${success}">
    <div class="toast success-toast" role="status">
        <span aria-hidden="true">✓</span>
        <div><strong>Candidatura submetida com sucesso.</strong><p>O registo foi guardado no PostgreSQL.</p></div>
    </div>
</c:if>

<section class="detail-hero">
    <div class="content-shell detail-hero-inner">
        <div class="identity-large">
            <span class="avatar avatar-large" aria-hidden="true"><c:out value="${candidatura.nomeCompleto.substring(0, 1)}"/></span>
            <div>
                <span class="record-pill">Candidatura #<c:out value="${candidatura.id}"/></span>
                <h1><c:out value="${candidatura.nomeCompleto}"/></h1>
                <p><c:out value="${candidatura.nivelEscolaridade}"/> · <c:out value="${candidatura.provincia}"/>, <c:out value="${candidatura.residenciaPais}"/></p>
            </div>
        </div>
        <div class="detail-actions">
            <a class="button button-light" href="${pageContext.request.contextPath}/candidaturas">← Voltar à lista</a>
            <a class="button" href="${pageContext.request.contextPath}/candidaturas/nova">Nova candidatura</a>
        </div>
    </div>
</section>

<div class="content-shell detail-layout">
    <div class="detail-main">
        <section class="detail-card">
            <div class="card-heading"><span>01</span><div><small>Perfil</small><h2>Dados pessoais</h2></div></div>
            <dl class="data-grid">
                <div><dt>Nome completo</dt><dd><c:out value="${candidatura.nomeCompleto}"/></dd></div>
                <div><dt>Data de nascimento</dt><dd><c:out value="${candidatura.dataNascimentoFormatada}"/></dd></div>
                <div><dt>Sexo</dt><dd><c:out value="${candidatura.sexo}"/></dd></div>
                <div><dt>Nacionalidade</dt><dd><c:out value="${candidatura.nacionalidade}"/></dd></div>
                <div><dt>B.I. / Passaporte</dt><dd><c:out value="${candidatura.biPassaporte}"/></dd></div>
                <div><dt>Residência</dt><dd><c:out value="${candidatura.provincia}"/>, <c:out value="${candidatura.residenciaPais}"/></dd></div>
                <div><dt>E-mail</dt><dd><c:out value="${candidatura.email}"/></dd></div>
                <div><dt>Contacto</dt><dd><c:out value="${candidatura.contactoTelefonico}"/></dd></div>
            </dl>
        </section>

        <section class="detail-card">
            <div class="card-heading"><span>02</span><div><small>Formação</small><h2>Habilitações académicas</h2></div></div>
            <dl class="data-grid">
                <div><dt>Nível</dt><dd><c:out value="${candidatura.nivelEscolaridade}"/></dd></div>
                <div><dt>Curso</dt><dd><c:out value="${candidatura.cursoTecnico}"/></dd></div>
                <div><dt>Instituição</dt><dd><c:out value="${candidatura.instituicao}"/></dd></div>
                <div><dt>País da formação</dt><dd><c:out value="${candidatura.paisFormacao}"/></dd></div>
                <div><dt>Conclusão</dt><dd><c:out value="${empty candidatura.dataFimCursoFormatada ? 'Não informada' : candidatura.dataFimCursoFormatada}"/></dd></div>
            </dl>
            <h3 class="mini-title">Áreas de estudo</h3>
            <div class="tag-list">
                <c:forEach items="${candidatura.areasEstudo}" var="area"><span class="tag"><c:out value="${area}"/></span></c:forEach>
                <c:if test="${not empty candidatura.outraAreaEstudo}"><span class="tag tag-accent"><c:out value="${candidatura.outraAreaEstudo}"/></span></c:if>
            </div>
        </section>

        <section class="detail-card">
            <div class="card-heading"><span>03</span><div><small>Ambição</small><h2>Perfil profissional</h2></div></div>
            <h3 class="mini-title">Áreas de interesse</h3>
            <div class="tag-list">
                <c:forEach items="${candidatura.areasInteresse}" var="area"><span class="tag"><c:out value="${area}"/></span></c:forEach>
                <c:if test="${not empty candidatura.outraAreaInteresse}"><span class="tag tag-accent"><c:out value="${candidatura.outraAreaInteresse}"/></span></c:if>
            </div>
            <div class="narrative"><h3>Objectivos profissionais</h3><p><c:out value="${candidatura.objectivosProfissionais}"/></p></div>
            <div class="narrative"><h3>Resumo profissional</h3><p><c:out value="${candidatura.resumoProfissional}"/></p></div>
        </section>
    </div>

    <aside class="detail-aside">
        <section class="aside-card">
            <span class="status-dot">Candidatura recebida</span>
            <h2>Registo #<c:out value="${candidatura.id}"/></h2>
            <p>Submetida em <strong><c:out value="${candidatura.criadoEmFormatado}"/></strong></p>
        </section>
        <section class="aside-card">
            <h2>Presença digital</h2>
            <c:choose>
                <c:when test="${empty candidatura.linkedinUrl and empty candidatura.portfolioUrl}"><p>Nenhum URL profissional informado.</p></c:when>
                <c:otherwise>
                    <c:if test="${not empty candidatura.linkedinUrl}"><a class="external-link" target="_blank" rel="noopener noreferrer" href="<c:out value='${candidatura.linkedinUrl}'/>">LinkedIn <span>↗</span></a></c:if>
                    <c:if test="${not empty candidatura.portfolioUrl}"><a class="external-link" target="_blank" rel="noopener noreferrer" href="<c:out value='${candidatura.portfolioUrl}'/>">Portfólio <span>↗</span></a></c:if>
                </c:otherwise>
            </c:choose>
        </section>
        <section class="aside-card consent-card">
            <span aria-hidden="true">✓</span>
            <div><h2>Consentimento confirmado</h2><p>Assinado por <c:out value="${candidatura.assinaturaCandidato}"/> em <c:out value="${candidatura.dataAssinaturaFormatada}"/>.</p></div>
        </section>
    </aside>
</div>

<%@ include file="fragments/footer.jspf" %>
