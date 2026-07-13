<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Candidatura submetida"/>
<c:set var="pageKey" value="success"/>
<%@ include file="fragments/header.jspf" %>

<section class="success-page">
    <div class="success-panel">
        <span class="success-icon" aria-hidden="true">✓</span>
        <span class="eyebrow">Candidatura recebida</span>
        <h1>Submissão feita com sucesso.</h1>
        <p>O seu registo foi guardado no sistema. Guarde este número para referência:</p>
        <strong class="success-reference">#<c:out value="${candidateId}"/></strong>
        <div class="success-actions">
            <a class="button" href="${pageContext.request.contextPath}/candidaturas/nova">Submeter outra candidatura</a>
            <a class="button button-ghost" href="${pageContext.request.contextPath}/admin/login">Área do administrador</a>
        </div>
    </div>
</section>

<%@ include file="fragments/footer.jspf" %>
