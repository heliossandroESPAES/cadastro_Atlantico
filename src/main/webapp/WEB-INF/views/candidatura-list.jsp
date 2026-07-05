<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Candidaturas"/>
<c:set var="pageKey" value="list"/>
<%@ include file="fragments/header.jspf" %>

<section class="hero hero-compact">
    <div class="hero-content">
        <span class="eyebrow">Portal de recrutamento</span>
        <h1>Encontre o próximo talento.</h1>
        <p>Consulte candidaturas com rapidez, segurança e informação organizada.</p>
    </div>
    <div class="hero-stat" aria-label="Total apresentado">
        <strong><c:out value="${candidaturas.size()}"/></strong>
        <span>registos apresentados</span>
    </div>
</section>

<section class="content-shell list-section" aria-labelledby="lista-title">
    <div class="section-heading split-heading">
        <div>
            <span class="section-kicker">Gestão de candidaturas</span>
            <h2 id="lista-title">Candidatos registados</h2>
        </div>
        <a class="button" href="${pageContext.request.contextPath}/candidaturas/nova">+ Nova candidatura</a>
    </div>

    <form class="search-bar" method="get" action="${pageContext.request.contextPath}/candidaturas" role="search">
        <label for="q">Pesquisar por nome ou B.I. / Passaporte</label>
        <div class="search-controls">
            <input id="q" name="q" type="search" maxlength="100"
                   value="<c:out value='${q}'/>" placeholder="Ex.: Ana Manuel ou 123456789LA...">
            <button class="button" type="submit">Pesquisar</button>
            <c:if test="${not empty q}">
                <a class="button button-ghost" href="${pageContext.request.contextPath}/candidaturas">Limpar</a>
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
                <a class="button" href="${pageContext.request.contextPath}/candidaturas/nova">Criar candidatura</a>
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
                        <a class="card-link" href="${pageContext.request.contextPath}/candidaturas/detalhe?id=${item.id}">
                            Ver candidatura completa <span aria-hidden="true">→</span>
                        </a>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="fragments/footer.jspf" %>
