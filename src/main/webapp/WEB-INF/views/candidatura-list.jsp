<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Painel administrativo"/>
<c:set var="pageKey" value="adminList"/>
<%@ include file="fragments/header.jspf" %>

<section class="hero hero-compact">
    <div class="hero-content">
        <span class="eyebrow">Painel administrativo</span>
        <h1>Gestão de candidaturas.</h1>
        <p>Área reservada para consultar, pesquisar e acompanhar os candidatos registados.</p>
    </div>
    <div class="hero-stat" aria-label="Total apresentado">
        <strong><c:out value="${candidaturas.size()}"/></strong>
        <span>registos apresentados</span>
    </div>
</section>

<section class="content-shell area-summary" aria-labelledby="areas-title">
    <div class="section-heading">
        <span class="section-kicker">Distribuição académica</span>
        <h2 id="areas-title">Candidatos por área de licenciatura</h2>
        <p class="section-description">Quantidade de candidatos em cada área de estudo selecionada no formulário.</p>
    </div>
    <div class="area-count-grid">
        <c:forEach items="${contagensAreasEstudo}" var="areaCount">
            <article class="area-count-card">
                <div class="area-count-topline">
                    <span class="area-count-icon" aria-hidden="true">A</span>
                    <div class="area-count-number">
                        <strong><c:out value="${areaCount.value}"/></strong>
                        <span>candidatos</span>
                    </div>
                </div>
                <h3><c:out value="${areaCount.key}"/></h3>
            </article>
        </c:forEach>
    </div>
</section>

<section class="content-shell list-section" aria-labelledby="lista-title">
    <div class="section-heading split-heading">
        <div>
            <span class="section-kicker">Admin · candidatos registados</span>
            <h2 id="lista-title">Candidatos registados</h2>
        </div>
        <a class="button" href="${pageContext.request.contextPath}/candidaturas/nova">Abrir formulário público</a>
    </div>

    <form class="search-bar" method="get" action="${pageContext.request.contextPath}/admin/candidaturas" role="search">
        <label for="q">Pesquisar por nome, B.I. / Passaporte, e-mail ou telefone</label>
        <div class="search-controls">
            <input id="q" name="q" type="search" maxlength="100"
                   value="<c:out value='${q}'/>" placeholder="Ex.: Ana Manuel, 123456789LA, email ou 923...">
            <button class="button" type="submit">Pesquisar</button>
            <c:if test="${not empty q}">
                <a class="button button-ghost" href="${pageContext.request.contextPath}/admin/candidaturas">Limpar</a>
            </c:if>
        </div>
        <c:if test="${not empty errors.pesquisa}">
            <span class="field-error"><c:out value="${errors.pesquisa}"/></span>
        </c:if>
    </form>

    <c:choose>
        <c:when test="${empty candidaturas}">
            <div class="empty-state">
                <span class="empty-icon" aria-hidden="true">⌕</span>
                <h3>Nenhuma candidatura encontrada</h3>
                <p><c:choose><c:when test="${not empty q}">Experimente outro termo de pesquisa.</c:when><c:otherwise>Registe a primeira candidatura para começar.</c:otherwise></c:choose></p>
                <a class="button" href="${pageContext.request.contextPath}/candidaturas/nova">Abrir formulário público</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="candidate-grid">
                <c:forEach items="${candidaturas}" var="item">
                    <article class="candidate-card">
                        <div class="candidate-topline">
                            <span class="record-pill">Registo #<c:out value="${item.id}"/></span>
                            <span class="date-copy"><c:out value="${item.criadoEmFormatado}"/></span>
                        </div>
                        <div class="candidate-identity">
                            <span class="avatar" aria-hidden="true"><c:out value="${item.nomeCompleto.substring(0, 1)}"/></span>
                            <div>
                                <h3><c:out value="${item.nomeCompleto}"/></h3>
                                <p><c:out value="${item.nivelEscolaridade}"/> · <c:out value="${item.provincia}"/></p>
                            </div>
                        </div>
                        <div class="tag-list" aria-label="Áreas de interesse">
                            <c:forEach items="${item.areasInteresse}" var="area" end="2">
                                <span class="tag"><c:out value="${area}"/></span>
                            </c:forEach>
                        </div>
                        <div class="candidate-meta">
                            <span><strong>Contacto</strong><c:out value="${item.contactoTelefonico}"/></span>
                            <span><strong>E-mail</strong><c:out value="${item.email}"/></span>
                        </div>
                        <a class="card-link" href="${pageContext.request.contextPath}/admin/candidaturas/detalhe?id=${item.id}">
                            Ver candidatura completa <span aria-hidden="true">→</span>
                        </a>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="fragments/footer.jspf" %>
