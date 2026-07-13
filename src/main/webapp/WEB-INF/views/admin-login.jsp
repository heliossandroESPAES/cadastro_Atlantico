<%@ page contentType="text/html;charset=UTF-8" %> <%@ taglib prefix="c"
uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login administrativo" />
<c:set var="pageKey" value="adminLogin" />
<%@ include file="fragments/header.jspf" %>

<section class="login-page">
  <div class="login-copy">
    <span class="eyebrow">Área reservada</span>
    <h1>Painel do administrador</h1>
    <p>
      Entre para consultar candidaturas, pesquisar candidatos e abrir o detalhe
      completo dos registos.
    </p>
    <div class="role-note">
      <strong>Candidato?</strong>
      <span
        >Não precisa de login. Use o formulário público para submeter a
        candidatura.</span
      >
      <a href="${pageContext.request.contextPath}/candidaturas/nova"
        >Ir ao formulário</a
      >
    </div>
  </div>

  <form
    class="login-card"
    method="post"
    action="${pageContext.request.contextPath}/admin/login"
  >
    <input
      type="hidden"
      name="csrfToken"
      value="<c:out value='${adminCsrfToken}'/>"
    />
    <span class="section-kicker">Login</span>
    <h2>Acesso administrativo</h2>
    <!-- <p class="login-help">Credenciais da defesa: <strong>admin</strong> / <strong>admin</strong></p> -->
    <c:if test="${not empty errors.login}">
      <div class="alert alert-error" role="alert">
        <strong><c:out value="${errors.login}" /></strong>
      </div>
    </c:if>

    <div class="field">
      <label for="username">Utilizador</label>
      <input
        id="username"
        name="username"
        type="text"
        required
        maxlength="30"
        autocomplete="username"
        value="<c:out value='${username}'/>"
      />
    </div>
    <div class="field">
      <label for="password">Senha</label>
      <input
        id="password"
        name="password"
        type="password"
        required
        maxlength="30"
        autocomplete="current-password"
      />
    </div>
    <button class="button button-large" type="submit">
      Entrar no painel admin
    </button>
  </form>
</section>

<%@ include file="fragments/footer.jspf" %>
