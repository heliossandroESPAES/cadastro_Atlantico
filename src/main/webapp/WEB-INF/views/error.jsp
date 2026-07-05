<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Ocorreu um problema"/>
<c:set var="pageKey" value="error"/>
<%@ include file="fragments/header.jspf" %>

<section class="error-page content-shell">
    <div class="error-code"><c:out value="${status}"/></div>
    <span class="section-kicker">Não foi possível continuar</span>
    <h1><c:out value="${errorTitle}"/></h1>
    <p><c:out value="${errorMessage}"/></p>
    <div class="error-actions">
        <a class="button" href="${pageContext.request.contextPath}/candidaturas">Voltar às candidaturas</a>
        <a class="button button-ghost" href="${pageContext.request.contextPath}/candidaturas/nova">Novo formulário</a>
    </div>
</section>

<%@ include file="fragments/footer.jspf" %>
